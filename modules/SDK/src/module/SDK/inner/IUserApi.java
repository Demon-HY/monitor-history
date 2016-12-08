package module.SDK.inner;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import module.SDK.info.UserInfo;
import monitor.exception.LogicalException;
import monitor.service.http.Env;

public interface IUserApi {
	
    public static final String name = "IUserApi";
    
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
    public void setUserAttr(Env env, Long uid, String key, Object value)throws LogicalException, SQLException;
    /**
     * 用户注册
     * @param env
     * @param userInfo 用户信息
     * @return
     * @throws Exception
     */
    public UserInfo userRegister(Env env, UserInfo userInfo) throws LogicalException, NoSuchAlgorithmException, SQLException;
    public UserInfo getUserInfoByUid(Long uid) throws SQLException;
    public void setUserAttr(Env env, UserInfo userInfo) throws LogicalException, SQLException;
    /**
     * 检查用户状态
     * 
     * @param user 用户信息
     * @throws LogicalException
     * @throws SQLException 
     */
    public void checkUserStatus(Env env, UserInfo user) throws SQLException, LogicalException;
    /**
     * 解锁密码错误的用户,判断用户锁定的时间来解锁
     * 
     * @param env
     * @param user
     * @throws SQLException
     * @throws LogicalException
     */
    public void unLockForWrongPsw(Env env, UserInfo user) throws SQLException, LogicalException;
    
    
    public IUserModel getUserModel();
    public interface IUserModel {
        /**
         * 创建用户
         * @param user
         * @return
         * @throws SQLException
         */
        public boolean createUser(UserInfo user) throws SQLException;
        /**
         * 设置用户单一属性
         * @param uid
         * @param key
         * @param value
         * @return
         * @throws SQLException
         */
        public boolean setUserAttr(Long uid, String key, Object value) throws SQLException;
        /**
         * 批量设置用户属性
         * @param userInfo
         * @return
         * @throws SQLException
         */
        public boolean setUserAttrs(UserInfo userInfo) throws SQLException;
        public UserInfo getUserInfoByUid(Long uid) throws SQLException;
    }

}
