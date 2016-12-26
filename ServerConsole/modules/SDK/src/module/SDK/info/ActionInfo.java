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
    
    /** 告警间隔(s) */
    public Long interval;
    
    /** 是否在故障恢复后发送通知消息 */
    public Integer notice;
    
    /** 标题 */
    public String subject;
    
    /** 正文 */
    public String message;
    
    /** 启用报警 */
    public Integer enabled;
}

/**
 * 报警设置信息
 * 
 * @author Demon
 */
class ActionOperationInfo {
	public ActionOperationInfo(){}
	
	/** 报警设置 ID */
    public Long action_operation_id;
    
    /** 报警 ID */
    public Long action_id;
    
    /** 报警设置名 */
    public String name;
    
    /** 第n次告警 */
    public Integer step;
    
    /** 动作类型：email，sms */
    public String action_type;
    
    /** 
     * 消息格式</br>
     * 默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg} 
     */
    public String msg_format;
}
