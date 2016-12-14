package module.group;

import monitor.exception.UnInitilized;

public class GroupHttpApi {

	private GroupApi groupApi;
	private static GroupHttpApi groupHttpApi;
	private GroupHttpApi(GroupApi groupApi) {
		this.groupApi = groupApi;
	}
	
	public static void init(GroupApi groupApi) {
		groupHttpApi = new GroupHttpApi(groupApi);
	}
	
	public static GroupHttpApi getInst() {
		if (null == groupHttpApi) {
			new UnInitilized();
		}
		return groupHttpApi;
	}
}
