package module.service;

import monitor.exception.UnInitilized;

public class ServiceHttpApi {

	private ServiceApi serviceApi;
	private static ServiceHttpApi serviceHttpApi;
	private ServiceHttpApi(ServiceApi serviceApi) {
		this.serviceApi = serviceApi;
	}
	
	public static void init(ServiceApi serviceApi) {
		serviceHttpApi = new ServiceHttpApi(serviceApi);
	}
	
	public static ServiceHttpApi getInst() {
		if (null == serviceHttpApi) {
			new UnInitilized();
		}
		return serviceHttpApi;
	}
}
