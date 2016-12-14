package module.group;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import module.SDK.inner.IGroupApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

public class GroupApi implements IGroupApi{
	protected IBeans beans;
	protected GroupModel groupModel;
	private static GroupApi groupApi;
	private GroupApi(IBeans beans, GroupModel groupModel) throws LogicalException {
		this.beans = beans;
		this.groupModel = groupModel;
		
		SdkCenter.getInst().addInterface(IGroupApi.name, this);
	}
	
	public static void init(IBeans beans, GroupModel groupModel) throws LogicalException {
		groupApi = new GroupApi(beans, groupModel);
	}
	
	public static GroupApi getInst() throws UnInitilized {
		if (null == groupApi) {
			throw new UnInitilized();
		}
		return groupApi;
	}
}
