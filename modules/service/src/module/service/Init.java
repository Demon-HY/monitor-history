package module.service;

import monitor.utils.XProperties;

public class Init {
	
	public static final String MODULE_NAME = "service";
	
	public static void init(String moduleDir) throws Exception {
		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
	}
    
}
