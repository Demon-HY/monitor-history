package module.action;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.event.type.ActionEvent;
import module.SDK.event.type.OperationEvent;
import module.SDK.info.ActionInfo;
import module.SDK.info.OperationInfo;
import module.SDK.inner.IActionApi;
import module.SDK.inner.IBeans;
import module.SDK.stat.ActionRetStat;
import module.SDK.stat.OperationRetStat;

import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;

public class ActionApi implements IActionApi{
	protected IBeans beans;
	protected ActionModel actionModel;
	private static ActionApi actionApi;
	private ActionApi(IBeans beans, ActionModel actionModel) throws LogicalException {
		this.beans = beans;
		this.actionModel = actionModel;
		
		SdkCenter.getInst().addInterface(IActionApi.name, this);
	}
	
	public static void init(IBeans beans, ActionModel actionModel) throws LogicalException {
		actionApi = new ActionApi(beans, actionModel);
	}
	
	public static ActionApi getInst() throws UnInitilized {
		if (null == actionApi) {
			throw new UnInitilized();
		}
		return actionApi;
	}
	
	/***************************************************  Action ***************************************************/
	/**
	 * 获取报警列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 * @throws LogicalException 
	 */
	public Pair<Integer, List<ActionInfo>> listAction(Env env, Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException, LogicalException {
		// 发送获取报警列表前事件
		ActionEvent actionEvent = new ActionEvent(env, order, sort);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.PRE_LIST_ACTION, actionEvent);
				
		List<ActionInfo> result = null;
		Integer count = null;
		result = this.actionModel.listAction(pageIndex, pageSize, order, sort);
		count = this.actionModel.countAction();
		Pair<Integer, List<ActionInfo>> pairActions = new Pair<Integer, List<ActionInfo>>(count, result);
		
		// 发送获取报警列表后事件
		actionEvent = new ActionEvent(env,  pairActions);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.POST_LIST_ACTION, actionEvent);
				
