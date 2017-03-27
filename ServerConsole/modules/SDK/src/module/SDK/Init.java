package module.SDK;

import module.SDK.db.DBConnector;
import module.SDK.instances.SdkInit;

import monitor.utils.LanguageUtil;

public class Init {

    public static final String MODULE_NAME = "SDK";

    public static void init(String moduleDir) throws Exception {
        LanguageUtil.getInst().regist(moduleDir + System.getProperty("file.separator") + "SDK.xml");
        
    	DBConnector.init();
    	SdkInit.init();
    }
}
