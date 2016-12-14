package module.maintain;

import monitor.exception.UnInitilized;

public class MaintainHttpApi {

	private MaintainApi maintainApi;
	private static MaintainHttpApi maintainHttpApi;
	private MaintainHttpApi(MaintainApi maintainApi) {
		this.maintainApi = maintainApi;
	}
	
	public static void init(MaintainApi maintainApi) {
		maintainHttpApi = new MaintainHttpApi(maintainApi);
	}
	
	public static MaintainHttpApi getInst() {
		if (null == maintainHttpApi) {
			new UnInitilized();
		}
		return maintainHttpApi;
	}
}
