package module.SDK.event.type;

import java.util.List;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.ActionOperationInfo;
import monitor.service.http.Env;

public class ActionOperationEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加报警设置前</br>
         * 有效参数：</br>
         * actionOperationInfo
         */
	    PRE_ADD_ACTION_OPERATION,
	    /**
         * 事件类型标识：添加报警设置后</br>
         * 有效参数：</br>
         * actionOperationInfo
         */
	    POST_ADD_ACTION_OPERATION,
	    
	    /**
         * 事件类型标识：修改报警设置前</br>
         * 有效参数：</br>
         * actionOperationInfo
         */
        PRE_EDIT_ACTION_OPERATION,
        /**
         * 事件类型标识：修改报警设置后</br>
         * 有效参数：</br>
         * actionOperationInfo
         */
        POST_EDIT_ACTION_OPERATION,
        
        /**
         * 事件类型标识：删除报警设置前</br>
         * 有效参数：</br>
         * action_operation_id
         */
        PRE_DELETE_ACTION_OPERATION,
        /**
         * 事件类型标识：删除报警设置后</br>
         * 有效参数：</br>
         * actionOperationInfo
         */
        POST_DELETE_ACTION_OPERATION,
        
        /**
         * 事件类型标识：查询报警设置前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_ACTION_OPERATION,
        /**
         * 事件类型标识：查询报警设置后</br>
         * 有效参数：</br>
         * listActionOperations
         */
        POST_LIST_ACTION_OPERATION,
	}
	
	public Env env;
	public Long action_operation_id;
	public Long action_id;
	public String name;
	public Integer step;
    public String action_type;
    public String msg_format;
	
	public String order;
	public String sort;
	public Pair<Integer, List<ActionOperationInfo>> listActionOperations;
	
	public ActionOperationInfo actionOperationInfo;
	public ActionOperationEvent(){}

	/**
	 * 添加报警设置/修改报警设置/删除报警设置后
	 * @param env
	 * @param actionOperationInfo
	 */
	public ActionOperationEvent(Env env, ActionOperationInfo actionOperationInfo) {
		this.env = env;
		this.actionOperationInfo = actionOperationInfo;
	}
	
	/**
	 * 删除报警设置前
	 * @param env
	 * @param action_operation_id
	 */
	public ActionOperationEvent(Env env, Long action_operation_id) {
		this.env = env;
		this.action_operation_id = action_operation_id;
	}
	
	/**
	 * 查询报警设置前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public ActionOperationEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 查询报警设置后
	 * @param env
	 * @param listActionOperations
	 */
	public ActionOperationEvent(Env env, Pair<Integer, List<ActionOperationInfo>> listActionOperations) {
		this.env = env;
		this.listActionOperations = listActionOperations;
	}
	
}
