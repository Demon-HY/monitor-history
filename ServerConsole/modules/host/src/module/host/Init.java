package module.host;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "host";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		HostModel model = new HostModel(mysql);
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		HostApi.init(beans, model);
		HostHttpApi.init(HostApi.getInst());
		
		SdkCenter.getInst().registHttpApi(MODULE_NAME, HostHttpApi.getInst());
	}
	
	
    
}
