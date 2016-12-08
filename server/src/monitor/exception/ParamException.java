package monitor.exception;

import monitor.service.http.protocol.RetStat;

/**
 * 参数错误异常
 * @author monitor
 *
 */
public class ParamException extends LogicalException {
    
    private static final long serialVersionUID = -2434647353423193324L;
    
    public ParamException(String msg) {
        super(RetStat.ERR_BAD_PARAMS, msg);
    }
}
