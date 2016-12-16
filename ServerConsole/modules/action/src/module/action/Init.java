package module.action;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "action";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		ActionModel model = new ActionModel(mysql);
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		ActionApi.init(beans, model);
		ActionHttpApi.init(ActionApi.getInst());
		
		SdkCenter.getInst().registHttpApi(MODULE_NAME, ActionHttpApi.getInst());
	}
    
}
