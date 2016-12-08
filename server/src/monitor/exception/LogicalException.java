package monitor.exception;

//@javadoc 逻辑错误异常类

import java.util.HashMap;
import java.util.Map;

/**
 * 逻辑错误异常
 * @author monitor
 *
 */
public class LogicalException extends Exception {
    
    private static final long serialVersionUID = -2247331292377127222L;
    
    public String stat;
    public String errMsg;
    public Map<String, Object> reaultMap;
    
    public LogicalException(String stat, String errMsg) {
        super(stat + "\t" + errMsg);
        this.stat = stat;
        this.errMsg = errMsg;
        reaultMap = new HashMap<String, Object>();
    }
    
}
