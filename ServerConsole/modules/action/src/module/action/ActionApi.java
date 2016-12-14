package module.action;

import module.SDK.SdkCenter;
import module.SDK.inner.IActionApi;
import module.SDK.inner.IBeans;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

public class ActionApi implements IActionApi{
	protected IBeans beans;
	protected ActionModel actionModel;
	private static ActionApi actionApi;
	private ActionApi(IBeans beans, ActionModel actionModel) throws LogicalException {
		this.beans = beans;
		this.actionModel = actionModel;
		
		SdkCenter.getInst().addInterface(IActionApi.name, this);
	}
	
	public static void init(IBeans beans, ActionModel actionModel) throws LogicalException {
		actionApi = new ActionApi(beans, actionModel);
	}
	
	public static ActionApi getInst() throws UnInitilized {
		if (null == actionApi) {
			throw new UnInitilized();
		}
		return actionApi;
	}
}
