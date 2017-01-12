package module.SDK.event.type;

import java.util.List;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.ExpressionInfo;
import monitor.service.http.Env;

public class TriggerExpressionEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加报警前</br>
         * 有效参数：</br>
         * expressionInfo
         */
	    PRE_ADD_TRIGGER_EXPRESSION,
	    /**
         * 事件类型标识：添加报警后</br>
         * 有效参数：</br>
         * expressionInfo
         */
	    POST_ADD_TRIGGER_EXPRESSION,
	    
	    /**
         * 事件类型标识：修改报警前</br>
         * 有效参数：</br>
         * expressionInfo
         */
        PRE_EDIT_TRIGGER_EXPRESSION,
        /**
         * 事件类型标识：修改报警后</br>
         * 有效参数：</br>
         * expressionInfo
         */
        POST_EDIT_TRIGGER_EXPRESSION,
        
        /**
         * 事件类型标识：删除报警前</br>
         * 有效参数：</br>
         * trigger_expression_id
         */
        PRE_DELETE_TRIGGER_EXPRESSION,
        /**
         * 事件类型标识：删除报警后</br>
         * 有效参数：</br>
         * expressionInfo
         */
        POST_DELETE_TRIGGER_EXPRESSION,
        
        /**
         * 事件类型标识：查询报警前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_TRIGGER_EXPRESSION,
        /**
         * 事件类型标识：查询报警后</br>
         * 有效参数：</br>
         * listTriggerExpressions
         */
        POST_LIST_TRIGGER_EXPRESSION,
	}
	
	public Env env;
    public Long trigger_id;
    public Long trigger_expression_id;
    public Long service_id;
    public Long service_index_id;
    public String key;
    public String operator_type;
    public String func;
    public String params;
    public Long threshold;
    public String logic_type;
	
	public String order;
	public String sort;
	public Pair<Integer, List<ExpressionInfo>> listTriggerExpressions;
	
	public ExpressionInfo expressionInfo;
	public TriggerExpressionEvent(){}

	/**
	 * 添加报警/修改报警/删除报警后
	 * @param env
	 * @param expressionInfo
	 */
	public TriggerExpressionEvent(Env env, ExpressionInfo expressionInfo) {
		this.env = env;
		this.expressionInfo = expressionInfo;
	}
	
	/**
	 * 删除报警前
	 * @param env
	 * @param trigger_expression_id
	 */
	public TriggerExpressionEvent(Env env, Long trigger_expression_id) {
		this.env = env;
		this.trigger_expression_id = trigger_expression_id;
	}
	
	/**
	 * 查询报警前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public TriggerExpressionEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 查询报警后
	 * @param env
	 * @param listTriggerExpressions
	 */
	public TriggerExpressionEvent(Env env, Pair<Integer, List<ExpressionInfo>> listTriggerExpressions) {
		this.env = env;
		this.listTriggerExpressions = listTriggerExpressions;
	}
	
}
