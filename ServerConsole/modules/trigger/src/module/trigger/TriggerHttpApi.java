package module.trigger;

import monitor.exception.UnInitilized;

public class TriggerHttpApi {

	private TriggerApi triggerApi;
	private static TriggerHttpApi triggerHttpApi;
	private TriggerHttpApi(TriggerApi triggerApi) {
		this.triggerApi = triggerApi;
	}
	
	public static void init(TriggerApi triggerApi) {
		triggerHttpApi = new TriggerHttpApi(triggerApi);
	}
	
	public static TriggerHttpApi getInst() {
		if (null == triggerHttpApi) {
			new UnInitilized();
		}
		return triggerHttpApi;
	}
}
