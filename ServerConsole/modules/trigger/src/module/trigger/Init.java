package module.trigger;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "trigger";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		TriggerModel model = new TriggerModel(mysql);
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		TriggerApi.init(beans, model);
		TriggerHttpApi.init(TriggerApi.getInst());
		
		SdkCenter.getInst().registHttpApi(MODULE_NAME, TriggerHttpApi.getInst());
	}
    
}
