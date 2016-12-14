package module.host;

import monitor.exception.UnInitilized;

public class HostHttpApi {

	private HostApi hostApi;
	private static HostHttpApi hostHttpApi;
	private HostHttpApi(HostApi hostApi) {
		this.hostApi = hostApi;
	}
	
	public static void init(HostApi hostApi) {
		hostHttpApi = new HostHttpApi(hostApi);
	}
	
	public static HostHttpApi getInst() {
		if (null == hostHttpApi) {
			new UnInitilized();
		}
		return hostHttpApi;
	}
}
