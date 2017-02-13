package module.maintain;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.event.type.MaintainEvent;
import module.SDK.info.MaintainInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.IMaintainApi;
import module.SDK.stat.MaintainRetStat;

import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;

public class MaintainApi implements IMaintainApi{
	protected IBeans beans;
	protected MaintainModel maintainModel;
	private static MaintainApi maintainApi;
	private MaintainApi(IBeans beans, MaintainModel maintainModel) throws LogicalException {
		this.beans = beans;
		this.maintainModel = maintainModel;
		
		SdkCenter.getInst().addInterface(IMaintainApi.name, this);
	}
	
	public static void init(IBeans beans, MaintainModel maintainModel) throws LogicalException {
		maintainApi = new MaintainApi(beans, maintainModel);
	}
	
	public static MaintainApi getInst() throws UnInitilized {
		if (null == maintainApi) {
			throw new UnInitilized();
		}
		return maintainApi;
	}
	
	/**
	 * 获取维护列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<MaintainInfo>> listMaintain(Integer pageIndex, Integer pageSize, 
			String order, String sort) throws SQLException {
		List<MaintainInfo> result = null;
		Integer count = null;
		result = this.maintainModel.listMaintain(pageIndex, pageSize, order, sort);
		count = this.maintainModel.countMaintain();
		
		return new Pair<Integer, List<MaintainInfo>>(count, result);
	}
	
	public MaintainInfo addMaintain(Env env, MaintainInfo maintainInfo, List<Long> hostIdList, List<Long> groupIdList) 
			throws LogicalException, SQLException {
		// 发送添加维护前事件
		MaintainEvent maintainEvent = new MaintainEvent(env, maintainInfo);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.PRE_ADD_MAINTAIN, maintainEvent);
		
		MaintainInfo temp = this.maintainModel.getMaintainByMaintainName(maintainInfo.name);
		if (null != temp) {
			throw new LogicalException(MaintainRetStat.ERR_MAINTAIN_NAME_EXISTED, 
					"MaintainApi.addMaintain add name(" + maintainInfo.name + ") existed!");
		}
		
		this.maintainModel.addMaintain(maintainInfo);
		maintainInfo = this.maintainModel.getMaintainByMaintainName(maintainInfo.name);
		if (null != hostIdList && hostIdList.size() > 0) {
			this.maintainModel.addMaintainHosts(maintainInfo.maintain_id, hostIdList);
		}
		if (null != groupIdList && groupIdList.size() > 0) {
			this.maintainModel.addMaintainGroups(maintainInfo.maintain_id, groupIdList);
		}
		
		// 发送添加维护后事件
		maintainEvent = new MaintainEvent(env,  maintainInfo);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.POST_ADD_MAINTAIN, maintainEvent);
		
		return maintainInfo;
	}
	
	public MaintainInfo editMaintain(Env env, MaintainInfo maintainInfo, List<Long> hostIdList, List<Long> groupIdList) 
			throws LogicalException, SQLException {
		// 发送修改维护前事件
		MaintainEvent groupEvent = new MaintainEvent(env, maintainInfo);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.PRE_EDIT_MAINTAIN, groupEvent);
		
		MaintainInfo temp = this.maintainModel.getMaintainByMaintainId(maintainInfo.maintain_id);
		if (null == temp) {
			throw new LogicalException(MaintainRetStat.ERR_MAINTAIN_ID_NOT_FOUND,
					"MaintainApi.editMaintain maintain_id(" + maintainInfo.maintain_id + ") not found!");
		}
		
		this.maintainModel.editMaintainByMaintainId(maintainInfo);
		if (null != hostIdList && hostIdList.size() > 0) {
			this.maintainModel.deleteMaintainHostsByMaintainId(maintainInfo.maintain_id);
			this.maintainModel.addMaintainHosts(maintainInfo.maintain_id, hostIdList);
		} else {
			this.maintainModel.deleteMaintainHostsByMaintainId(maintainInfo.maintain_id);
		}
		if (null != groupIdList && groupIdList.size() > 0) {
			this.maintainModel.deleteMaintainGroupsByMaintainId(maintainInfo.maintain_id);
			this.maintainModel.addMaintainGroups(maintainInfo.maintain_id, groupIdList);
		} else {
			this.maintainModel.deleteMaintainGroupsByMaintainId(maintainInfo.maintain_id);
		}
		
		// 发送修改维护后事件
		groupEvent = new MaintainEvent(env,  maintainInfo);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.POST_EDIT_MAINTAIN, groupEvent);
		
		return maintainInfo;
	}
	
	public MaintainInfo deleteMaintain(Env env, Long maintain_id) throws LogicalException, SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new LogicalException(MaintainRetStat.ERR_MAINTAIN_ID_NOT_FOUND, 
					"MaintainApi.deleteMaintain maintain_id(" + maintain_id + ") not found!");
		}
		// 发送删除维护前事件
		MaintainEvent maintainEvent = new MaintainEvent(env, maintain_id);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.PRE_DELETE_MAINTAIN, maintainEvent);
		
		MaintainInfo maintainInfo = this.maintainModel.getMaintainByMaintainId(maintain_id);
		if (null == maintainInfo) {
			throw new LogicalException(MaintainRetStat.ERR_MAINTAIN_ID_NOT_FOUND, 
					"MaintainApi.deleteMaintain maintain_id(" + maintain_id + ") not found!");
		}
		
		this.maintainModel.deleteMaintainByMaintainId(maintain_id);
		
		// 发送删除维护后事件
		maintainEvent = new MaintainEvent(env,  maintainInfo);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.POST_DELETE_MAINTAIN, maintainEvent);
		
		return maintainInfo;
	}
	
	public Map<Long, List<Long>> getMaintainHosts(Env env, Long maintain_id) throws LogicalException, SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new LogicalException(MaintainRetStat.ERR_MAINTAIN_ID_NOT_FOUND, 
					"MaintainApi.getMaintainHosts maintain_id(" + maintain_id + ") not found!");
		}
		// 发送获取维护关联的主机前事件
		MaintainEvent maintainEvent = new MaintainEvent(env, maintain_id);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.PRE_LIST_MAINTAIN_HOST, maintainEvent);
		
		MaintainInfo maintainInfo = this.maintainModel.getMaintainByMaintainId(maintain_id);
		if (null == maintainInfo) {
			throw new LogicalException(MaintainRetStat.ERR_MAINTAIN_ID_NOT_FOUND, 
					"MaintainApi.getMaintainHosts maintain_id(" + maintain_id + ") not found!");
		}
		
		Map<Long, List<Long>> maintainHosts = this.maintainModel.getMaintainHostsByMaintainId(maintain_id);
		
		// 发送获取维护关联的主机后事件
		maintainEvent = new MaintainEvent(env,  maintainHosts);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.POST_LIST_MAINTAIN_HOST, maintainEvent);
		
		return maintainHosts;
	}
	
	public Map<Long, List<Long>> getMaintainGroups(Env env, Long maintain_id) throws LogicalException, SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new LogicalException(MaintainRetStat.ERR_MAINTAIN_ID_NOT_FOUND, 
					"MaintainApi.getMaintainGroups maintain_id(" + maintain_id + ") not found!");
		}
		// 发送获取维护关联的群组前事件
		MaintainEvent maintainEvent = new MaintainEvent(env, maintain_id);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.PRE_LIST_MAINTAIN_GROUP, maintainEvent);
		
		MaintainInfo maintainInfo = this.maintainModel.getMaintainByMaintainId(maintain_id);
		if (null == maintainInfo) {
			throw new LogicalException(MaintainRetStat.ERR_MAINTAIN_ID_NOT_FOUND, 
					"MaintainApi.getMaintainGroups maintain_id(" + maintain_id + ") not found!");
		}
		
		Map<Long, List<Long>> maintainGroups = this.maintainModel.getMaintainGroupsByMaintainId(maintain_id);
		
		// 发送获取维护关联的群组后事件
		maintainEvent = new MaintainEvent(env,  maintainGroups);
		this.beans.getEventHub().dispatchEvent(MaintainEvent.Type.POST_LIST_MAINTAIN_GROUP, maintainEvent);
		
		return maintainGroups;
	}
}
