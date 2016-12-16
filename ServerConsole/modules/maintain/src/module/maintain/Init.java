package module.maintain;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "maintain";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		MaintainModel model = new MaintainModel(mysql);
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		MaintainApi.init(beans, model);
		MaintainHttpApi.init(MaintainApi.getInst());
		
		SdkCenter.getInst().registHttpApi(MODULE_NAME, MaintainHttpApi.getInst());
	}
    
}
