package monitor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import monitor.Config;

public class XProperties {

    private Properties baseProperties;
    private Properties customProperties;
    
    public XProperties(String moduleName, String moduleDir) throws IOException {
        this(moduleName, moduleDir, Config.getConfigDir());
    }
    
    public XProperties(String moduleName, String moduleDir, String thirdConfigDir) throws IOException {
        baseProperties = loadProperties(new File(moduleDir, "module.properties"));
        
        customProperties = loadProperties(new File(new File(thirdConfigDir, moduleName), "module.properties"));
    }
    
    private Properties loadProperties(File file) throws IOException {
        
        if (file.exists()) {
            InputStream input = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(input);
            input.close();
            return prop;
        }
        return null;
    }
    
    public String getProperty(String key) {
        
        String value = null;
        if (customProperties != null) {
            value = customProperties.getProperty(key);
            value = value == null ? value : value.trim();
        }
        
        if (null == value && baseProperties != null) {
            value = baseProperties.getProperty(key);
            value = value == null ? value : value.trim();
        }
        
        return value;
    }
    
    public Set<Object> keySet() {
        Set<Object> set = baseProperties == null ? null : baseProperties.keySet();
        
        if (null != customProperties) {
            if (set == null) {
                set = customProperties.keySet();
            } else {
                set.addAll(customProperties.keySet());
            }
        }
        
        return set;
    }
}
