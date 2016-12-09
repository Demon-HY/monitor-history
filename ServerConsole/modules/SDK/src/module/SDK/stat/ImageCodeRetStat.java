package module.SDK.stat;

/**
 * @author monitor
 */
public class ImageCodeRetStat {
    
    /** 验证码过期 */
    public static final String ERR_VALIDATE_CODE_EXPIRED = "ERR_VALIDATE_CODE_EXPIRED";
    /** 验证码错误 */
    public static final String ERR_INVALID_VALIDATE_CODE = "ERR_INVALID_VALIDATE_CODE";
    
    public static String getMsgByStat(String stat, Object... params) {
        switch (stat) {
        	default : return null;
        }
    }
}