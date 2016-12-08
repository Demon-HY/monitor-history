package module.user;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "user";
	
	public static void init(String moduleDir) throws Exception {
//	    XProperties properties = new XProperties(MODULE_NAME, moduleDir);
	    
		MySql mysql = MySql.getInst(MODULE_NAME);
		UserModel userModel = new UserModel(mysql);
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		
	    UserApi.init(beans, userModel);
	    UserHttpApi.init(UserApi.getInst());
	    
	    SdkCenter.getInst().registHttpApi(MODULE_NAME, UserHttpApi.getInst());
	}
}
