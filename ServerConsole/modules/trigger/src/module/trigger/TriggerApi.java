package module.trigger;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import module.SDK.inner.ITriggerApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

public class TriggerApi implements ITriggerApi{
	protected IBeans beans;
	protected TriggerModel triggerModel;
	private static TriggerApi triggerApi;
	private TriggerApi(IBeans beans, TriggerModel triggerModel) throws LogicalException {
		this.beans = beans;
		this.triggerModel = triggerModel;
		
		SdkCenter.getInst().addInterface(ITriggerApi.name, this);
	}
	
	public static void init(IBeans beans, TriggerModel triggerModel) throws LogicalException {
		triggerApi = new TriggerApi(beans, triggerModel);
	}
	
	public static TriggerApi getInst() throws UnInitilized {
		if (null == triggerApi) {
			throw new UnInitilized();
		}
		return triggerApi;
	}
}
