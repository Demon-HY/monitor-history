package module.SDK.event.type;

import java.util.List;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.TriggerInfo;
import monitor.service.http.Env;

public class TriggerEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加触发器前</br>
         * 有效参数：</br>
         * triggerInfo
         */
	    PRE_ADD_TRIGGER,
	    /**
         * 事件类型标识：添加触发器后</br>
         * 有效参数：</br>
         * triggerInfo
         */
	    POST_ADD_TRIGGER,
	    
	    /**
         * 事件类型标识：修改触发器前</br>
         * 有效参数：</br>
         * triggerInfo
         */
        PRE_EDIT_TRIGGER,
        /**
         * 事件类型标识：修改触发器后</br>
         * 有效参数：</br>
         * triggerInfo
         */
        POST_EDIT_TRIGGER,
        
        /**
         * 事件类型标识：删除触发器前</br>
         * 有效参数：</br>
         * trigger_id
         */
        PRE_DELETE_TRIGGER,
        /**
         * 事件类型标识：删除触发器后</br>
         * 有效参数：</br>
         * triggerInfo
         */
        POST_DELETE_TRIGGER,
        
        /**
         * 事件类型标识：查询触发器前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_TRIGGER,
        /**
         * 事件类型标识：查询触发器后</br>
         * 有效参数：</br>
         * pairTriggers
         */
        POST_LIST_TRIGGER,
	}
	
	public Env env;
	public Long trigger_id;
	public String name;
    public Integer severity;
    public Integer enabled;
    public String memo;
	
	public String order;
	public String sort;
	public Pair<Integer, List<TriggerInfo>> pairTriggers;
	
	public TriggerInfo triggerInfo;
	public TriggerEvent(){}

	/**
	 * 添加触发器/修改触发器/删除触发器后
	 * @param env
	 * @param triggerInfo
	 */
	public TriggerEvent(Env env, TriggerInfo triggerInfo) {
		this.env = env;
		this.triggerInfo = triggerInfo;
	}
	
	/**
	 * 删除触发器前
	 * @param env
	 * @param trigger_id
	 */
	public TriggerEvent(Env env, Long trigger_id) {
		this.env = env;
		this.trigger_id = trigger_id;
	}
	
	/**
	 * 查询触发器前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public TriggerEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 查询触发器后
	 * @param env
	 * @param pairTriggers
	 */
	public TriggerEvent(Env env, Pair<Integer, List<TriggerInfo>> pairTriggers) {
		this.env = env;
		this.pairTriggers = pairTriggers;
	}
	
}
