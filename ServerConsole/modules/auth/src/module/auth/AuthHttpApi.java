package module.auth;

import javax.servlet.http.Cookie;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.LoginInfo;
import module.SDK.stat.UserRetStat;
import monitor.service.http.protocol.JsonProtocol;
import monitor.service.http.protocol.JsonReq;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;
import monitor.utils.Time;

public class AuthHttpApi {
	private AuthApi authApi;
	
	private static AuthHttpApi authHttpApi;
	private AuthHttpApi(AuthApi authApi) {
		this.authApi = authApi;
	}
	
	public static void init(AuthApi authApi) {
		authHttpApi = new AuthHttpApi(authApi);
	}
	
	public static AuthHttpApi getInst() {
		if (null == authHttpApi) {
			new UnInitilized();
		}
		return authHttpApi;
	}
	
	/********************************************     对外接口               ********************************************/
	/**
	 * 用户登录
	 * 
	 * @param account 
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：手机号/用户名/邮箱<br/>
     * 		必需：YES
     * </blockquote>
	 * @param password 
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：密码<br/>
     * 		必需：YES
     * </blockquote>
	 * @param type 
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：账号类型：手机号(phone)/用户名(name)/邮箱(email)<br/>
     * 		必需：YES
     * </blockquote>
	 * @param tokenAge 
	 * <blockquote>
     * 		类型：整形<br/>
     * 		描述：token 过期时间(单位：毫秒)<br/>
     * 		必需：NO
     * </blockquote>
	 * @param isCookie 
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：是否写入cookie(no,yes)<br/>
     * 		必需：NO
     * </blockquote>
	 * @return UserInfo
	 */
	@ApiGateway.ApiMethod(protocol = JsonProtocol.class)
	public JsonResp login(JsonReq req) throws Exception {
		String account = req.paramGetString("name", true);
		String password = req.paramGetString("password", true);
		String type = req.paramGetString("type", true);
		Long tokenAge = req.paramGetNumber("tokenAge", false, true);
		String isCookie = req.paramGetString("isCookie", false);
		isCookie = null == isCookie ? "yes" : isCookie;
		
		JsonResp resp = new JsonResp(RetStat.OK);
		// 非法账号类型
		if (!type.equals("name") && !type.equals("email") && !type.equals("phone")) {
			resp.stat = UserRetStat.ERR_ILLEGAL_ACCOUNT_TYPE;
			return resp;
		}

		AuthUtils.checkAccount(req.env, type, account);
		
		LoginInfo loginInfo = null;
		loginInfo = authApi.login(req.env, account, password, type, tokenAge);
		
        resp.resultMap.put("token", loginInfo.tokenInfo.token);

        Cookie cookie = new Cookie("token", loginInfo.tokenInfo.token);
        cookie.setPath("/");
        if ("yes".equals(isCookie)) {
            cookie.setMaxAge((int) (loginInfo.tokenInfo.expires.getTime() - loginInfo.tokenInfo.ctime.getTime()));
        }
        resp.addCookie(cookie);
        return resp;
	}

	/**
	 * 验证用户是否已登录
	 * 
	 * @param token
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：token<br/>
     * 		必需：YES
     * </blockquote>
	 * @return rest token的剩余时间，单位毫秒
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp checkLogin(AuthedJsonReq req) throws Exception{
		JsonResp resp = new JsonResp(RetStat.OK);
        Long rest = req.loginInfo.tokenInfo.expires.getTime() - Time.currentTimeMillis();
        resp.resultMap.put("rest", rest);

        return resp;
	}
	
	/**
	 * 重新 fork 一个新的 token
	 * 
	 * @param token
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：token<br/>
     * 		必需：YES
     * </blockquote>
	 * @param tokenAge 
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：新建token的寿命，单位毫秒<br/>
     * 		必需：NO
     * </blockquote>
	 * @return new token
	 */
	@ApiGateway.ApiMethod(protocol = JsonProtocol.class)
	public JsonResp forkToken(JsonReq req) throws Exception{
		String token = req.paramGetString("token", true);
		Long tokenAge = req.paramGetNumber("tokenAge", false, true);
		
		authApi.forkToken(req.env, token, tokenAge);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		return resp;
	}
	
	/**
	 * 退出登录
	 * 
	 * @param token
	 * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：token<br/>
     * 		必需：YES
     * </blockquote>
	 * @return
	 */
	@ApiGateway.ApiMethod(protocol = JsonProtocol.class)
	public JsonResp logout(JsonReq req) throws Exception{
		String token = req.paramGetString("token", true);
		
		authApi.logout(req.env, token);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		return resp;
	}
}
