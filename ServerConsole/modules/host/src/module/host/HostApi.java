package module.host;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.event.type.HostEvent;
import module.SDK.info.HostInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.IHostApi;
import module.SDK.stat.HostRetStat;

import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;

public class HostApi implements IHostApi{
	protected IBeans beans;
	protected HostModel hostModel;
	private static HostApi hostApi;
	private HostApi(IBeans beans, HostModel hostModel) throws LogicalException {
		this.beans = beans;
		this.hostModel = hostModel;
		
		SdkCenter.getInst().addInterface(IHostApi.name, this);
	}
	
	public static void init(IBeans beans, HostModel hostModel) throws LogicalException {
		hostApi = new HostApi(beans, hostModel);
	}
	
	public static HostApi getInst() throws UnInitilized {
		if (null == hostApi) {
			throw new UnInitilized();
		}
		return hostApi;
	}

	/**
	 * 获取主机列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<HostInfo>> listHost(Integer pageIndex, Integer pageSize, String order, String sort) 
			throws SQLException {
		List<HostInfo> result = null;
		Integer count = null;
		result = this.hostModel.listHost(pageIndex, pageSize, order, sort);
		count = this.hostModel.countHost();
		
		return new Pair<Integer, List<HostInfo>>(count, result);
	}

	/**
	 * 添加主机
	 * 
	 * @param env
	 * @param hostInfo 主机信息
     * @param groupIdList 群组 Id 集合
     * @param templateIdList 模板 Id 集合
     * 
     * @return HostInfo
	 * @throws SQLException
	 * @throws LogicalException 
	 */
	public HostInfo addHost(Env env, HostInfo hostInfo, List<Long> groupIdList, List<Long> templateIdList) 
			throws SQLException, LogicalException  {
		// 发送添加主机前事件
		HostEvent hostEvent = new HostEvent(env, hostInfo);
		this.beans.getEventHub().dispatchEvent(HostEvent.Type.PRE_ADD_HOST, hostEvent);
		
		HostInfo temp = this.hostModel.getHostByIP(hostInfo.ip);
		if (null != temp) {
		    throw new LogicalException(HostRetStat.ERR_IP_EXISTED, 
		    		"HostApi.addHost add ip(" + hostInfo.ip + ") existed!");
		}
		temp = this.hostModel.getHostByName(hostInfo.name);
		if (null != temp) {
			throw new LogicalException(HostRetStat.ERR_HOST_NAME_EXISTED, 
					"HostApi.addHost add name(" + hostInfo.name + ") existed!");
		}
		
		this.hostModel.addHost(hostInfo);
		hostInfo = this.hostModel.getHostByIP(hostInfo.ip);
		if (null != groupIdList && groupIdList.size() > 0) {
			this.hostModel.addHostGroups(hostInfo.host_id, groupIdList);
		}
		if (null != templateIdList && templateIdList.size() > 0) {
			this.hostModel.addHostTemplates(hostInfo.host_id, templateIdList);
		}
		
		// 发送添加主机后事件
		hostEvent = new HostEvent(env, hostInfo);
		this.beans.getEventHub().dispatchEvent(HostEvent.Type.POST_ADD_HOST, hostEvent);
		
		return hostInfo;
	}
	
	/**
     * 编辑主机
     * 
     * @param env
     * @param hostInfo 主机信息
     * @param groupIdList 群组 Id 集合
     * @param templateIdList 模板 Id 集合
     * 
     * @return HostInfo
     * @throws SQLException
     * @throws LogicalException 
     */
    public HostInfo editHost(Env env, HostInfo hostInfo, List<Long> groupIdList, List<Long> templateIdList) 
            throws SQLException, LogicalException  {
        // 发送修改主机前事件
        HostEvent hostEvent = new HostEvent(env, hostInfo);
        this.beans.getEventHub().dispatchEvent(HostEvent.Type.PRE_EDIT_HOST, hostEvent);
        
        HostInfo temp = this.hostModel.getHostByHostId(hostInfo.host_id);
        if(null == temp) {
            throw new LogicalException(HostRetStat.ERR_HOST_ID_NOT_FOUND, 
            		"HostApi.editHost host_id(" + hostInfo.host_id + ") not found!");
        } else {
            temp = null;
        }
        
        this.hostModel.editHostByHostId(hostInfo);
        // 这里先判断请求的 group_id 和 template_id 集合是否为空再删除，是为了考虑用户修改了其他信息，
        // 没有修改群组和模板关联的话，
        // 这里可以不用动，因为修改这个需要访问四次数据库，很浪费性能
        if (null != groupIdList && groupIdList.size() > 0) {
        	this.hostModel.deleteHostGroupByHostId(hostInfo.host_id);
            this.hostModel.addHostGroups(hostInfo.host_id, groupIdList);
        } else {
        	this.hostModel.deleteHostGroupByHostId(hostInfo.host_id);
		}
        if (null != templateIdList && templateIdList.size() > 0) {
        	this.hostModel.deleteHostTemplateByHostId(hostInfo.host_id);
            this.hostModel.addHostTemplates(hostInfo.host_id, templateIdList);
        } else {
        	this.hostModel.deleteHostTemplateByHostId(hostInfo.host_id);
		}
        
        HostInfo host = this.hostModel.getHostByHostId(hostInfo.host_id);
        
        // 发送修改主机后事件
        hostEvent = new HostEvent(env, host);
        this.beans.getEventHub().dispatchEvent(HostEvent.Type.POST_EDIT_HOST, hostEvent);
        return null;
    }

