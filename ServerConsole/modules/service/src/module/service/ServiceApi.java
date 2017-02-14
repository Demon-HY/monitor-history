package module.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.event.type.ServiceEvent;
import module.SDK.event.type.ServiceIndexEvent;
import module.SDK.info.IndexInfo;
import module.SDK.info.ServiceInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.IServiceApi;
import module.SDK.stat.ServiceIndexRetStat;
import module.SDK.stat.ServiceRetStat;

import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;

public class ServiceApi implements IServiceApi{
	protected IBeans beans;
	protected ServiceModel serviceModel;
	private static ServiceApi serviceApi;
	private ServiceApi(IBeans beans, ServiceModel serviceModel) throws LogicalException {
		this.beans = beans;
		this.serviceModel = serviceModel;
		
		SdkCenter.getInst().addInterface(IServiceApi.name, this);
	}
	
	public static void init(IBeans beans, ServiceModel serviceModel) throws LogicalException {
		serviceApi = new ServiceApi(beans, serviceModel);
	}
	
	public static ServiceApi getInst() throws UnInitilized {
		if (null == serviceApi) {
			throw new UnInitilized();
		}
		return serviceApi;
	}

    public Pair<Integer, List<ServiceInfo>> listService(Integer pageIndex, Integer pageSize,
    		String order, String sort) throws SQLException {
        List<ServiceInfo> result = null;
        Integer count = null;
        result = this.serviceModel.listService(pageIndex, pageSize, order, sort);
        count = this.serviceModel.countService();
        
        return new Pair<Integer, List<ServiceInfo>>(count, result);
    }
    
    public ServiceInfo addService(Env env, ServiceInfo serviceInfo, List<Long> indexIdList) 
			throws LogicalException, SQLException {
		// 发送添加服务前事件
		ServiceEvent serviceEvent = new ServiceEvent(env, serviceInfo);
		this.beans.getEventHub().dispatchEvent(ServiceEvent.Type.PRE_ADD_SERVICE, serviceEvent);
		
		ServiceInfo temp = this.serviceModel.getServiceByServiceName(serviceInfo.name);
		if (null != temp) {
			throw new LogicalException(ServiceRetStat.ERR_SERVICE_NAME_EXISTED, 
					"ServiceApi.addService add name(" + serviceInfo.name + ") existed!");
		}
		
		this.serviceModel.addService(serviceInfo);
		serviceInfo = this.serviceModel.getServiceByServiceName(serviceInfo.name);
		if (null != indexIdList && indexIdList.size() > 0) {
			this.serviceModel.addServiceIndexs(serviceInfo.service_id, indexIdList);
		}
		
		// 发送添加服务后事件
		serviceEvent = new ServiceEvent(env,  serviceInfo);
		this.beans.getEventHub().dispatchEvent(ServiceEvent.Type.POST_ADD_SERVICE, serviceEvent);
		
		return serviceInfo;
	}
    
    public ServiceInfo editService(Env env, ServiceInfo serviceInfo, List<Long> indexIdList) 
			throws LogicalException, SQLException {
		// 发送修改服务前事件
		ServiceEvent serviceEvent = new ServiceEvent(env, serviceInfo);
		this.beans.getEventHub().dispatchEvent(ServiceEvent.Type.PRE_EDIT_SERVICE, serviceEvent);
		
		ServiceInfo temp = this.serviceModel.getServiceByServiceId(serviceInfo.service_id);
		if (null == temp) {
			throw new LogicalException(ServiceRetStat.ERR_SERVICE_ID_NOT_FOUND,
					"ServiceApi.editService service_id(" + serviceInfo.service_id + ") not found!");
		}
		
		this.serviceModel.editServiceByServiceId(serviceInfo);
		if (null != indexIdList && indexIdList.size() > 0) {
			this.serviceModel.deleteServiceIndexsByServiceId(serviceInfo.service_id);
			this.serviceModel.addServiceIndexs(serviceInfo.service_id, indexIdList);
		} else {
			this.serviceModel.deleteServiceIndexsByServiceId(serviceInfo.service_id);
		}
		
		// 发送修改服务后事件
		serviceEvent = new ServiceEvent(env,  serviceInfo);
		this.beans.getEventHub().dispatchEvent(ServiceEvent.Type.POST_EDIT_SERVICE, serviceEvent);
		
		return serviceInfo;
	}
    
    public ServiceInfo deleteService(Env env, Long service_id) throws LogicalException, SQLException {
		if (null == service_id || service_id.longValue() < 1) {
			throw new LogicalException(ServiceRetStat.ERR_SERVICE_ID_NOT_FOUND, 
					"ServiceApi.deleteService service_id(" + service_id + ") not found!");
		}
		// 发送删除服务前事件
		ServiceEvent serviceEvent = new ServiceEvent(env, service_id);
		this.beans.getEventHub().dispatchEvent(ServiceEvent.Type.PRE_DELETE_SERVICE, serviceEvent);
		
		ServiceInfo serviceInfo = this.serviceModel.getServiceByServiceId(service_id);
		if (null == serviceInfo) {
			throw new LogicalException(ServiceRetStat.ERR_SERVICE_ID_NOT_FOUND,
					"ServiceApi.deleteService service_id(" + service_id + ") not found!");
		}
		
		this.serviceModel.deleteServiceByServiceId(service_id);
		
		// 发送删除服务后事件
		serviceEvent = new ServiceEvent(env,  serviceInfo);
		this.beans.getEventHub().dispatchEvent(ServiceEvent.Type.POST_DELETE_SERVICE, serviceEvent);
		
		return serviceInfo;
	}

