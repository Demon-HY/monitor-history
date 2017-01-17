package module.SDK.event.type;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.MaintainInfo;
import monitor.service.http.Env;

public class MaintainEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加维护前</br>
         * 有效参数：</br>
         * maintainInfo
         */
	    PRE_ADD_MAINTAIN,
	    /**
         * 事件类型标识：添加维护后</br>
         * 有效参数：</br>
         * maintainInfo
         */
	    POST_ADD_MAINTAIN,
	    
	    /**
         * 事件类型标识：修改维护前</br>
         * 有效参数：</br>
         * maintainInfo
         */
        PRE_EDIT_MAINTAIN,
        /**
         * 事件类型标识：修改维护后</br>
         * 有效参数：</br>
         * maintainInfo
         */
        POST_EDIT_MAINTAIN,
        
        /**
         * 事件类型标识：删除维护前</br>
         * 有效参数：</br>
         * maintain_id
         */
        PRE_DELETE_MAINTAIN,
        /**
         * 事件类型标识：删除维护后</br>
         * 有效参数：</br>
         * maintainInfo
         */
        POST_DELETE_MAINTAIN,
        
        /**
         * 事件类型标识：获取维护前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_MAINTAIN,
        /**
         * 事件类型标识：获取维护后</br>
         * 有效参数：</br>
         * pairMaintains
         */
        POST_LIST_MAINTAIN, 
        
        /**
         * 事件类型标识：获取维护关联的主机前</br>
         * 有效参数：</br>
         * maintain_id
         */
        PRE_LIST_MAINTAIN_HOST,
        /**
         * 事件类型标识：获取维护关联的主机后</br>
         * 有效参数：</br>
         * maintainRelationObjectIds
         */
        POST_LIST_MAINTAIN_HOST,
        
        /**
         * 事件类型标识：获取维护关联的群组前</br>
         * 有效参数：</br>
         * maintain_id
         */
        PRE_LIST_MAINTAIN_GROUP,
        /**
         * 事件类型标识：获取维护关联的群组后</br>
         * 有效参数：</br>
         * maintainRelationObjectIds
         */
        POST_LIST_MAINTAIN_GROUP,
	}
	
	public Env env;
	public Long maintain_id;
	public String name;
	public String content;
    public Timestamp start_time;
    public Timestamp end_time;
	
	public String order;
	public String sort;
	public Pair<Integer, List<MaintainInfo>> pairMaintains;
	
	/** 维护关联的对象 Id 集合 */
	public Map<Long, List<Long>> maintainRelationObjectIds;
	
	public MaintainInfo maintainInfo;
	
	public MaintainEvent(){}
	public MaintainEvent(Env env) {
		this.env = env;
	}
	
	/**
	 * 添加维护/修改维护/删除维护后
	 * @param env
	 * @param maintainInfo
	 */
	public MaintainEvent(Env env, MaintainInfo maintainInfo) {
		this.env = env;
		this.maintainInfo = maintainInfo;
	}
	
	/**
	 * 删除维护前/获取维护关联的对象前
	 * @param env
	 * @param maintain_id
	 */
	public MaintainEvent(Env env, Long maintain_id) {
		this.env = env;
		this.maintain_id = maintain_id;
	}
	
	/**
	 * 获取维护前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public MaintainEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 获取维护后
	 * @param env
	 * @param pairMaintains
	 */
	public MaintainEvent(Env env, Pair<Integer, List<MaintainInfo>> pairMaintains) {
		this.env = env;
		this.pairMaintains = pairMaintains;
	}
	
	/**
	 * 获取维护关联的对象后
	 * @param env
	 * @param maintainRelationObjectIds
	 */
	public MaintainEvent(Env env, Map<Long, List<Long>> maintainRelationObjectIds) {
		this.env = env;
		this.maintainRelationObjectIds = maintainRelationObjectIds;
	}
}
