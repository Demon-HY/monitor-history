package module.SDK.info;

/**
 * 触发表达式信息
 * 
 * @author Demon
 */
public class ExpressionInfo {
	public ExpressionInfo(){}
	
	/** 触发器 ID */
    public Long trigger_id;
    
    /** 触发条件 ID */
    public Long trigger_expression_id;
    
    /** 服务 ID */
    public Long service_id;
    
    /** 服务指标 ID */
    public Long service_index_id;
    
    /** 只监控专门指定的指标key，这里的key是 Agent 客户端返回的具体指标 */
    public String key;
    
    /** 运算符 */
    public String operator_type;
    
    /** 数据处理方式：Average,Max,Hit,Last */
    public String func;
    
    /** 参数 */
    public String params;
    
    /** 阈值 */
    public Long threshold;
    
    /** 与上一条条件的逻辑关系 */
    public String logic_type;
}
