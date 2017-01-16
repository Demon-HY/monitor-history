package module.SDK.event.type;

import java.util.List;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.IndexInfo;
import monitor.service.http.Env;

public class ServiceIndexEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加服务指标前</br>
         * 有效参数：</br>
         * serviceIndexInfo
         */
	    PRE_ADD_SERVICE_INDEX,
	    /**
         * 事件类型标识：添加服务指标后</br>
         * 有效参数：</br>
         * serviceIndexInfo
         */
	    POST_ADD_SERVICE_INDEX,
	    
	    /**
         * 事件类型标识：修改服务指标前</br>
         * 有效参数：</br>
         * serviceIndexInfo
         */
        PRE_EDIT_SERVICE_INDEX,
        /**
         * 事件类型标识：修改服务指标后</br>
         * 有效参数：</br>
         * serviceIndexInfo
         */
        POST_EDIT_SERVICE_INDEX,
        
        /**
         * 事件类型标识：删除服务指标前</br>
         * 有效参数：</br>
         * index_id
         */
        PRE_DELETE_SERVICE_INDEX,
        /**
         * 事件类型标识：删除服务指标后</br>
         * 有效参数：</br>
         * serviceIndexInfo
         */
        POST_DELETE_SERVICE_INDEX,
        
        /**
         * 事件类型标识：查询服务指标前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_SERVICE_INDEX,
        /**
         * 事件类型标识：查询服务指标后</br>
         * 有效参数：</br>
         * pairServiceIndexs
         */
        POST_LIST_SERVICE_INDEX,
	}
	
	public Env env;
    public Long index_id;
    public Long service_id;
    public String name;
    public String key;
    public String type;
    public String memo;
	
	public String order;
	public String sort;
	public Pair<Integer, List<IndexInfo>> pairServiceIndexs;
	
	public IndexInfo serviceIndexInfo;
	public ServiceIndexEvent(){}

	/**
	 * 添加服务指标/修改服务指标/删除服务指标后
	 * @param env
	 * @param serviceIndexInfo
	 */
	public ServiceIndexEvent(Env env, IndexInfo serviceIndexInfo) {
		this.env = env;
		this.serviceIndexInfo = serviceIndexInfo;
	}
	
	/**
	 * 删除服务指标前
	 * @param env
	 * @param index_id
	 */
	public ServiceIndexEvent(Env env, Long index_id) {
		this.env = env;
		this.index_id = index_id;
	}
	
	/**
	 * 查询服务指标前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public ServiceIndexEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 查询服务指标后
	 * @param env
	 * @param pairServiceIndexs
	 */
	public ServiceIndexEvent(Env env, Pair<Integer, List<IndexInfo>> pairServiceIndexs) {
		this.env = env;
		this.pairServiceIndexs = pairServiceIndexs;
	}
	
}
