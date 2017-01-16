package module.maintain;

import java.sql.SQLException;
import java.util.List;

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
	
	public MaintainInfo addMaintain(Env env, MaintainInfo maintainInfo, List<Long> hostIdList, List<Long> groupIdList) throws LogicalException, SQLException {
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
}
