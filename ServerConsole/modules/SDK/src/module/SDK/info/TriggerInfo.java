package module.SDK.info;

/**
 * 触发器信息
 * 
 * @author Demon
 */
public class TriggerInfo {
	public TriggerInfo(){}
	
	/** 触发器 ID */
    public Long trigger_id;
    
    /** 触发器名称 */
    public String name;
    
    /** 告警级别:Information(1),Warning(2),Average(3),High(4),Diaster(5) */
    public Integer severity;
    
    /** 是否启动触发器 */
    public Integer enabled;

    /** 备注 */
    public String memo;
}
