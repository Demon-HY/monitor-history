package module.host;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import module.SDK.inner.IHostApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

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
}
