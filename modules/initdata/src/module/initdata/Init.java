package module.initdata;

import module.SDK.SdkCenter;
import module.SDK.inner.IBeans;
import monitor.service.http.Env;
import monitor.utils.XProperties;

public class Init {
	
	public static final String MODULE_NAME = "initdata";
	
	public static void init(String moduleDir) throws Exception {
		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		Env env = new Env(Init.MODULE_NAME);
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		
		// 初始化用户
		InitUser.initUser(env, properties, beans);
	}
    
}
