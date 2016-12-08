package module.SDK;

import module.SDK.db.DBConnector;
import module.SDK.instances.SdkInit;

public class Init {

    public static final String MODULE_NAME = "SDK";

    public static void init(String moduleDir) throws Exception {
    	DBConnector.init();
    	SdkInit.init();
    }
}
