package module.SDK.event.type;

import java.sql.Timestamp;
import java.util.List;

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
         * 事件类型标识：查询维护前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_MAINTAIN,
        /**
         * 事件类型标识：查询维护后</br>
         * 有效参数：</br>
         * listMaintains
         */
        POST_LIST_MAINTAIN,
	}
	
	public Env env;
	public Long maintain_id;
	public String name;
	public String content;
    public Timestamp start_time;
    public Timestamp end_time;
	
	public String order;
	public String sort;
	public Pair<Integer, List<MaintainInfo>> listMaintains;
	
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
	 * 删除维护前
	 * @param env
	 * @param maintain_id
	 */
	public MaintainEvent(Env env, Long maintain_id) {
		this.env = env;
		this.maintain_id = maintain_id;
	}
	
	/**
	 * 查询维护前
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
	 * 查询维护后
	 * @param env
	 * @param listMaintains
	 */
	public MaintainEvent(Env env, Pair<Integer, List<MaintainInfo>> listMaintains) {
		this.env = env;
		this.listMaintains = listMaintains;
	}
	
}
