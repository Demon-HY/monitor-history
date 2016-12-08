package module.host;

import monitor.utils.XProperties;

public class Init {
	
	public static final String MODULE_NAME = "host";
	
	public static void init(String moduleDir) throws Exception {
		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
	}
    
}
