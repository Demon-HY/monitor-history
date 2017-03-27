package module.user;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.UserInfo;
import module.SDK.stat.UserRetStat;
import monitor.service.http.protocol.JsonProtocol;
import monitor.service.http.protocol.JsonReq;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.utils.LanguageUtil;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;

public class UserHttpApi {

	protected UserApi userApi;
	
	public UserHttpApi(UserApi userApi){
		this.userApi = userApi;
	}
	
	private static UserHttpApi userHttpApi;
	public static void init(UserApi userApi) {
		userHttpApi = new UserHttpApi(userApi);
	}
	
	public static UserHttpApi getInst() throws UnInitilized {
		if (userHttpApi == null) {
			throw new UnInitilized();
		}
		return userHttpApi;
	}
	
	/*************************************************************************************************/
	/**
	 * 用户注册
	 * 
	 * @param name
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：用户名<br/>
     * 		必需：NO
     * </blockquote>
	 * @param email
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：邮箱<br/>
     * 		必需：NO
     * </blockquote>
	 * @param phone
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：手机号<br/>
     * 		必需：NO
     * </blockquote>
	 * @param password 
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：密码<br/>
     * 		必需：YES
     * </blockquote>
	 * @param nick 
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：昵称<br/>
     * 		必需：NO
     * </blockquote>
	 * @param qq 
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：QQ<br/>
     * 		必需：NO
     * </blockquote>
	 * @param exattr 
	 * <blockquote>
     * 		类型：Map<br/>
     * 		描述：额外属性<br/>
     * 		必需：NO
     * </blockquote> 
	 * @return UserInfo
	 */
	@ApiGateway.ApiMethod(protocol = JsonProtocol.class)
	public JsonResp userRegister(JsonReq req) throws Exception {
		String name = req.paramGetString("name", false);
		String email = req.paramGetString("eamil", false);
		String phone = req.paramGetString("phone", false);
		String password = req.paramGetString("password", true);
		password = new String(Base64.decodeBase64(password));
		String nick = req.paramGetString("name", false);
		String qq = req.paramGetString("qq", false);
		Map<String, Object> exattr = req.paramGetMap("exattr", false, String.class, Object.class, true);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		UserInfo userInfo = new UserInfo(name, phone, email, nick, password, qq, UserConfig.defaultUserType, exattr);
		userInfo = this.userApi.userRegister(req.env, userInfo);
		if (userInfo == null) {
			resp.stat = UserRetStat.ERR_ADD_LOGIN_ID_FAILED;
		}
		
        resp.resultMap.put("UserInfo", userInfo);
		return resp;
	}
	
	/**
	 * 获取 token 中存储的用户信息
	 * 
	 * @param token
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token<br/>
     * 		必需：YES
     * </blockquote>
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp getUserInfo(AuthedJsonReq req) throws Exception {
        JsonResp resp = new JsonResp(RetStat.OK);
        req.loginInfo.userInfo.password = "";
        resp.resultMap.put("user", req.loginInfo.userInfo);
        
        return resp;
	}
	
	/**
	 * 判断用户名是否有效及是否已被注册
	 * @param username
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：已存在用户名或者将要注册用户名<br/>
     * 		必需：YES
     * </blockquote>
	 * @return
	 * @throws Exception
	 */
	@ApiGateway.ApiMethod(protocol = JsonProtocol.class)
	public JsonResp isVaildUsername(JsonReq req) throws Exception {
		String userName = req.paramGetString("username", true);
		if (userName == null || userName.length() < 1) {
			throw new LogicalException(RetStat.ERR_BAD_PARAMS, "uid == null || uid < 1");
		}
		JsonResp resp = new JsonResp(RetStat.OK);
		
		boolean isVaild = this.userApi.isVaildUsername(req.env, userName);
		if (isVaild) {
			return resp;
		} else {
			resp.stat = "ERR_ACCOUNT_EXIST";
			resp.resultMap.put("errMsg", LanguageUtil.getInst().getText(UserRetStat.ERR_ACCOUNT_EXIST, req.env.Language));
		}
		
		return resp;
	}
	
//	/**
//	 * 设置用户信息
//	 * 
//	 * @param token
//	 * <blockquote>
//     * 		类型：字符型<br/>
//     * 		描述：token<br/>
//     * 		必需：YES
//     * </blockquote>
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp setUserInfo(AuthedJsonReq req) throws Exception {
//		return null;
//	}
	
//	/**
//	 * 更新用户昵称
//	 * 
//	 * @param token
//	 * <blockquote>
//     * 		类型：字符型<br/>
//     * 		描述：token<br/>
//     * 		必需：YES
//     * </blockquote>
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp updateNickName(AuthedJsonReq req) throws Exception {
//		return null;
//	}
	
//	/**
//	 * 更新密码
//	 * 
//	 * @param token
//	 * <blockquote>
//     * 		类型：字符型<br/>
//     * 		描述：token<br/>
//     * 		必需：YES
//     * </blockquote>
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp updatePassword(AuthedJsonReq req) throws Exception {
//		return null;
//	}
	
//	/**
//	 * 找回密码
//	 * 
//	 * @param token
//	 * <blockquote>
//     * 		类型：字符型<br/>
//     * 		描述：token<br/>
//     * 		必需：YES
//     * </blockquote>
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp retrievePassword(AuthedJsonReq req) throws Exception {
//		return null;
//	}
	
//	/**
//	 * 添加用户收货地址
//	 * 
//	 * @param token
//	 * <blockquote>
//     * 		类型：字符型<br/>
//     * 		描述：token<br/>
//     * 		必需：YES
//     * </blockquote>
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp addDeliveryAddress(AuthedJsonReq req) throws Exception {
//		return null;
//	}
	
//	/**
//	 * 修改用户收货地址
//	 * 
//	 * @param token
//	 * <blockquote>
//     * 		类型：字符型<br/>
//     * 		描述：token<br/>
//     * 		必需：YES
//     * </blockquote>
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp updateDeliveryAddress(AuthedJsonReq req) throws Exception {
//		return null;
//	}
	
//	/**
//	 * 获取用户收货地址
//	 * 
//	 * @param token
//	 * <blockquote>
//     * 		类型：字符型<br/>
//     * 		描述：token<br/>
//     * 		必需：YES
//     * </blockquote>
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp getDeliveryAddress(AuthedJsonReq req) throws Exception {
//		return null;
//	}
	
//	/**
//	 * 设置用户头像，上传用户头像数据
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp setUserImage(AuthedJsonReq req) throws Exception {
//		return null;
//	}

//	/**
//	 * 裁减用户头像
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp cutUserImage(AuthedJsonReq req) throws Exception {
//		return null;
//	}

//	/**
//	 * 获取用户头像数据
//	 */
//	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
//	public JsonResp getUserImage(AuthedJsonReq req) throws Exception {
//		return null;
//	}
	
}
