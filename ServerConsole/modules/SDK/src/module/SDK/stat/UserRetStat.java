package module.SDK.stat;

import monitor.service.http.protocol.RetStat;

/**
 * @author monitor
 */
public class UserRetStat extends RetStat {

    /** 该账号已被使用 */
    public static final String ERR_ACCOUNT_EXIST = "ERR_ACCOUNT_EXIST";

    /** 手机号已经被绑定 */
    public static final String ERR_PHONE_ALREADY_BOUND = "ERR_PHONE_ALREADY_BOUND";

    /** 请不要重复发送验证码 */
    public static final String ERR_SEND_CODE_FREQUENTLY = "ERR_SEND_CODE_FREQUENTLY";

    /** 验证码错误 */
    public static final String ERR_CODE = "ERR_CODE";

    /** 清除用户数据失败 */
    public static final String ERR_CLEAN_ACCOUNT_FAILED = "ERR_CLEAN_ACCOUNT_FAILED";

    /** 注册登录信息失败 */
    public static final String ERR_ADD_LOGIN_ID_FAILED = "ERR_ADD_LOGIN_ID_FAILED";

    /** 账号不存在 */
    public static final String ERR_NO_SUCH_ACCOUNT = "ERR_NO_SUCH_ACCOUNT";

    /** 用户不存在 */
    public static final String ERR_USER_NOT_FOUND = "ERR_USER_NOT_FOUND";

    /** 用户信息损坏 */
    public static final String ERR_USER_INFO_BROKEN = "ERR_USER_INFO_BROKEN";

    /** 密码错误 */
    public static final String ERR_INVALID_PASSWORD = "ERR_INVALID_PASSWORD";

    /** 密码过期 */
    public static final String ERR_PASSWORD_EXPIRED = "ERR_PASSWORD_EXPIRED";

    /** 非法账号类型 */
    public static final String ERR_ILLEGAL_ACCOUNT_TYPE = "ERR_ILLEGAL_ACCOUNT_TYPE";

    /** 非法邮箱 */
    public static final String ERR_ILLEGAL_EMAIL_ACCOUNT = "ERR_ILLEGAL_EMAIL_ACCOUNT";

    /** 非法手机号 */
    public static final String ERR_ILLEGAL_PHONE_ACCOUNT = "ERR_ILLEGAL_PHONE_ACCOUNT";

    /** 错误码：非法密码 */
    public static final String ERR_ILLEGAL_PASSWORD = "ERR_ILLEGAL_PASSWORD";

    /** 用户还未登录 */
    public static final String ERR_TOKEN_NOT_FOUND = "ERR_TOKEN_NOT_FOUND";

    /** 用户登录超时 */
    public static final String ERR_TOKEN_EXPIRED = "ERR_TOKEN_EXPIRED";

    /** 验证码过期 */
    public static final String ERR_VALIDATE_CODE_EXPIRED = "ERR_VALIDATE_CODE_EXPIRED";

    /** 验证码错误 */
    public static final String ERR_INVALID_VALIDATE_CODE = "ERR_INVALID_VALIDATE_CODE";

    /** 登录的用户与新建token用户不为一个用户 */
    public static final String ERR_TOKEN_UID_MISMATCHING = "ERR_TOKEN_UID_MISMATCHING";

    /** 错误码：用户已被锁定 */
    public static final String ERR_USER_LOCKED = "ERR_USER_LOCKED";
}
