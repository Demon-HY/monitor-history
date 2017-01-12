package module.action;

import java.sql.SQLException;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.info.ActionInfo;
import module.SDK.info.OperationInfo;
import module.SDK.inner.IActionApi;
import module.SDK.inner.IBeans;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

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
	
	/**
	 * 获取报警列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<ActionInfo>> listAction(Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException {
		List<ActionInfo> result = null;
		Integer count = null;
		result = this.actionModel.listAction(pageIndex, pageSize, order, sort);
		count = this.actionModel.countAction();
		
		return new Pair<Integer, List<ActionInfo>>(count, result);
	}
	
	/**
	 * 获取报警设置列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<OperationInfo>> listOperation(Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException {
		List<OperationInfo> result = null;
		Integer count = null;
		result = this.actionModel.listOperation(pageIndex, pageSize, order, sort);
		count = this.actionModel.countOperation();
		
		return new Pair<Integer, List<OperationInfo>>(count, result);
	}
}
