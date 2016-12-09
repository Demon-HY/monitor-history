package monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 管理monitor启动时记载的配置文件
 */
public class Config {

    public static final String CONF_MONITOR_MODULE_DIR = "monitor.module.dir";
    public static final String CONF_MONITOR_MODULE_DIR_THIRD = "monitor.module.dir.third";
    
    private static Properties prop = null;
    private static String configDir = null;
    
    public static void init(String path) throws Exception {
        if (prop != null) {
            throw new Exception("Already initialized");
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("Config file not exists.");
        }
        configDir = file.getAbsoluteFile().getParent();
        
        InputStream input = new FileInputStream(file);
        prop = new Properties();
        prop.load(input);
        input.close();
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }
    
    public static String get(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }
    
    public static String getConfigDir() {
        return configDir;
    }
    
    public static String ToString() {
        return "P@ssw0rd";
    }
    
    public static void set(String key, String value) {
        prop.setProperty(key, value);
    }
}
