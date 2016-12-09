package module.group;

import monitor.service.db.MySql;

public class Init {
	
	public static final String MODULE_NAME = "group";
	
	public static void init(String moduleDir) throws Exception {
//		XProperties properties = new XProperties(MODULE_NAME, moduleDir);
		
		MySql mysql = MySql.getInst(MODULE_NAME);
		GroupModel model = new GroupModel(mysql);
	}
    
}
