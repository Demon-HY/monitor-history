package module.template;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "template";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		TemplateModel model = new TemplateModel(mysql);
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		TemplateApi.init(beans, model);
		TemplateHttpApi.init(TemplateApi.getInst());
		
		SdkCenter.getInst().registHttpApi(MODULE_NAME, TemplateHttpApi.getInst());
	}
    
}
