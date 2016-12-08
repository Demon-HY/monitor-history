package module.SDK.stat;

public class AclRetStat {
	
	/** 没有查看用户信息权限 */
    public static final String ERR_ACL_NOT_GET_USERINFO = "ERR_ACL_NOT_GET_USERINFO";
    /** 没有删除用户权限 */
    public static final String ERR_ACL_NOT_DELETE_USER = "ERR_ACL_NOT_DELETE_USER";
    
    public static String getMsgByStat(String stat, Object... params) {
        switch (stat) {
        case ERR_ACL_NOT_GET_USERINFO : return String.format("Account '%s' not get userinfo acl.", params);
        case ERR_ACL_NOT_DELETE_USER : return String.format("Account '%s' not delete user acl.", params);
        default : return null;
        }
    }
}