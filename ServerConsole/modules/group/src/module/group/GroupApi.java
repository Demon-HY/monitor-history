package module.group;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.event.type.GroupEvent;
import module.SDK.info.GroupInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.IGroupApi;
import module.SDK.stat.GroupRetStat;

import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;

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
	public Pair<Integer, List<GroupInfo>> listGroup(Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException {
		List<GroupInfo> result = null;
		Integer count = null;
		result = this.groupModel.listGroup(pageIndex, pageSize, order, sort);
		count = this.groupModel.countGroup();
		
		return new Pair<Integer, List<GroupInfo>>(count, result);
	}
	
	public GroupInfo addGroup(Env env, GroupInfo groupInfo, List<Long> templateIdList) throws LogicalException, SQLException {
		// 发送添加群组前事件
		GroupEvent groupEvent = new GroupEvent(env, groupInfo);
		this.beans.getEventHub().dispatchEvent(GroupEvent.Type.PRE_ADD_GROUP, groupEvent);
		
		GroupInfo temp = this.groupModel.getGroupByGroupName(groupInfo.name);
		if (null != temp) {
			throw new LogicalException(GroupRetStat.ERR_GROUP_NAME_EXISTED, 
					"GroupApi.addGroup add name(" + groupInfo.name + ") existed!");
		}
		
		this.groupModel.addGroup(groupInfo);
		groupInfo = this.groupModel.getGroupByGroupName(groupInfo.name);
		if (null != templateIdList && templateIdList.size() > 0) {
			this.groupModel.addGroupTemplates(groupInfo.group_id, templateIdList);
		}
		
		// 发送添加群组后事件
		groupEvent = new GroupEvent(env,  groupInfo);
		this.beans.getEventHub().dispatchEvent(GroupEvent.Type.POST_ADD_GROUP, groupEvent);
		
		return groupInfo;
	}
	
	public GroupInfo editGroup(Env env, GroupInfo groupInfo, List<Long> templateIdList) throws LogicalException, SQLException {
		// 发送修改群组前事件
		GroupEvent groupEvent = new GroupEvent(env, groupInfo);
		this.beans.getEventHub().dispatchEvent(GroupEvent.Type.PRE_EDIT_GROUP, groupEvent);
		
		GroupInfo temp = this.groupModel.getGroupByGroupId(groupInfo.group_id);
		if (null == temp) {
			throw new LogicalException(GroupRetStat.ERR_GROUP_ID_NOT_FOUND,
					"GroupApi.addGroup group_id(" + groupInfo.group_id + ") not found!");
		}
		
		this.groupModel.editGroupByGroupId(groupInfo);
		if (null != templateIdList && templateIdList.size() > 0) {
			this.groupModel.deleteGroupTemplatesByGroupId(groupInfo.group_id);
			this.groupModel.addGroupTemplates(groupInfo.group_id, templateIdList);
		} else {
			this.groupModel.deleteGroupTemplatesByGroupId(groupInfo.group_id);
		}
		
		// 发送修改群组后事件
		groupEvent = new GroupEvent(env,  groupInfo);
		this.beans.getEventHub().dispatchEvent(GroupEvent.Type.POST_EDIT_GROUP, groupEvent);
		
		return groupInfo;
	}
	
	public boolean deleteGroup(Env env, Long group_id) throws LogicalException, SQLException {
		if (null == group_id || group_id.longValue() < 1) {
			throw new LogicalException(GroupRetStat.ERR_GROUP_ID_NOT_FOUND, "GroupApi.deleteGroup group_id(" + group_id + ") not found!");
		}
		// 发送删除群组前事件
		GroupEvent groupEvent = new GroupEvent(env, group_id);
		this.beans.getEventHub().dispatchEvent(GroupEvent.Type.PRE_DELETE_GROUP, groupEvent);
		
		GroupInfo groupInfo = this.groupModel.getGroupByGroupId(group_id);
		if (null == groupInfo) {
			throw new LogicalException(GroupRetStat.ERR_GROUP_ID_NOT_FOUND, "GroupApi.deleteGroup group_id(" + group_id + ") not found!");
		}
		
		boolean result = this.groupModel.deleteGroupByGroupId(group_id);
		
		// 发送删除群组后事件
		groupEvent = new GroupEvent(env,  groupInfo);
		this.beans.getEventHub().dispatchEvent(GroupEvent.Type.POST_DELETE_GROUP, groupEvent);
		
		return result;
	}
	
	public Map<Long, List<Long>> getGroupTemplates(Env env, Long group_id) throws LogicalException, SQLException {
		if (null == group_id || group_id.longValue() < 1) {
			throw new LogicalException(GroupRetStat.ERR_GROUP_ID_NOT_FOUND, "GroupApi.getGroupTemplates group_id(" + group_id + ") not found!");
		}
		// 发送获取群组关联的模板前事件
		GroupEvent groupEvent = new GroupEvent(env, group_id);
		this.beans.getEventHub().dispatchEvent(GroupEvent.Type.PRE_LIST_GROUP_TEMPLATE, groupEvent);
		
		GroupInfo groupInfo = this.groupModel.getGroupByGroupId(group_id);
		if (null == groupInfo) {
			throw new LogicalException(GroupRetStat.ERR_GROUP_ID_NOT_FOUND, "GroupApi.getGroupTemplates group_id(" + group_id + ") not found!");
		}
		
		Map<Long, List<Long>> groupTemplates = this.groupModel.getGroupTemplatesByGroupId(group_id);
		
		// 发送获取群组关联的模板后事件
		groupEvent = new GroupEvent(env,  groupTemplates);
		this.beans.getEventHub().dispatchEvent(GroupEvent.Type.POST_LIST_GROUP_TEMPLATE, groupEvent);
		
		return groupTemplates;
	}
}
