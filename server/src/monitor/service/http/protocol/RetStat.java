package monitor.service.http.protocol;

import java.util.HashMap;
import java.util.Map;

import monitor.service.http.Env;

public class RetStat implements ErrTextFormat {
    
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
    
	public static final Map<String, String> ERR_TEXT;
	static {
	    ERR_TEXT = new HashMap<String, String>();
	    
	    ERR_TEXT.put(ERR_BAD_PARAMS, "参数错误");
	    ERR_TEXT.put(ERR_FORBIDDEN, "无访问权限");
	    ERR_TEXT.put(ERR_INVALID_JSON, "非法JSON串");
	    ERR_TEXT.put(ERR_NOT_FOUND, "资源不存在");
	    ERR_TEXT.put(ERR_READ_POST_EXCEPTION, "无法解析post数据");
	    ERR_TEXT.put(ERR_STAT_NOT_SET, "没有返回码");
	    ERR_TEXT.put(ERR_SERVER_EXCEPTION, "服务端异常");
	    ERR_TEXT.put(ERR_EVENT_INTERRUPT, "事件中断");
	    ERR_TEXT.put(ERR_USER_NOT_LOGIN, "用户没有登录");
	    ERR_TEXT.put(ERR_MAILBOX_FULL, "邮件队列已满");
	    ERR_TEXT.put(ERR_OPERATION_NOT_SUPPORTED, "不支持该操作");
	}
    
    public String getErrText(Env env, String stat) {
        return ERR_TEXT.get(stat);
    }
	
}
