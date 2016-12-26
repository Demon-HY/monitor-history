package module.SDK.info;

/**
 * 服务信息
 * 
 * @author Demon
 */
public class ServiceInfo {
    public ServiceInfo() {}
    
    /** 服务 ID */
    public Long service_id;
    
    /** 服务名 */
    public String name;
    
    /** 监控间隔(s) */
    public Integer interval;
    
    /** 插件名 */
    public String plugin_name;
    
    /** 是否有子服务 */
    public Integer has_sub_service;
    
    /** 备注 */
    public String memo;
}

/**
 * 服务指标
 * 
 * @author Demon
 */
class ServiceIndexInfo {
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
