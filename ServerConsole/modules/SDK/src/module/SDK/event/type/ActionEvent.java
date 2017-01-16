package module.SDK.event.type;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.ActionInfo;
import monitor.service.http.Env;

public class ActionEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加报警前</br>
         * 有效参数：</br>
         * actionInfo
         */
	    PRE_ADD_ACTION,
	    /**
         * 事件类型标识：添加报警后</br>
         * 有效参数：</br>
         * actionInfo
         */
	    POST_ADD_ACTION,
	    
	    /**
         * 事件类型标识：修改报警前</br>
         * 有效参数：</br>
         * actionInfo
         */
        PRE_EDIT_ACTION,
        /**
         * 事件类型标识：修改报警后</br>
         * 有效参数：</br>
         * actionInfo
         */
        POST_EDIT_ACTION,
        
        /**
         * 事件类型标识：删除报警前</br>
         * 有效参数：</br>
         * action_id
         */
        PRE_DELETE_ACTION,
        /**
         * 事件类型标识：删除报警后</br>
         * 有效参数：</br>
         * actionInfo
         */
        POST_DELETE_ACTION,
        
        /**
         * 事件类型标识：获取报警前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_ACTION,
        /**
         * 事件类型标识：获取报警后</br>
         * 有效参数：</br>
         * listActions
         */
        POST_LIST_ACTION,
        
        /**
         * 事件类型标识：获取报警装置关联的报警设置前</br>
         * 有效参数：</br>
         * action_id
         */
        PRE_LIST_ACTION_OPERATION,
        /**
         * 事件类型标识：获取报警装置关联的报警设置后</br>
         * 有效参数：</br>
         * actionOperations
         */
        POST_LIST_ACTION_OPERATION,
        
        /**
         * 事件类型标识：获取报警装置关联的群组前</br>
         * 有效参数：</br>
         * action_id
         */
        PRE_LIST_ACTION_GROUP,
        /**
         * 事件类型标识：获取报警装置关联的群组后</br>
         * 有效参数：</br>
         * actionGroups
         */
        POST_LIST_ACTION_GROUP,
        
        /**
         * 事件类型标识：获取报警装置关联的主机前</br>
         * 有效参数：</br>
         * action_id
         */
        PRE_LIST_ACTION_HOST,
        /**
         * 事件类型标识：获取报警装置关联的主机后</br>
         * 有效参数：</br>
         * actionHosts
         */
        POST_LIST_ACTION_HOST,
        
        /**
         * 事件类型标识：获取报警装置关联的触发器前</br>
         * 有效参数：</br>
         * action_id
         */
        PRE_LIST_ACTION_TRIGGER,
        /**
         * 事件类型标识：获取报警装置关联的触发器后</br>
         * 有效参数：</br>
         * actionHosts
         */
        POST_LIST_ACTION_TRIGGER,
	}
	
	public Env env;
	public Long action_id;
	public String name;
	public String memo;
	public Timestamp ctime;
	public Timestamp mtime;
	
	public String order;
	public String sort;
	public Pair<Integer, List<ActionInfo>> pairActions;
	
	/** 报警装置关联的对象 Id 集合 */
	public Map<Long, List<Long>> actionRelationObjectIds;
	
	public ActionInfo actionInfo;
	public ActionEvent(){}

	/**
	 * 添加报警/修改报警/删除报警后
	 * @param env
	 * @param actionInfo
	 */
	public ActionEvent(Env env, ActionInfo actionInfo) {
		this.env = env;
		this.actionInfo = actionInfo;
	}
	
	/**
	 * 删除报警前/获取报警装置关联的对象前
	 * @param env
	 * @param action_id
	 */
	public ActionEvent(Env env, Long action_id) {
		this.env = env;
		this.action_id = action_id;
	}
	
	/**
	 * 获取报警前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public ActionEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 获取报警后
	 * @param env
	 * @param pairActions
	 */
	public ActionEvent(Env env, Pair<Integer, List<ActionInfo>> pairActions) {
		this.env = env;
		this.pairActions = pairActions;
	}
	
	/**
	 * 获取报警装置关联的对象后
	 * @param env
	 * @param actionRelationObjectIds
	 */
	public ActionEvent(Env env, Map<Long, List<Long>> actionRelationObjectIds) {
		this.env = env;
		this.actionRelationObjectIds = actionRelationObjectIds;
	}
	
}
