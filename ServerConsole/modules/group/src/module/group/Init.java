package module.group;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "group";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		GroupModel model = new GroupModel(mysql);
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		GroupApi.init(beans, model);
		GroupHttpApi.init(GroupApi.getInst());
		
		SdkCenter.getInst().registHttpApi(MODULE_NAME, GroupHttpApi.getInst());
	}
    
}