    public Pair<Integer, List<IndexInfo>> listIndex(Integer pageIndex, Integer pageSize, 
    		String order, String sort) throws SQLException {
    	List<IndexInfo> result = null;
    	Integer count = null;
    	result = this.serviceModel.listIndex(pageIndex, pageSize, order, sort);
    	count = this.serviceModel.countIndex();
    	
        return new Pair<Integer, List<IndexInfo>>(count, result);
    }
    
    public IndexInfo addIndex(Env env, IndexInfo indexInfo) 
			throws LogicalException, SQLException {
		// 发送添加服务指标前事件
		ServiceIndexEvent indexEvent = new ServiceIndexEvent(env, indexInfo);
		this.beans.getEventHub().dispatchEvent(ServiceIndexEvent.Type.PRE_ADD_SERVICE_INDEX, indexEvent);
		
		IndexInfo temp = this.serviceModel.getIndexByIndexName(indexInfo.name);
		if (null != temp) {
			throw new LogicalException(ServiceIndexRetStat.ERR_SERVICE_INDEX_NAME_EXISTED, 
					"IndexApi.addIndex add name(" + indexInfo.name + ") existed!");
		}
		
		this.serviceModel.addIndex(indexInfo);
		indexInfo = this.serviceModel.getIndexByIndexName(indexInfo.name);
		
		// 发送添加服务指标后事件
		indexEvent = new ServiceIndexEvent(env,  indexInfo);
		this.beans.getEventHub().dispatchEvent(ServiceIndexEvent.Type.POST_ADD_SERVICE_INDEX, indexEvent);
		
		return indexInfo;
	}
	
	public IndexInfo editIndex(Env env, IndexInfo indexInfo, List<Long> templateIdList) 
			throws LogicalException, SQLException {
		// 发送修改服务指标前事件
		ServiceIndexEvent indexEvent = new ServiceIndexEvent(env, indexInfo);
		this.beans.getEventHub().dispatchEvent(ServiceIndexEvent.Type.PRE_EDIT_SERVICE_INDEX, indexEvent);
		
		IndexInfo temp = this.serviceModel.getIndexByIndexId(indexInfo.index_id);
		if (null == temp) {
			throw new LogicalException(ServiceIndexRetStat.ERR_SERVICE_INDEX_ID_NOT_FOUND,
					"IndexApi.editIndex index_id(" + indexInfo.index_id + ") not found!");
		}
		
		this.serviceModel.editIndexByIndexId(indexInfo);
		
		// 发送修改服务指标后事件
		indexEvent = new ServiceIndexEvent(env,  indexInfo);
		this.beans.getEventHub().dispatchEvent(ServiceIndexEvent.Type.POST_EDIT_SERVICE_INDEX, indexEvent);
		
		return indexInfo;
	}
	
	public boolean deleteIndex(Env env, Long index_id) throws LogicalException, SQLException {
		if (null == index_id || index_id.longValue() < 1) {
			throw new LogicalException(ServiceIndexRetStat.ERR_SERVICE_INDEX_ID_NOT_FOUND, 
					"IndexApi.deleteIndex index_id(" + index_id + ") not found!");
		}
		// 发送删除服务指标前事件
		ServiceIndexEvent indexEvent = new ServiceIndexEvent(env, index_id);
		this.beans.getEventHub().dispatchEvent(ServiceIndexEvent.Type.PRE_DELETE_SERVICE_INDEX, indexEvent);
		
		IndexInfo indexInfo = this.serviceModel.getIndexByIndexId(index_id);
		if (null == indexInfo) {
			throw new LogicalException(ServiceIndexRetStat.ERR_SERVICE_INDEX_ID_NOT_FOUND,
					"IndexApi.deleteIndex index_id(" + index_id + ") not found!");
		}
		
		boolean result = this.serviceModel.deleteIndexByIndexId(index_id);
		
		// 发送删除服务指标后事件
		indexEvent = new ServiceIndexEvent(env,  indexInfo);
		this.beans.getEventHub().dispatchEvent(ServiceIndexEvent.Type.POST_DELETE_SERVICE_INDEX, indexEvent);
		
		return result;
	}
	
	public Map<Long, List<Long>> getServiceIndexs(Env env, Long service_id) throws LogicalException, SQLException {
		if (null == service_id || service_id.longValue() < 1) {
			throw new LogicalException(ServiceRetStat.ERR_SERVICE_ID_NOT_FOUND, 
					"ServiceApi.getServiceTemplates service_id(" + service_id + ") not found!");
		}
		// 发送获取服务关联的服务指标前事件
		ServiceEvent serviceEvent = new ServiceEvent(env, service_id);
		this.beans.getEventHub().dispatchEvent(ServiceEvent.Type.PRE_LIST_SERVICE_INDEX, serviceEvent);
		
		ServiceInfo serviceInfo = this.serviceModel.getServiceByServiceId(service_id);
		if (null == serviceInfo) {
			throw new LogicalException(ServiceRetStat.ERR_SERVICE_ID_NOT_FOUND, 
					"ServiceApi.getServiceIndexs service_id(" + service_id + ") not found!");
		}
		
		Map<Long, List<Long>> serviceIndexs = this.serviceModel.getServiceIndexsByServiceId(service_id);
		
		// 发送获取服务关联的服务指标后事件
		serviceEvent = new ServiceEvent(env,  serviceIndexs);
		this.beans.getEventHub().dispatchEvent(ServiceEvent.Type.POST_LIST_SERVICE_INDEX, serviceEvent);
		
		return serviceIndexs;
	}
}
