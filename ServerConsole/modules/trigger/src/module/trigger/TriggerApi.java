package module.trigger;

import java.sql.SQLException;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.event.type.ExpressionEvent;
import module.SDK.event.type.TriggerEvent;
import module.SDK.info.ExpressionInfo;
import module.SDK.info.TriggerInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.ITriggerApi;
import module.SDK.stat.ExpressionRetStat;
import module.SDK.stat.TriggerRetStat;

import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;

public class TriggerApi implements ITriggerApi{
	protected IBeans beans;
	protected TriggerModel triggerModel;
	private static TriggerApi triggerApi;
	private TriggerApi(IBeans beans, TriggerModel triggerModel) throws LogicalException {
		this.beans = beans;
		this.triggerModel = triggerModel;
		
		SdkCenter.getInst().addInterface(ITriggerApi.name, this);
	}
	
	public static void init(IBeans beans, TriggerModel triggerModel) throws LogicalException {
		triggerApi = new TriggerApi(beans, triggerModel);
	}
	
	public static TriggerApi getInst() throws UnInitilized {
		if (null == triggerApi) {
			throw new UnInitilized();
		}
		return triggerApi;
	}
	
	/**
	 * 获取触发器列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<TriggerInfo>> listTrigger(Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException {
		List<TriggerInfo> result = null;
		Integer count = null;
		result = this.triggerModel.listTrigger(pageIndex, pageSize, order, sort);
		count = this.triggerModel.countTrigger();
		
		return new Pair<Integer, List<TriggerInfo>>(count, result);
	}
	
	public TriggerInfo addTrigger(Env env, TriggerInfo triggerInfo) 
			throws LogicalException, SQLException {
		// 发送添加触发器前事件
		TriggerEvent triggerEvent = new TriggerEvent(env, triggerInfo);
		this.beans.getEventHub().dispatchEvent(TriggerEvent.Type.PRE_ADD_TRIGGER, triggerEvent);
		
		TriggerInfo temp = this.triggerModel.getTriggerByName(triggerInfo.name);
		if (null != temp) {
			throw new LogicalException(TriggerRetStat.ERR_TRIGGER_NAME_EXISTED, 
					"TriggerApi.addTrigger add name(" + triggerInfo.name + ") existed!");
		}
		
		this.triggerModel.addTrigger(triggerInfo);
		triggerInfo = this.triggerModel.getTriggerByName(triggerInfo.name);
		
		// 发送添加触发器后事件
		triggerEvent = new TriggerEvent(env,  triggerInfo);
		this.beans.getEventHub().dispatchEvent(TriggerEvent.Type.POST_ADD_TRIGGER, triggerEvent);
		
		return triggerInfo;
	}
	
	public TriggerInfo editTrigger(Env env, TriggerInfo triggerInfo) 
			throws LogicalException, SQLException {
		// 发送修改触发器前事件
		TriggerEvent triggerEvent = new TriggerEvent(env, triggerInfo);
		this.beans.getEventHub().dispatchEvent(TriggerEvent.Type.PRE_EDIT_TRIGGER, triggerEvent);
		
		TriggerInfo temp = this.triggerModel.getTriggerByTriggerId(triggerInfo.trigger_id);
		if (null == temp) {
			throw new LogicalException(TriggerRetStat.ERR_TRIGGER_ID_NOT_FOUND,
					"TriggerApi.editTrigger trigger_id(" + triggerInfo.trigger_id + ") not found!");
		}
		
		this.triggerModel.editTriggerByTriggerId(triggerInfo);
		
		// 发送修改触发器后事件
		triggerEvent = new TriggerEvent(env,  triggerInfo);
		this.beans.getEventHub().dispatchEvent(TriggerEvent.Type.POST_EDIT_TRIGGER, triggerEvent);
		
		return triggerInfo;
	}
	
	public TriggerInfo deleteTrigger(Env env, Long trigger_id) throws LogicalException, SQLException {
		if (null == trigger_id || trigger_id.longValue() < 1) {
			throw new LogicalException(TriggerRetStat.ERR_TRIGGER_ID_NOT_FOUND, 
					"TriggerApi.deleteTrigger trigger_id(" + trigger_id + ") not found!");
		}
		// 发送删除触发器前事件
		TriggerEvent triggerEvent = new TriggerEvent(env, trigger_id);
		this.beans.getEventHub().dispatchEvent(TriggerEvent.Type.PRE_DELETE_TRIGGER, triggerEvent);
		
		TriggerInfo triggerInfo = this.triggerModel.getTriggerByTriggerId(trigger_id);
		if (null == triggerInfo) {
			throw new LogicalException(TriggerRetStat.ERR_TRIGGER_ID_NOT_FOUND,
					"TriggerApi.deleteTrigger trigger_id(" + trigger_id + ") not found!");
		}
		
		this.triggerModel.deleteTriggerByTriggerId(trigger_id);
		
		// 发送删除触发器后事件
		triggerEvent = new TriggerEvent(env,  triggerInfo);
		this.beans.getEventHub().dispatchEvent(TriggerEvent.Type.POST_DELETE_TRIGGER, triggerEvent);
		
		return triggerInfo;
	}
	
	/**
	 * 获取触发条件列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<ExpressionInfo>> listExpression(Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException {
		List<ExpressionInfo> result = null;
		Integer count = null;
		result = this.triggerModel.listExpression(pageIndex, pageSize, order, sort);
		count = this.triggerModel.countExpression();
		
		return new Pair<Integer, List<ExpressionInfo>>(count, result);
	}
	
	public boolean addExpression(Env env, ExpressionInfo expressionInfo) 
			throws LogicalException, SQLException {
		// 发送添加触发表达式前事件
		ExpressionEvent expressionEvent = new ExpressionEvent(env, expressionInfo);
		this.beans.getEventHub().dispatchEvent(ExpressionEvent.Type.PRE_ADD_TRIGGER_EXPRESSION, expressionEvent);
		
		boolean result = this.triggerModel.addExpression(expressionInfo);
		
		// 发送添加触发表达式后事件
		expressionEvent = new ExpressionEvent(env,  expressionInfo);
		this.beans.getEventHub().dispatchEvent(ExpressionEvent.Type.POST_ADD_TRIGGER_EXPRESSION, expressionEvent);
		
		return result;
	}
	
	public ExpressionInfo editExpression(Env env, ExpressionInfo expressionInfo) 
			throws LogicalException, SQLException {
		// 发送修改触发表达式前事件
		ExpressionEvent expressionEvent = new ExpressionEvent(env, expressionInfo);
		this.beans.getEventHub().dispatchEvent(ExpressionEvent.Type.PRE_EDIT_TRIGGER_EXPRESSION, expressionEvent);
		
		ExpressionInfo temp = this.triggerModel.getExpressionByExpressionId(expressionInfo.expression_id);
		if (null == temp) {
			throw new LogicalException(ExpressionRetStat.ERR_TRIGGER_EXPRESSION_ID_NOT_FOUND,
					"ExpressionApi.editExpression expression_id(" + expressionInfo.expression_id + ") not found!");
		}
		
		this.triggerModel.editExpressionByExpressionId(expressionInfo);
		
		// 发送修改触发表达式后事件
		expressionEvent = new ExpressionEvent(env,  expressionInfo);
		this.beans.getEventHub().dispatchEvent(ExpressionEvent.Type.POST_EDIT_TRIGGER_EXPRESSION, expressionEvent);
		
		return expressionInfo;
	}
	
	public boolean deleteExpression(Env env, Long expression_id) throws LogicalException, SQLException {
		if (null == expression_id || expression_id.longValue() < 1) {
			throw new LogicalException(ExpressionRetStat.ERR_TRIGGER_EXPRESSION_ID_NOT_FOUND, 
					"ExpressionApi.deleteExpression expression_id(" + expression_id + ") not found!");
		}
		// 发送删除触发表达式前事件
		ExpressionEvent expressionEvent = new ExpressionEvent(env, expression_id);
		this.beans.getEventHub().dispatchEvent(ExpressionEvent.Type.PRE_DELETE_TRIGGER_EXPRESSION, expressionEvent);
		
		ExpressionInfo expressionInfo = this.triggerModel.getExpressionByExpressionId(expression_id);
		if (null == expressionInfo) {
			throw new LogicalException(ExpressionRetStat.ERR_TRIGGER_EXPRESSION_ID_NOT_FOUND,
					"ExpressionApi.deleteExpression expression_id(" + expression_id + ") not found!");
		}
		
		boolean result = this.triggerModel.deleteExpressionByExpressionId(expression_id);
		
		// 发送删除触发表达式后事件
		expressionEvent = new ExpressionEvent(env,  expressionInfo);
		this.beans.getEventHub().dispatchEvent(ExpressionEvent.Type.POST_DELETE_TRIGGER_EXPRESSION, expressionEvent);
		
		return result;
	}
}
