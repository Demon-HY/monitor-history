package module.SDK.info;

/**
 * 报警设置信息
 * 
 * @author Demon
 */
public class ActionOperationInfo {
	public ActionOperationInfo(){}
	
	/** 报警设置 ID */
    public Long action_operation_id;
    
    /** 报警 ID */
    public Long action_id;
    
    /** 报警设置名称 */
    public String name;
    
    /** 第 N 次告警 */
    public Integer step;
    
    /** 动作类型：email，sms */
    public String action_type;
    
    /** 
     * 消息格式</br>
     * 默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg} 
     */
    public String msg_format;
}
