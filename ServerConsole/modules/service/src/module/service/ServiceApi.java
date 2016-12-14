package module.service;

import module.SDK.SdkCenter;
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
}
