package module.trigger;

import java.sql.SQLException;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.info.ExpressionInfo;
import module.SDK.info.TriggerInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.ITriggerApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

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
	
	/**
	 * 获取触发条件列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<ExpressionInfo>> listTriggerExpression(Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException {
		List<ExpressionInfo> result = null;
		Integer count = null;
		result = this.triggerModel.listTriggerExpression(pageIndex, pageSize, order, sort);
		count = this.triggerModel.countTriggerExpression();
		
		return new Pair<Integer, List<ExpressionInfo>>(count, result);
	}
}
