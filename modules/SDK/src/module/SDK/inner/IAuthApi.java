package module.SDK.inner;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;

import javax.security.auth.login.LoginException;

import module.SDK.info.LoginInfo;
import module.SDK.info.TokenInfo;
import monitor.exception.LogicalException;
import monitor.service.http.Env;

public interface IAuthApi {

	public static final String name = "IAuthApi";
	
	/**
	 * 用户登录
	 * @param account 手机号/用户名/邮箱
	 * @param password 密码
	 * @param type 账号类型：手机号(phone)/用户名(name)/邮箱(email)
	 * @param tokenAge token 过期时间(单位：毫秒)
	 * @param isCookie 是否写入cookie(no,yes)
	 * @return UserInfo
	 * @throws LoginException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, ParseException, LogicalException
	 */
	public LoginInfo login(Env env, String account, String password, String type, Long tokenAge) 
			throws LoginException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, ParseException, LogicalException;
	/**
	 * 退出登录
	 * @param env
	 * @param token 字符串
	 * @return
	 * @throws SQLException, LogicalException
	 */
	public boolean logout(Env env, String token) throws SQLException, LogicalException;
	/**
	 * 验证用户是否已登录
	 * @param env
	 * @param token 字符串
	 * @return
	 * @throws SQLException, LogicalException
	 */
	public LoginInfo checkLogin(Env env, String token) throws SQLException, LogicalException;
	/**
	 * 重新 fork 一个新的 token
	 * @param token
	 * @param tokenAge 新建token的寿命，单位毫秒
	 * @return TokenInfo
	 * @throws SQLException, LogicalException
	 */
	public TokenInfo forkToken(Env env, String token, Long tokenAge) throws SQLException, LogicalException;
	
	public IAuthModel getAuthModel();
    public interface IAuthModel {
    	
    	public boolean addToken(TokenInfo tokenInfo) throws SQLException;
    	public TokenInfo getTokenInfo(String token) throws SQLException;
    	/***********************************************************************/
    	/**
    	 * 验证登录 Id
    	 * @return 用户 uid
    	 * @throws SQLException
    	 */
    	public Long checkLoginId(String type, String value) throws SQLException;
    	public boolean setLoginId(String type, String value, Long uid) throws SQLException;
    }
}
