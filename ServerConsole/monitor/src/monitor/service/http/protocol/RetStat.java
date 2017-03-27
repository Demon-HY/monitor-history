package monitor.service.http.protocol;

public class RetStat {
    
	public static final String OK = "OK";
	
	/**
     * 错误码：参数错误
     */
	public static final String ERR_BAD_PARAMS = "ERR_BAD_PARAMS";
	
	/**
     * 错误码：无访问权限
     */
	public static final String ERR_FORBIDDEN = "ERR_FORBIDDEN";
	
	/**
     * 错误码：非法JSON串
     */
	public static final String ERR_INVALID_JSON = "ERR_INVALID_JSON";
	
	/**
     * 错误码：资源不存在
     */
	public static final String ERR_NOT_FOUND = "ERR_NOT_FOUND";
	
	/**
     * 错误码：无法解析post数据
     */
	public static final String ERR_READ_POST_EXCEPTION = "ERR_READ_POST_EXCEPTION";
	
	/**
     * 错误码：没有返回码
     */
	public static final String ERR_STAT_NOT_SET = "ERR_STAT_NOT_SET";
	
	/**
     * 错误码：服务端异常
     */
	public static final String ERR_SERVER_EXCEPTION = "ERR_SERVER_EXCEPTION";
	
	/**
     * 错误码：事件中断
     */
	public static final String ERR_EVENT_INTERRUPT = "ERR_EVENT_INTERRUPT";
	
	/**
     * 错误码：用户没有登录
     */
	public static final String ERR_USER_NOT_LOGIN = "ERR_USER_NOT_LOGIN";
	
	/**
     * 错误码：邮件队列已满
     */
	public static final String ERR_MAILBOX_FULL = "ERR_MAILBOX_FULL";
	
	/**
     * 错误码：不支持该操作
     */
    public static final String ERR_OPERATION_NOT_SUPPORTED = "ERR_OPERATION_NOT_SUPPORTED";
    
}
