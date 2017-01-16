package module.SDK.event.type;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.OperationInfo;
import monitor.service.http.Env;

public class OperationEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加报警设置前</br>
         * 有效参数：</br>
         * operationInfo
         */
	    PRE_ADD_OPERATION,
	    /**
         * 事件类型标识：添加报警设置后</br>
         * 有效参数：</br>
         * operationInfo
         */
	    POST_ADD_OPERATION,
	    
	    /**
         * 事件类型标识：修改报警设置前</br>
         * 有效参数：</br>
         * operationInfo
         */
        PRE_EDIT_OPERATION,
        /**
         * 事件类型标识：修改报警设置后</br>
         * 有效参数：</br>
         * operationInfo
         */
        POST_EDIT_OPERATION,
        
        /**
         * 事件类型标识：删除报警设置前</br>
         * 有效参数：</br>
         * operation_id
         */
        PRE_DELETE_OPERATION,
        /**
         * 事件类型标识：删除报警设置后</br>
         * 有效参数：</br>
         * operationInfo
         */
        POST_DELETE_OPERATION,
        
        /**
         * 事件类型标识：查询报警设置前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_OPERATION,
        /**
         * 事件类型标识：查询报警设置后</br>
         * 有效参数：</br>
         * pairOperations
         */
        POST_LIST_OPERATION,
        
        /**
         * 事件类型标识：查询报警设置所属的用户前</br>
         * 有效参数：</br>
         * operation_id
         */
        PRE_LIST_OPERATION_USER,
        /**
         * 事件类型标识：查询报警设置所属的用户后</br>
         * 有效参数：</br>
         * operationUsers
         */
        POST_LIST_OPERATION_USER,
	}
	
	public Env env;
	public Long operation_id;
	public Long action_id;
	public String name;
	public Integer step;
    public String action_type;
    public String msg_format;
	
	public String order;
	public String sort;
	public Pair<Integer, List<OperationInfo>> pairOperations;
	
	public Map<Long, List<Long>> operationUsers;
	
	public OperationInfo operationInfo;
	public OperationEvent(){}

	/**
	 * 添加报警设置/修改报警设置/删除报警设置后
	 * @param env
	 * @param operationInfo
	 */
	public OperationEvent(Env env, OperationInfo operationInfo) {
		this.env = env;
		this.operationInfo = operationInfo;
	}
	
	/**
	 * 删除报警设置前
	 * @param env
	 * @param operation_id
	 */
	public OperationEvent(Env env, Long operation_id) {
		this.env = env;
		this.operation_id = operation_id;
	}
	
	/**
	 * 查询报警设置前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public OperationEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 查询报警设置后
	 * @param env
	 * @param pairOperations
	 */
	public OperationEvent(Env env, Pair<Integer, List<OperationInfo>> pairOperations) {
		this.env = env;
		this.pairOperations = pairOperations;
	}
	
}
