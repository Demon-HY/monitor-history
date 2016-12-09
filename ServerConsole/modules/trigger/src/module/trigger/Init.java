package module.trigger;

import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "trigger";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		TriggerModel model = new TriggerModel(mysql);
	}
    
}
