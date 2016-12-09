package module.user;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import module.SDK.SdkCenter;
import module.SDK.event.type.UserEvent;
import module.SDK.info.UserInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.IUserApi;
import module.SDK.stat.UserRetStat;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;
import monitor.utils.SSHA;
import monitor.utils.Time;

public class UserApi implements IUserApi {
	
	protected IBeans beans;
	protected UserModel userModel;
    
    public UserApi(IBeans beans, UserModel userModel) throws LogicalException {
    	this.beans = beans;
    	this.userModel = userModel;
    	
    	SdkCenter.getInst().addInterface(IUserApi.name, this);
    }
    
    private static UserApi userApi;
    public static void init(IBeans beans, UserModel userModel) throws LogicalException {
        userApi = new UserApi(beans, userModel);
    }
    
    public static UserApi getInst() throws UnInitilized {
        if (null == userApi) {
            throw new UnInitilized();
        }
        return userApi;
    }
    
    @Override
    public IUserModel getUserModel() {
        return this.userModel;
    }
    
    /**
     * @throws LogicalException 
     * @throws NoSuchAlgorithmException 
     * @throws SQLException 
     * @throws Exception ********************************************************************************************/
    public UserInfo userRegister(Env env, UserInfo userInfo) throws LogicalException, NoSuchAlgorithmException, SQLException  {
        // check username and password
        UserApi.checkPasswordIsLegal(userInfo.password);
        // sha1
        userInfo.password = SSHA.getSaltedPassword(userInfo.password);
        
        UserEvent userEvent = new UserEvent(env, userInfo);
        beans.getEventHub().dispatchEvent(UserEvent.Type.PRE_REGISTER_USER, userEvent);
        
        boolean result = userModel.createUser(userInfo);
        if (result) {
        	userInfo = userModel.findUser(userInfo.name, userInfo.email, userInfo.phone);
        	// login_id
        	beans.getAuthApi().getAuthModel().setLoginId("name", userInfo.name, userInfo.uid);
        } else {
        	return null;
        }
        
        userEvent = new UserEvent(env, userInfo);
        beans.getEventHub().dispatchEvent(UserEvent.Type.POST_REGISTER_USER, userEvent);
        
        return userInfo;
    }
    
    /**
     * 验证密码有效性
     * @param password
     * @return
     * @throws LogicalException 
     * @throws Exception
     */
    public static boolean checkPasswordIsLegal(String password) throws LogicalException  {
        if(password == null || password.length() == 0 || password.matches("[u4e00-u9fa5]"))
            throw new LogicalException(UserRetStat.ERR_ILLEGAL_PASSWORD, "illegal password :" + password);
        return true;
    }

    /**
     * 设置用户单一属性
     * @param env
     * @param uid
     * @param key 属性的 KEY
     * @param value 属性值
     * @return 
     * @throws LogicalException 
     * @throws SQLException 
     */
	public void setUserAttr(Env env, Long uid, String key, Object value) throws LogicalException, SQLException {
		UserInfo userInfo = getUserInfoByUid(uid);
		UserEvent ev = new UserEvent(env, userInfo, key, value);
        this.beans.getEventHub().dispatchEvent(UserEvent.Type.PRE_SET_USER_ATTR, ev);
        if (!ev.isContinue) {
            throw new LogicalException(ev.stat, ev.breakReason);
        }
        
        boolean flag = userModel.setUserAttr(uid, key, value);

        ev.setAttrFlag = flag; // 设置属性是否成功
        this.beans.getEventHub().dispatchEvent(UserEvent.Type.POST_SET_USER_ATTR, ev);
	}
	
	public void setUserAttr(Env env, UserInfo userInfo) throws LogicalException, SQLException {
		UserEvent ev = new UserEvent(env, userInfo);
        this.beans.getEventHub().dispatchEvent(UserEvent.Type.PRE_SET_USER_ATTR, ev);
        if (!ev.isContinue) {
            throw new LogicalException(ev.stat, ev.breakReason);
        }
        
        boolean flag = userModel.setUserAttrs(userInfo);

        ev.setAttrFlag = flag; // 设置属性是否成功
        this.beans.getEventHub().dispatchEvent(UserEvent.Type.POST_SET_USER_ATTR, ev);
	}

	public UserInfo getUserInfoByUid(Long uid) throws SQLException {
		UserInfo userInfo = userModel.getUserInfoByUid(uid);
		return userInfo;
	}
	
	/**
     * 检查用户状态
     * 
     * @param user 用户信息
     * @throws LogicalException
     * @throws SQLException 
     */
    public void checkUserStatus(Env env, UserInfo user) throws SQLException, LogicalException {
        
        unLockForWrongPsw(env, user);
        
        int status = user.status;
        switch (status) {
        case UserConfig.STATUS_NORMAL :
            break;
        case UserConfig.STATUS_LOCK :
            throw new LogicalException(UserRetStat.ERR_USER_LOCKED, "" + user.uid);
        }
    }
    
    /**
     * 解锁密码错误的用户
     * 
     * @param env
     * @param user
     * @throws SQLException
     * @throws LogicalException
     */
    public void unLockForWrongPsw(Env env, UserInfo user) throws SQLException, LogicalException {
        Object obj = user.getAttr(UserConfig.LOCK_PSW);
        if (obj != null && Time.currentTimeMillis() > Long.parseLong(obj.toString())) {
            user.delAttr(UserConfig.LOCK_PSW);
            setUserAttr(env, user.uid, UserConfig.USER_ATTR_STATUS, UserConfig.STATUS_NORMAL);
        }
    }

    /**
	 * 判断用户名是否有效及是否已被注册
	 * @param username 已存在用户名或者将要注册用户名
	 * @return true(用户名未被注册),false(用户名已被注册)
     * @throws SQLException 
	 */
	public boolean isVaildUsername(Env env, String userName) throws SQLException {
		UserInfo userInfo = this.userModel.findUser(userName, null, null);
		return userInfo == null ? true : false;
	}
}
