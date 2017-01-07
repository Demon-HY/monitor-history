package module.host;

import java.sql.SQLException;
import java.util.List;

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
	 * <blockquote>
     * 		类型：对象<br/>
     * 		描述：上下文对象<br/>
     * </blockquote>
	 * @param hostInfo 
	 * <blockquote>
     * 		类型：对象<br/>
     * 		描述：主机信息<br/>
     * </blockquote>
     * @param groupIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：群组 ID 集合<br/>
     * </blockquote>
     * @param templateIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：模板 ID 集合<br/>
     * </blockquote>
     * 
     * @return HostInfo
     * 
	 * @throws SQLException
	 * @throws LogicalException 
	 */
	public HostInfo addHost(Env env, HostInfo hostInfo, List<Long> groupIdList, List<Long> templateIdList) 
			throws SQLException, LogicalException  {
		// 发送添加主机前事件
		HostEvent hostEvent = new HostEvent(env, hostInfo);
		this.beans.getEventHub().dispatchEvent(HostEvent.Type.PRE_ADD_HOST, hostEvent);
		
		HostInfo host = this.hostModel.getHostByIP(hostInfo.ip);
		if (null != host) {
		    throw new LogicalException(HostRetStat.ERR_ADD_IP_EXISTED, "HostApi.addHost add ip(" + hostInfo.ip + ") existed!");
		}
		
		this.hostModel.addHost(hostInfo);
		host = this.hostModel.getHostByIP(hostInfo.ip);
		if (null != groupIdList && groupIdList.size() > 0) {
			this.hostModel.addHostGroup(host.host_id, groupIdList);
		}
		if (null != templateIdList && templateIdList.size() > 0) {
			this.hostModel.addHostTemplate(host.host_id, templateIdList);
		}
		
		// 发送添加主机后事件
		hostEvent = new HostEvent(env, hostInfo);
		this.beans.getEventHub().dispatchEvent(HostEvent.Type.POST_ADD_HOST, hostEvent);
		return null;
	}
	
	/**
     * 添加主机
     * 
     * @param env
     * <blockquote>
     *      类型：对象<br/>
     *      描述：上下文对象<br/>
     * </blockquote>
     * @param hostInfo 
     * <blockquote>
     *      类型：对象<br/>
     *      描述：主机信息<br/>
     * </blockquote>
     * @param groupIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：群组 ID 集合<br/>
     * </blockquote>
     * @param templateIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：模板 ID 集合<br/>
     * </blockquote>
     * 
     * @return HostInfo
     * 
     * @throws SQLException
     * @throws LogicalException 
     */
    public HostInfo editHost(Env env, HostInfo hostInfo, List<Long> groupIdList, List<Long> templateIdList) 
            throws SQLException, LogicalException  {
        // 发送修改主机前事件
        HostEvent hostEvent = new HostEvent(env, hostInfo);
        this.beans.getEventHub().dispatchEvent(HostEvent.Type.PRE_EDIT_HOST, hostEvent);
        
        HostInfo temp = this.hostModel.getHostByHostID(hostInfo.host_id);
        if(null == temp) {
            throw new LogicalException(HostRetStat.ERR_HOST_ID_NOT_FOUND, "HostApi.editHost host_id(" + hostInfo.host_id + ") not found!");
        } else {
            temp = null;
        }
        
        this.hostModel.editHostByHostID(hostInfo);
        this.hostModel.deleteHostGroupByHostID(hostInfo.host_id);
        this.hostModel.deleteHostTemplateByHostID(hostInfo.host_id);
        if (null != groupIdList && groupIdList.size() > 0) {
            this.hostModel.addHostGroup(hostInfo.host_id, groupIdList);
        }
        if (null != templateIdList && templateIdList.size() > 0) {
            this.hostModel.addHostTemplate(hostInfo.host_id, templateIdList);
        }
        
        // 发送修改主机后事件
        hostEvent = new HostEvent(env, hostInfo);
        this.beans.getEventHub().dispatchEvent(HostEvent.Type.POST_EDIT_HOST, hostEvent);
        return null;
    }
}