		return pairActions;
	}
	
	public ActionInfo addAction(Env env, ActionInfo actionInfo, List<Long> operationIdList, List<Long> groupIdList, 
			List<Long> hostIdList, List<Long> triggerIdList) throws LogicalException, SQLException {
		// 发送添加报警列表前事件
		ActionEvent actionEvent = new ActionEvent(env, actionInfo);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.PRE_ADD_ACTION, actionEvent);
		
		ActionInfo temp = this.actionModel.getActionByActionName(actionInfo.name);
		if (null != temp) {
			throw new LogicalException(ActionRetStat.ERR_NAME_EXISTED, 
					"ActionApi.addAction add name(" + actionInfo.name + ") existed!");
		}
		
		this.actionModel.addAction(actionInfo);
		actionInfo = this.actionModel.getActionByActionName(actionInfo.name);
		if (null != operationIdList && operationIdList.size() > 0) {
			this.actionModel.addActionOperations(actionInfo.action_id, operationIdList);
		}
		if (null != groupIdList && groupIdList.size() > 0) {
			this.actionModel.addActionGroups(actionInfo.action_id, groupIdList);
		}
		if (null != hostIdList && hostIdList.size() > 0) {
			this.actionModel.addActionHosts(actionInfo.action_id, hostIdList);
		}
		if (null != triggerIdList && triggerIdList.size() > 0) {
			this.actionModel.addActionTriggers(actionInfo.action_id, triggerIdList);
		}
		
		// 发送添加报警列表后事件
		actionEvent = new ActionEvent(env,  actionInfo);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.POST_ADD_ACTION, actionEvent);
		
		return actionInfo;
	}
	
	public ActionInfo editAction(Env env, ActionInfo actionInfo, List<Long> operationIdList, List<Long> groupIdList, 
			List<Long> hostIdList, List<Long> triggerIdList) throws LogicalException, SQLException {
		// 发送修改报警列表前事件
		ActionEvent actionEvent = new ActionEvent(env, actionInfo);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.PRE_EDIT_ACTION, actionEvent);
		
		ActionInfo temp = this.actionModel.getActionByActionId(actionInfo.action_id);
		if (null == temp) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND, 
					"ActionApi.addAction action_id(" + actionInfo.action_id + ") not found!");
		}
		
		this.actionModel.editActionByActionId(actionInfo);
		if (null != operationIdList && operationIdList.size() > 0) {
			this.actionModel.deleteActionOperationsByActionId(actionInfo.action_id);
			this.actionModel.addActionOperations(actionInfo.action_id, operationIdList);
		} else {
			this.actionModel.deleteActionOperationsByActionId(actionInfo.action_id);
		}
		if (null != groupIdList && groupIdList.size() > 0) {
			this.actionModel.deleteActionGroupsByActionId(actionInfo.action_id);
			this.actionModel.addActionGroups(actionInfo.action_id, groupIdList);
		} else {
			this.actionModel.deleteActionGroupsByActionId(actionInfo.action_id);
		}
		if (null != hostIdList && hostIdList.size() > 0) {
			this.actionModel.deleteActionHostsByActionId(actionInfo.action_id);
			this.actionModel.addActionHosts(actionInfo.action_id, hostIdList);
		} else {
			this.actionModel.deleteActionHostsByActionId(actionInfo.action_id);
		}
		if (null != triggerIdList && triggerIdList.size() > 0) {
			this.actionModel.deleteActionTriggersByActionId(actionInfo.action_id);
			this.actionModel.addActionTriggers(actionInfo.action_id, triggerIdList);
		} else {
			this.actionModel.deleteActionTriggersByActionId(actionInfo.action_id);
		}
		
		// 发送修改报警列表后事件
		actionEvent = new ActionEvent(env,  actionInfo);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.POST_EDIT_ACTION, actionEvent);
		
		return actionInfo;
	}
	
	public boolean deleteAction(Env env, Long action_id) throws LogicalException, SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND, 
					"ActionApi.deleteAction action_id(" + action_id + ") not found!");
		}
		// 发送删除报警列表前事件
		ActionEvent actionEvent = new ActionEvent(env, action_id);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.PRE_DELETE_ACTION, actionEvent);
		
		ActionInfo actionInfo = this.actionModel.getActionByActionId(action_id);
		if (null == actionInfo) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND, 
					"ActionApi.deleteAction action_id(" + action_id + ") not found!");
		}
		
		boolean result = this.actionModel.deleteActionByActionId(action_id);
		
		// 发送删除报警列表后事件
		actionEvent = new ActionEvent(env,  actionInfo);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.POST_DELETE_ACTION, actionEvent);
		
		return result;
	}
	
	public Map<Long, List<Long>> getActionOperations(Env env, Long action_id) throws LogicalException, SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND, 
					"ActionApi.getActionOperation action_id(" + action_id + ") not found!");
		}
		// 发送获取报警装置关联的报警设置前事件
		ActionEvent actionEvent = new ActionEvent(env, action_id);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.PRE_LIST_ACTION_OPERATION, actionEvent);
		
		ActionInfo actionInfo = this.actionModel.getActionByActionId(action_id);
		if (null == actionInfo) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND,
					"ActionApi.getActionOperation action_id(" + action_id + ") not found!");
		}
		
		Map<Long, List<Long>> actionOperations = this.actionModel.getActionOperationsByActionId(action_id);
		
		// 发送获取报警装置关联的报警设置后事件
		actionEvent = new ActionEvent(env,  actionOperations);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.POST_LIST_ACTION_OPERATION, actionEvent);
		
		return actionOperations;
	}
	
	public Map<Long, List<Long>> getActionGroups(Env env, Long action_id) throws LogicalException, SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND, 
					"ActionApi.getActionGroup action_id(" + action_id + ") not found!");
		}
		// 发送获取报警装置关联的群组前事件
		ActionEvent actionEvent = new ActionEvent(env, action_id);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.PRE_LIST_ACTION_GROUP, actionEvent);
		
		ActionInfo actionInfo = this.actionModel.getActionByActionId(action_id);
		if (null == actionInfo) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND, 
					"ActionApi.getActionGroup action_id(" + action_id + ") not found!");
		}

		Map<Long, List<Long>> actionGroups = this.actionModel.getActionGroupsByActionId(action_id);
		
		// 发送获取报警装置关联的群组后事件
		actionEvent = new ActionEvent(env,  actionGroups);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.POST_LIST_ACTION_GROUP, actionEvent);
		
		return actionGroups;
	}
	
	public Map<Long, List<Long>> getActionHosts(Env env, Long action_id) throws LogicalException, SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND,
					"ActionApi.getActionHost action_id(" + action_id + ") not found!");
		}
		// 发送获取报警装置关联的主机前事件
		ActionEvent actionEvent = new ActionEvent(env, action_id);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.PRE_LIST_ACTION_HOST, actionEvent);
		
		ActionInfo actionInfo = this.actionModel.getActionByActionId(action_id);
		if (null == actionInfo) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND,
					"ActionApi.getActionHost action_id(" + action_id + ") not found!");
		}
		
		Map<Long, List<Long>> actionHosts = this.actionModel.getActionHostsByActionId(action_id);
		
		// 发送获取报警装置关联的主机后事件
		actionEvent = new ActionEvent(env,  actionHosts);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.POST_LIST_ACTION_HOST, actionEvent);
		
		return actionHosts;
	}
	
	public Map<Long, List<Long>> getActionTriggers(Env env, Long action_id) throws LogicalException, SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND, 
					"ActionApi.getActionTrigger action_id(" + action_id + ") not found!");
		}
		// 发送获取报警装置关联的触发器前事件
		ActionEvent actionEvent = new ActionEvent(env, action_id);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.PRE_LIST_ACTION_TRIGGER, actionEvent);
		
		ActionInfo actionInfo = this.actionModel.getActionByActionId(action_id);
		if (null == actionInfo) {
			throw new LogicalException(ActionRetStat.ERR_ACTION_ID_NOT_FOUND,
					"ActionApi.getActionTrigger action_id(" + action_id + ") not found!");
		}
		
		Map<Long, List<Long>> actionTriggers = this.actionModel.getActionTriggersByActionId(action_id);
		
		// 发送获取报警装置关联的触发器后事件
		actionEvent = new ActionEvent(env,  actionTriggers);
		this.beans.getEventHub().dispatchEvent(ActionEvent.Type.POST_LIST_ACTION_TRIGGER, actionEvent);
		
		return actionTriggers;
	}
	
	/***************************************************  Operation ***************************************************/
	/**
	 * 获取报警设置列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 * @throws LogicalException 
	 */
	public Pair<Integer, List<OperationInfo>> listOperation(Env env, Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException, LogicalException {
		// 发送获取报警设置列表前事件
		OperationEvent operationEvent = new OperationEvent(env, order, sort);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.PRE_LIST_OPERATION, operationEvent);
				
		List<OperationInfo> result = null;
		Integer count = null;
		result = this.actionModel.listOperation(pageIndex, pageSize, order, sort);
		count = this.actionModel.countOperation();
		Pair<Integer, List<OperationInfo>> pairOperations = new Pair<Integer, List<OperationInfo>>(count, result);
		
		// 发送获取报警设置列表后事件
		operationEvent = new OperationEvent(env,  pairOperations);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.POST_LIST_OPERATION, operationEvent);
		
		return pairOperations;
	}
	
	public OperationInfo addOperation(Env env, OperationInfo operationInfo, List<Long> userIdList) throws LogicalException, SQLException {
		// 发送添加报警设置前事件
		OperationEvent operationEvent = new OperationEvent(env, operationInfo);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.PRE_ADD_OPERATION, operationEvent);
		
		OperationInfo temp = this.actionModel.getOperationByName(operationInfo.name);
		if (null != temp) {
			throw new LogicalException(OperationRetStat.ERR_NAME_EXISTED, 
					"ActionApi.addOperation add name(" + operationInfo.name + ") existed!");
		}
		
		this.actionModel.addOperation(operationInfo);
		operationInfo = this.actionModel.getOperationByName(operationInfo.name);
		if (null != userIdList && userIdList.size() > 0) {
			this.actionModel.addOperationUsers(operationInfo.operation_id, userIdList);
		}
		
		// 发送添加报警设置后事件
		operationEvent = new OperationEvent(env,  operationInfo);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.POST_ADD_OPERATION, operationEvent);
		
		return operationInfo;
	}
	
	public OperationInfo editOperation(Env env, OperationInfo operationInfo, List<Long> userIdList) throws LogicalException, SQLException {
		// 发送修改报警设置前事件
		OperationEvent operationEvent = new OperationEvent(env, operationInfo);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.PRE_EDIT_OPERATION, operationEvent);
		
		OperationInfo temp = this.actionModel.getOperationByOperationId(operationInfo.operation_id);
		if (null == temp) {
			throw new LogicalException(OperationRetStat.ERR_OPERATION_ID_NOT_FOUND, 
					"ActionApi.editOperation operation_id(" + operationInfo.operation_id + ") not found!");
		}
		
		this.actionModel.editOperationByOperationId(operationInfo);
		if (null != userIdList && userIdList.size() > 0) {
			this.actionModel.deleteOperationUsersByOperationId(operationInfo.operation_id);
			this.actionModel.addOperationUsers(operationInfo.operation_id, userIdList);
		} else {
			this.actionModel.deleteOperationUsersByOperationId(operationInfo.operation_id);
		}
		
		// 发送修改报警设置后事件
		operationEvent = new OperationEvent(env,  operationInfo);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.POST_EDIT_OPERATION, operationEvent);
		
		return operationInfo;
	}
	
	public boolean deleteOperation(Env env, Long operation_id) throws LogicalException, SQLException {
		if (null == operation_id || operation_id.longValue() < 1) {
			throw new LogicalException(OperationRetStat.ERR_OPERATION_ID_NOT_FOUND, 
					"ActionApi.deleteOperation operation_id(" + operation_id + ") not found!");
		}
		// 发送删除报警设置前事件
		OperationEvent operationEvent = new OperationEvent(env, operation_id);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.PRE_DELETE_OPERATION, operationEvent);
		
		OperationInfo operationInfo = this.actionModel.getOperationByOperationId(operation_id);
		if (null == operationInfo) {
			throw new LogicalException(OperationRetStat.ERR_OPERATION_ID_NOT_FOUND,
					"ActionApi.deleteOperation operation_id(" + operation_id + ") not found!");
		}
		
		boolean result = this.actionModel.deleteOperationByOperationId(operation_id);
		
		// 发送删除报警设置后事件
		operationEvent = new OperationEvent(env,  operationInfo);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.POST_DELETE_OPERATION, operationEvent);
		
		return result;
	}
	
	public Map<Long, List<Long>> getOperationUsers(Env env, Long operation_id) throws LogicalException, SQLException {
		if (null == operation_id || operation_id.longValue() < 1) {
			throw new LogicalException(OperationRetStat.ERR_OPERATION_ID_NOT_FOUND, 
					"ActionApi.getOperationGroup operation_id(" + operation_id + ") not found!");
		}
		// 发送获取报警设置关联的用户前事件
		OperationEvent operationEvent = new OperationEvent(env, operation_id);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.PRE_LIST_OPERATION_USER, operationEvent);

		OperationInfo operationInfo = this.actionModel.getOperationByOperationId(operation_id);
		if (null == operationInfo) {
			throw new LogicalException(OperationRetStat.ERR_OPERATION_ID_NOT_FOUND, 
					"ActionApi.getOperationGroup operation_id(" + operation_id + ") not found!");
		}
		
		Map<Long, List<Long>> operationGroups = this.actionModel.getOperationUsersByOperationId(operation_id);
		
		// 发送获取报警设置关联的用户后事件
		operationEvent = new OperationEvent(env,  operationInfo);
		this.beans.getEventHub().dispatchEvent(OperationEvent.Type.POST_LIST_OPERATION_USER, operationEvent);
		
		return operationGroups;
	}
	
}
