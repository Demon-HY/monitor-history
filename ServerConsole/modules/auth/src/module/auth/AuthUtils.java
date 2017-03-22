package module.auth;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.ParseException;

import module.SDK.info.UserInfo;
import module.SDK.stat.AuthRetStat;
import monitor.exception.LogicalException;
import monitor.service.http.Env;
import monitor.utils.LanguageUtil;
import monitor.utils.SSHA;
import monitor.utils.XCodeUtil;

public class AuthUtils {
	
	private AuthUtils() {}
	
	/**
     * 检查登录账号
     * 
     * @param type
     *            账号类型
     * @param account
     *            账号
     * @return
     * @throws LogicalException
     */
    public static boolean checkAccount(Env env, String type, String account) throws LogicalException {
        if (!(AuthConfig.LOGINID_NAME.equals(type) || AuthConfig.LOGINID_EMAIL.equals(type) || AuthConfig.LOGINID_PHONE.equals(type))) {
            throw new LogicalException(AuthRetStat.ERR_ILLEGAL_ACCOUNT_TYPE, 
                    LanguageUtil.getInst().getText(AuthRetStat.ERR_ILLEGAL_ACCOUNT_TYPE, env.Language));
        }
        if (AuthConfig.LOGINID_EMAIL.equals(type) && !account.matches("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$")) {
            throw new LogicalException(AuthRetStat.ERR_ILLEGAL_EMAIL_ACCOUNT, 
                    LanguageUtil.getInst().getText(AuthRetStat.ERR_ILLEGAL_EMAIL_ACCOUNT, env.Language));
        }
        if (AuthConfig.LOGINID_PHONE.equals(type) && !account.matches("^1[34578][0-9]{9}")) {
            throw new LogicalException(AuthRetStat.ERR_ILLEGAL_PHONE_ACCOUNT, 
                    LanguageUtil.getInst().getText(AuthRetStat.ERR_ILLEGAL_PHONE_ACCOUNT, env.Language));
        }

        return true;
    }
    
    /**
     * 验证用户密码
     * 
     * @param user 用户信息
     * @param password 密码
     * @return
     * @throws LogicalException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static boolean checkPassword(Env env, UserInfo user, String password)
            throws LogicalException, NoSuchAlgorithmException,
            UnsupportedEncodingException, ParseException {
        if (null == user || password == null) {
            throw new IllegalArgumentException();
        }

        String psw = user.password;

        if (psw == null || psw.length() == 0) {
            throw new LogicalException(AuthRetStat.ERR_USER_INFO_BROKEN,
                    LanguageUtil.getInst().getText(AuthRetStat.ERR_USER_INFO_BROKEN, env.Language));
        }

        String format = "'{'{0}'}'{1}";
        MessageFormat mf = new MessageFormat(format);
        Object[] objs = mf.parse(psw);
        String algorithm = (String) objs[0];
        String shadow = (String) objs[1];

        if (AuthConfig.ALG_MD5.equals(algorithm)) {
            String tmp = XCodeUtil.base64ToBase16(shadow);
            password = XCodeUtil.getMD5(password);
            if (tmp.equals(password)) {
                return true;
            }
        } else if (AuthConfig.ALG_SSHA.equals(algorithm)) {
            return SSHA.verifySaltedPassword(password.getBytes(), psw);
        }

        return false;
    }
}
