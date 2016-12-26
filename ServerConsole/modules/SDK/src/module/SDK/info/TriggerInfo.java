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
    
    /** 触发器名 */
    public String name;
    
    /** 告警级别:Information(1),Warning(2),Average(3),High(4),Diaster(5) */
    public Integer severity;
    
    /** 是否启动触发器 */
    public Integer enabled;

    /** 备注 */
    public String memo;
}

/**
 * 触发表达式信息
 * 
 * @author Demon
 */
class TriggerExpressionInfo {
	public TriggerExpressionInfo(){}
	
	/** 触发器 ID */
    public Long trigger_id;
    
    /** 触发表达式 ID */
    public Long trigger_expression_id;
    
    /** 服务 ID */
    public Long service_id;
    
    /** 服务指标 ID */
    public Long service_index_id;
    
    /** 只监控专门指定的指标key，这里的key是 Agent 客户端返回的具体指标 */
    public String key;
    
    /** 运算符 */
    public String operator_type;
    
    /** 数据处理方式 */
    public String func;
    
    /** 参数 */
    public String params;
    
    /** 阈值 */
    public Long threshold;
    
    /** 与上一条条件的逻辑关系 */
    public String logic_type;
}
