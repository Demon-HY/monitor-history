package module.action;

import monitor.exception.UnInitilized;

public class ActionHttpApi {

	private ActionApi actionApi;
	private static ActionHttpApi actionHttpApi;
	private ActionHttpApi(ActionApi actionApi) {
		this.actionApi = actionApi;
	}
	
	public static void init(ActionApi actionApi) {
		actionHttpApi = new ActionHttpApi(actionApi);
	}
	
	public static ActionHttpApi getInst() {
		if (null == actionHttpApi) {
			new UnInitilized();
		}
		return actionHttpApi;
	}
}
