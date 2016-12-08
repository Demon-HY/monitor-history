package module.SDK.http;

import java.lang.reflect.Method;

import module.SDK.SdkCenter;
import module.SDK.event.type.PostCheckLoginEvent;
import module.SDK.event.type.PreCheckLoginEvent;
import module.SDK.info.LoginInfo;
import module.SDK.info.UserInfo;
import module.SDK.inner.IAuthApi;
import module.SDK.inner.IEventHub;
import monitor.Config;
import monitor.service.http.protocol.JsonProtocol;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.exception.LogicalException;
import monitor.service.http.Env;

public class AuthedJsonProtocol extends JsonProtocol {

    /**
     * 允许无token或无效的token
     */
    public static final int ALLOW_ANONYMOUS = 0x00000010;

    private static IAuthApi s_authApi;

    private static IEventHub s_eventHub;

    public static class LoginError extends LogicalException {

        private static final long serialVersionUID = -5377829441919519615L;

        public String stat;

        public String msg;

        public LoginError(String stat, String msg) {
            super(stat, msg);
            this.stat = stat;
            this.msg = msg;
        }
    }

    @Override
    public void process(Env env, Object apiSuite, Method m) throws Exception {
        AuthedJsonReq req = new AuthedJsonReq(env);

        // Parse query string
        parseQueryString(env, req);

        // Parse cookie
        parseCookie(env, req);

        // Parse request body as JSON to Java Map
        if (!parseBody(env, req)) {
            return;
        }

        // Check login
        LoginError loginError = checkLogin(req, m);
        if (loginError != null && (this.option & ALLOW_ANONYMOUS) == 0) {
            env.getLogger().err("", loginError);
            sendJsonResponse(env, loginError.stat, null, null, loginError.msg, null);
            return;
        }

        Object uidObj = env.sticker.get("uid");
        UserInfo userInfo = req.loginInfo == null ? null : req.loginInfo.userInfo;
        if (null != userInfo) {
            if (null == uidObj)
                env.sticker.put("uid", userInfo.uid);
        }

        // Invoke
        JsonResp resp = invoke(env, req, apiSuite, m);
        if (resp == null) {
            return;
        }

        // Serialize result to JSON
        sendJsonResponse(env, resp.stat, null, resp.resultMap);
    }

    /**
     * 检查用户是否登录
     * 
     * @param env
     * @param req
     * @param m
     *            处理请求的方法
     * @return LoginError的实例
     * @throws Exception
     */
    protected LoginError checkLogin(AuthedJsonReq req, Method m) throws Exception {
        PreCheckLoginEvent preCheckLoginEvent = new PreCheckLoginEvent(req.env, req.params, req.loginInfo);
        s_eventHub.dispatchEvent(PreCheckLoginEvent.Type.PRE_CHECK_LOGIN, preCheckLoginEvent);

        if (!preCheckLoginEvent.isContinue) {
            return new LoginError(preCheckLoginEvent.logicalException.stat, preCheckLoginEvent.logicalException.errMsg);
        } else if (preCheckLoginEvent.loginInfo != null) {
            req.loginInfo = preCheckLoginEvent.loginInfo;
            req.env.sticker.put("uid", req.loginInfo.userInfo.uid);
            req.env.sticker.put("token", req.loginInfo.tokenInfo.token);
        }

        if (!preCheckLoginEvent.thirdCheck) {
            return defaultCheckLogin(req, m);
        }

        PostCheckLoginEvent postCheckLoginEvent = new PostCheckLoginEvent(req.env, req.params, req.loginInfo);
        s_eventHub.dispatchEvent(PostCheckLoginEvent.Type.POST_CHECK_LOGIN, postCheckLoginEvent);

        return null;
    }

    private static final String ERR_TOKEN_NOT_FOUND = "ERR_TOKEN_NOT_FOUND";

    private LoginError defaultCheckLogin(AuthedJsonReq req, Method m) throws Exception {
        Object _token = req.params.get("token");
        if (_token == null) {
            return new LoginError(ERR_TOKEN_NOT_FOUND, "Json param 'token' required.");
        }

        if (!(_token instanceof String)) {
            return new LoginError(ERR_TOKEN_NOT_FOUND, "Param 'token' should be string.");
        }

        String token = (String) _token;

        if (token.length() == 0) {
            return new LoginError(ERR_TOKEN_NOT_FOUND, "Param 'token' shouldn't be empty.");
        }

        try {
            LoginInfo loginInfo = s_authApi.checkLogin(req.env, token);
            req.loginInfo = loginInfo;
            Long uid = loginInfo.userInfo.uid;
            req.env.logParam("token", token.substring(0, 5));
            req.env.logParam("uid", uid);
            req.env.sticker.put("uid", uid);
            req.env.sticker.put("token", req.loginInfo.tokenInfo.token);
            req.env.sticker.put("account", req.loginInfo.userInfo.getAccount());

        } catch (LogicalException e) {

            return new LoginError(e.stat, e.errMsg);
        } catch (Exception e) {
            req.env.getLogger().err("checkLogin exception", e);
            return new LoginError(RetStat.ERR_SERVER_EXCEPTION, "Check login exception.");
        }

        return null;
    }

    @Override
    public boolean isValidMethod(Method m) {
        Class<?>[] params = m.getParameterTypes();
        if (!(params.length == 1 && params[0].equals(AuthedJsonReq.class))) {
            return false;
        }

        if (m.getReturnType() == JsonResp.class) {
            return true;
        }
        return false;
    }

    /* ------------------------------------------------------------ */
    public static void init() throws LogicalException {
        s_authApi = (IAuthApi) SdkCenter.getInst().queryInterface(IAuthApi.name,
                SdkCenter.ToString() + "InnerKey" + Config.ToString());
        s_eventHub = (IEventHub) SdkCenter.getInst().queryInterface(IEventHub.name,
                SdkCenter.ToString() + "InnerKey" + Config.ToString());
    }
}
