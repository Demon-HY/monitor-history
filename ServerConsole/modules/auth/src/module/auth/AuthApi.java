package module.auth;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;

import javax.security.auth.login.LoginException;

import module.SDK.SdkCenter;
import module.SDK.event.type.PostLoginEvent;
import module.SDK.event.type.PreLoginEvent;
import module.SDK.info.LoginInfo;
import module.SDK.info.TokenInfo;
import module.SDK.info.UserInfo;
import module.SDK.inner.IAuthApi;
import module.SDK.inner.IBeans;
import module.SDK.stat.AuthRetStat;
import module.SDK.stat.UserRetStat;
import monitor.service.http.protocol.RetStat;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;
import monitor.utils.Time;

public class AuthApi implements IAuthApi {

	protected IBeans beans;
	protected AuthModel authModel;
	
	private static AuthApi authApi;
	private AuthApi(IBeans beans, AuthModel authModel) throws LogicalException {
		this.beans = beans;
		this.authModel = authModel;
		
		SdkCenter.getInst().addInterface(IAuthApi.name, this);
	}
	
	public static void init(IBeans beans, AuthModel authModel) throws LogicalException {
		authApi = new AuthApi(beans, authModel);
	}
	
	public static AuthApi getInst() throws UnInitilized {
		if (null == authApi) {
			throw new UnInitilized();
		}
		return authApi;
	}
	
	/*******************************************************************************************/
	public IAuthModel getAuthModel() {
		return this.authModel;
	}
	
	public LoginInfo login(Env env, String account, String password, String type, Long tokenAge) 
			throws LoginException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, ParseException, LogicalException {
		if (tokenAge == null) {
            tokenAge = (long) AuthConfig.defaultTokenAge;
        }
		
		LoginInfo loginInfo = null;
		LogicalException logincalException = null;
		
		try {
			// 发送登录前事件
			PreLoginEvent preLoginEvent = new PreLoginEvent(env, account,
			        password, type, tokenAge);
			this.beans.getEventHub().dispatchEvent(PreLoginEvent.Type.PRE_LOGIN, preLoginEvent);

			if (!preLoginEvent.isContinue) {
			    throw new LoginException(preLoginEvent.breakReason);
			}
			if (preLoginEvent.loginInfo == null && preLoginEvent.logicalException == null) {
				Long uid = this.authModel.checkLoginId(type, account);
				if (uid == null) {
                    throw new LogicalException(UserRetStat.ERR_NO_SUCH_ACCOUNT,
                    		UserRetStat.getMsgByStat(
                            		UserRetStat.ERR_NO_SUCH_ACCOUNT, account));
                }
				loginInfo = login(env, uid, account, password, type, tokenAge);
			} else {
                loginInfo = preLoginEvent.loginInfo;
                logincalException = preLoginEvent.logicalException;
            }
		} catch (LogicalException e) {
            logincalException = e;
        }
		// 发送登录后事件
		PostLoginEvent postLoginEvent = new PostLoginEvent(env, account,
                password, type, tokenAge, logincalException, loginInfo);
		this.beans.getEventHub().dispatchEvent(PostLoginEvent.Type.POST_LOGIN, postLoginEvent);
        
        if (postLoginEvent.loginInfo != null) {
            return postLoginEvent.loginInfo;
        } else if (postLoginEvent.logicalException != null) {
            throw postLoginEvent.logicalException;
        } else {
            throw new LogicalException(RetStat.ERR_SERVER_EXCEPTION,
                    "Logical exception not set.");
        }
	}

	private LoginInfo login(Env env, Long uid, String name, String password, String type, Long tokenAge) throws SQLException, LogicalException, NoSuchAlgorithmException, UnsupportedEncodingException, ParseException {
		UserInfo user = this.beans.getUserApi().getUserModel().getUserInfoByUid(uid);
		if (user == null) {
			throw new LogicalException(UserRetStat.ERR_NO_SUCH_ACCOUNT,
					UserRetStat.getMsgByStat(UserRetStat.ERR_NO_SUCH_ACCOUNT, name));
		}
		// check user status
		beans.getUserApi().checkUserStatus(env, user);
		
		if (!AuthUtils.checkPassword(user, password)) {
			throw new LogicalException(UserRetStat.ERR_INVALID_PASSWORD,
					UserRetStat.getMsgByStat(UserRetStat.ERR_INVALID_PASSWORD, name));
		}
        if (tokenAge == null) {
            tokenAge = (long) AuthConfig.defaultTokenAge;
        }
        TokenInfo tokenInfo = TokenInfo.newToken(uid, tokenAge, env.ip,
                env.device);
        this.authModel.addToken(tokenInfo);
		
        return new LoginInfo(tokenInfo, user);
	}
	
//    public Long checkLoginId(String type, String value) throws SQLException {
//    	Long uid = this.authModel.checkLoginId(type, value);
//        return uid;
//    }

    public LoginInfo checkLogin(Env env, String token) throws SQLException, LogicalException {
    	if (null == token) {
            throw new IllegalArgumentException();
        }
    	
    	TokenInfo tokenInfo = this.authModel.getTokenInfo(token);
        if (tokenInfo == null) {
            throw new LogicalException(AuthRetStat.ERR_TOKEN_NOT_FOUND, token);
        }
        if (tokenInfo.expires.getTime() < Time.currentTimeMillis()) {
            throw new LogicalException(AuthRetStat.ERR_TOKEN_EXPIRED, token);
        }

        UserInfo userInfo = this.beans.getUserApi().getUserInfoByUid(tokenInfo.uid);

        LoginInfo loginInfo = new LoginInfo(tokenInfo, userInfo);

        env.logParam("auth_uid", userInfo.uid);
        
        return loginInfo;
    }
    
	public TokenInfo forkToken(Env env, String token, Long tokenAge) throws SQLException, LogicalException {
		if (null == token) {
            throw new IllegalArgumentException();
        }
		if (tokenAge == null) {
            tokenAge = (long) AuthConfig.defaultTokenAge;
        }
		
		TokenInfo tokenInfo = this.authModel.getTokenInfo(token);
        if (tokenInfo == null) {
            throw new LogicalException(AuthRetStat.ERR_TOKEN_NOT_FOUND, token);
        }
        
        tokenInfo = TokenInfo.newToken(tokenInfo.uid, tokenAge, tokenInfo.ip, tokenInfo.device);
        return tokenInfo;
	}
	
	public boolean logout(Env env, String token) throws SQLException, LogicalException {
		if (null == token) {
            throw new IllegalArgumentException();
        }
		TokenInfo tokenInfo = this.authModel.getTokenInfo(token);
		if (tokenInfo == null) {
            throw new LogicalException(AuthRetStat.ERR_TOKEN_NOT_FOUND, token);
        }
		
		return this.authModel.deleteToken(tokenInfo.token);
	}
}
