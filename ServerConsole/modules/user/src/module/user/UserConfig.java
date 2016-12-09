package module.user;

public class UserConfig {
	
    /** 默认用户类型是 type=1(客户) */
    public static final int defaultUserType = 1;
    
    /** 用户状态 */
    public static final String USER_ATTR_STATUS = "status";
    
    /** STATUS_NORMAL: 正常 */
    public static final int STATUS_NORMAL = 1;
    
    /** STATUS_LOCK: 锁定 */
    public static final int STATUS_LOCK = 2;
    
    /** STATUS_DELETE: 已删除  */
    public static final int STATUS_DELETE = 3;
    
    public static final String LOCK_PSW = "innerWrongPsw";
}
