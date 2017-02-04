package module.SDK.info;

/**
 * 报警设置信息
 * 
 * @author Demon
 */
public class OperationInfo {
	public OperationInfo(){}

	/** 报警设置 ID */
    public Long operation_id;
    
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
    

	public OperationInfo(String name, Integer step, String action_type, String msg_format) {
		this.name = name;
		this.step = step;
		this.action_type = action_type;
		this.msg_format = msg_format;
	}
	
	public OperationInfo(Long operation_id, String name, Integer step, String action_type, String msg_format) {
		this.operation_id = operation_id;
		this.name = name;
		this.step = step;
		this.action_type = action_type;
		this.msg_format = msg_format;
	}
}
