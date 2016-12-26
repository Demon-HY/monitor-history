package module.service;

import java.sql.SQLException;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.info.GroupInfo;
import module.SDK.info.ServiceInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.IServiceApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

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

    public Pair<Integer, List<GroupInfo>> listService(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
        List<ServiceInfo> result = null;
        Integer count = null;
        result = this.serviceModel.listService(pageIndex, pageSize, order, sort);
        return null;
    }

    public Pair<Integer, List<GroupInfo>> listServiceIndex(Integer pageIndex, Integer pageSize, String order,
            String sort) {
        // TODO Auto-generated method stub
        return null;
    }
}
