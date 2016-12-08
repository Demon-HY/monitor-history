package module.auth;

import module.SDK.SdkCenter;
import module.SDK.http.AuthedJsonProtocol;
import module.SDK.inner.IBeans;
import monitor.service.db.MySql;
import monitor.utils.XProperties;
import monitor.utils.unit.TimeUnit;

public class Init {
	
	public static final String MODULE_NAME = "auth";
	
	public static void init(String moduleDir) throws Exception {
		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		AuthConfig.defaultTokenAge = new TimeUnit(properties.getProperty(AuthConfig.CONF_USER_TOKEN_AGE)).value.longValue();
		
		IBeans beans = (IBeans) SdkCenter.getInst().queryInterface(IBeans.name, "monitor1.0InnerKeyP@ssw0rd");
		MySql mysql = MySql.getInst(MODULE_NAME);
		
		AuthModel authModel = new AuthModel(mysql);
		AuthApi.init(beans, authModel);
		AuthHttpApi.init(AuthApi.getInst());
		
		SdkCenter.getInst().registHttpApi(MODULE_NAME, AuthHttpApi.getInst());
		
		AuthedJsonProtocol.init();
	}
}
