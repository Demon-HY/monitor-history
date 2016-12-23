package module.group;

import java.sql.SQLException;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.info.GroupInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.IGroupApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

public class GroupApi implements IGroupApi{
	protected IBeans beans;
	protected GroupModel groupModel;
	private static GroupApi groupApi;
	private GroupApi(IBeans beans, GroupModel groupModel) throws LogicalException {
		this.beans = beans;
		this.groupModel = groupModel;
		
		SdkCenter.getInst().addInterface(IGroupApi.name, this);
	}
	
	public static void init(IBeans beans, GroupModel groupModel) throws LogicalException {
		groupApi = new GroupApi(beans, groupModel);
	}
	
	public static GroupApi getInst() throws UnInitilized {
		if (null == groupApi) {
			throw new UnInitilized();
		}
		return groupApi;
	}

	/**
	 * 获取群组列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<GroupInfo>> listGroup(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		List<GroupInfo> result = null;
		Integer count = null;
		result = this.groupModel.listGroup(pageIndex, pageSize, order, sort);
		count = this.groupModel.countGroup();
		
		return new Pair<Integer, List<GroupInfo>>(count, result);
	}
}
