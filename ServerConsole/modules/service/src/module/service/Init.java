package module.service;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "service";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		ServiceModel model = new ServiceModel(mysql);
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		ServiceApi.init(beans, model);
		ServiceHttpApi.init(ServiceApi.getInst());
		
		SdkCenter.getInst().registHttpApi(MODULE_NAME, ServiceHttpApi.getInst());
	}
    
}
