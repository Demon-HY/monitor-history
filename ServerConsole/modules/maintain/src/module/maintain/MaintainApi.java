package module.maintain;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import module.SDK.inner.IMaintainApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

public class MaintainApi implements IMaintainApi{
	protected IBeans beans;
	protected MaintainModel maintainModel;
	private static MaintainApi maintainApi;
	private MaintainApi(IBeans beans, MaintainModel maintainModel) throws LogicalException {
		this.beans = beans;
		this.maintainModel = maintainModel;
		
		SdkCenter.getInst().addInterface(IMaintainApi.name, this);
	}
	
	public static void init(IBeans beans, MaintainModel maintainModel) throws LogicalException {
		maintainApi = new MaintainApi(beans, maintainModel);
	}
	
	public static MaintainApi getInst() throws UnInitilized {
		if (null == maintainApi) {
			throw new UnInitilized();
		}
		return maintainApi;
	}
}
