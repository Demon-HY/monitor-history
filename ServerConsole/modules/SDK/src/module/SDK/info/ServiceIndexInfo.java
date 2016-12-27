package module.SDK.info;

/**
 * 服务指标
 * 
 * @author Demon
 */
public class ServiceIndexInfo {
	public ServiceIndexInfo(){}

    /** 服务指标 ID */
    public Long service_index_id;
    
    /** 服务 ID */
    public Long service_id;
    
    /** 服务指标名  */
    public String name;
    
    /** 服务指标具体指标名  */
    public String key;
    
    /** 指标数据类型  */
    public String type;
    
    /** 备注 */
    public String memo;
}