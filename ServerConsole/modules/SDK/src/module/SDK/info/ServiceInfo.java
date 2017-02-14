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
    
    public ServiceInfo(String name, Integer interval, String plugin_name, Integer has_sub_service, String memo) {
    	this.name= name;
    	this.interval = interval;
    	this.plugin_name= plugin_name;
    	this.has_sub_service = has_sub_service;
    	this.memo = memo;
    }
    
    public ServiceInfo(Long service_id, String name, Integer interval, String plugin_name, Integer has_sub_service, String memo) {
    	this.service_id = service_id;
    	this.name= name;
    	this.interval = interval;
    	this.plugin_name= plugin_name;
    	this.has_sub_service = has_sub_service;
    	this.memo = memo;
    }
}