    /**
     * 删除主机
     * 
     * @param env
     * @param host_id 主机 Id
     * 
     * @return 要删除的主机信息
     * @throws LogicalException 
     * @throws SQLException 
     */
	public HostInfo deleteHost(Env env, Long host_id) throws LogicalException, SQLException {
		if (null ==host_id || host_id.longValue() < 0) {
			throw new LogicalException(HostRetStat.ERR_HOST_ID_NOT_FOUND, 
					"HostApi.deleteHost host_id(" + host_id + ") not found!");
		}
		// 发送删除主机前事件
        HostEvent hostEvent = new HostEvent(env, host_id);
        this.beans.getEventHub().dispatchEvent(HostEvent.Type.PRE_DELETE_HOST, hostEvent);
		
        HostInfo hostInfo = this.hostModel.getHostByHostId(host_id);
        if (null == hostInfo) {
        	throw new LogicalException(HostRetStat.ERR_HOST_ID_NOT_FOUND, 
        			"HostApi.deleteHost host_id(" + host_id + ") not found!");
        }
        
        this.hostModel.deleteHostByHostId(host_id);
        
        // 发送删除主机后事件
        hostEvent = new HostEvent(env, hostInfo);
        this.beans.getEventHub().dispatchEvent(HostEvent.Type.POST_DELETE_HOST, hostEvent);
		
        return hostInfo;
	}
	
	public Map<Long, List<Long>> getHostGroups(Env env, Long host_id) 
			throws LogicalException, SQLException {
		if (null == host_id || host_id.longValue() < 1) {
			throw new LogicalException(HostRetStat.ERR_HOST_ID_NOT_FOUND, 
					"HostApi.getHostGroups host_id(" + host_id + ") not found!");
		}
		// 发送获取主机关联的群组前事件
		HostEvent hostEvent = new HostEvent(env, host_id);
		this.beans.getEventHub().dispatchEvent(HostEvent.Type.PRE_LIST_HOST_GROUP, hostEvent);
		
		HostInfo hostInfo = this.hostModel.getHostByHostId(host_id);
		if (null == hostInfo) {
			throw new LogicalException(HostRetStat.ERR_HOST_ID_NOT_FOUND, 
					"HostApi.getHostGroups host_id(" + host_id + ") not found!");
		}
		
		Map<Long, List<Long>> hostGroups = this.hostModel.getHostGroupsByHostId(host_id);
		
		// 发送获取主机关联的群组后事件
		hostEvent = new HostEvent(env,  hostGroups);
		this.beans.getEventHub().dispatchEvent(HostEvent.Type.POST_LIST_HOST_GROUP, hostEvent);
		
		return hostGroups;
	}
	
	public Map<Long, List<Long>> getHostTemplates(Env env, Long host_id) 
			throws LogicalException, SQLException {
		if (null == host_id || host_id.longValue() < 1) {
			throw new LogicalException(HostRetStat.ERR_HOST_ID_NOT_FOUND, 
					"HostApi.getHostTemplates host_id(" + host_id + ") not found!");
		}
		// 发送获取主机关联的模板前事件
		HostEvent hostEvent = new HostEvent(env, host_id);
		this.beans.getEventHub().dispatchEvent(HostEvent.Type.PRE_LIST_HOST_TEMPLATE, hostEvent);
		
		HostInfo hostInfo = this.hostModel.getHostByHostId(host_id);
		if (null == hostInfo) {
			throw new LogicalException(HostRetStat.ERR_HOST_ID_NOT_FOUND, 
					"HostApi.getHostTemplates host_id(" + host_id + ") not found!");
		}
		
		Map<Long, List<Long>> hostTemplates = this.hostModel.getHostTemplatesByHostId(host_id);
		
		// 发送获取主机关联的模板后事件
		hostEvent = new HostEvent(env,  hostTemplates);
		this.beans.getEventHub().dispatchEvent(HostEvent.Type.POST_LIST_HOST_TEMPLATE, hostEvent);
		
		return hostTemplates;
	}
}
