package module.SDK.event.type;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.ServiceInfo;
import monitor.service.http.Env;

public class ServiceEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加服务前</br>
         * 有效参数：</br>
         * serviceInfo
         */
	    PRE_ADD_SERVICE,
	    /**
         * 事件类型标识：添加服务后</br>
         * 有效参数：</br>
         * serviceInfo
         */
	    POST_ADD_SERVICE,
	    
	    /**
         * 事件类型标识：修改服务前</br>
         * 有效参数：</br>
         * serviceInfo
         */
        PRE_EDIT_SERVICE,
        /**
         * 事件类型标识：修改服务后</br>
         * 有效参数：</br>
         * serviceInfo
         */
        POST_EDIT_SERVICE,
        
        /**
         * 事件类型标识：删除服务前</br>
         * 有效参数：</br>
         * service_id
         */
        PRE_DELETE_SERVICE,
        /**
         * 事件类型标识：删除服务后</br>
         * 有效参数：</br>
         * serviceInfo
         */
        POST_DELETE_SERVICE,
        
        /**
         * 事件类型标识：获取服务前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_SERVICE,
        /**
         * 事件类型标识：获取服务后</br>
         * 有效参数：</br>
         * pairServices
         */
        POST_LIST_SERVICE,
        
        /**
         * 事件类型标识：获取服务关联的服务指标前</br>
         * 有效参数：</br>
         * service_id
         */
        PRE_LIST_SERVICE_INDEX,
        /**
         * 事件类型标识：获取服务关联的服务指标后</br>
         * 有效参数：</br>
         * serviceRelationObjectIds
         */
        POST_LIST_SERVICE_INDEX,
	}
	
	public Env env;
	public Long service_id;
	public String name;
    public Integer interval;
    public String plugin_name;
    public Integer has_sub_service;
    public String memo;
	
	public String order;
	public String sort;
	public Pair<Integer, List<ServiceInfo>> pairServices;
	
	/** 服务关联的对象 Id 集合 */
	public Map<Long, List<Long>> serviceRelationObjectIds;
	
	public ServiceInfo serviceInfo;
	
	public ServiceEvent(){}
	public ServiceEvent(Env env) {
		this.env = env;
	}
	
	/**
	 * 添加服务/修改服务/删除服务后
	 * @param env
	 * @param serviceInfo
	 */
	public ServiceEvent(Env env, ServiceInfo serviceInfo) {
		this.env = env;
		this.serviceInfo = serviceInfo;
	}
	
	/**
	 * 删除服务前
	 * @param env
	 * @param service_id
	 */
	public ServiceEvent(Env env, Long service_id) {
		this.env = env;
		this.service_id = service_id;
	}
	
	/**
	 * 获取服务前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public ServiceEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 获取服务后
	 * @param env
	 * @param pairServices
	 */
	public ServiceEvent(Env env, Pair<Integer, List<ServiceInfo>> pairServices) {
		this.env = env;
		this.pairServices = pairServices;
	}
	
	/**
	 * 获取服务关联的对象后
	 * @param env
	 * @param groupRelationObjectIds
	 */
	public ServiceEvent(Env env, Map<Long, List<Long>> serviceRelationObjectIds) {
		this.env = env;
		this.serviceRelationObjectIds = serviceRelationObjectIds;
	}
}
