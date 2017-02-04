package module.SDK.info;

/**
 * 报警信息
 * 
 * @author Demon
 */
public class ActionInfo {
	public ActionInfo(){}

	/** 报警 ID */
    public Long action_id;
    
    /** 报警名 */
    public String name;
    
    /** 报警间隔(s) */
    public Long interval;
    
    /** 是否在故障恢复后发送通知消息 */
    public Integer notice;
    
    /** 标题 */
    public String subject;
    
    /** 正文 */
    public String message;
    
    /** 启用报警 */
    public Integer enabled;

	public ActionInfo(String name, Long interval, Integer notice, String subject, String message, Integer enabled) {
		this.name = name;
		this.interval = interval;
		this.notice = notice;
		this.subject = subject;
		this.message = message;
		this.enabled = enabled;
	}
	
	public ActionInfo(Long action_id, String name, Long interval, Integer notice, String subject, String message, Integer enabled) {
		this.action_id = action_id;
		this.name = name;
		this.interval = interval;
		this.notice = notice;
		this.subject = subject;
		this.message = message;
		this.enabled = enabled;
	}
}