package monitor.service.license;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import monitor.exception.UnInitilized;
import monitor.service.log.Logger;
import monitor.utils.StringUtils;
import monitor.utils.system.OS;
import monitor.utils.unit.TimeUnit;

public class LicenseManager {

    public static final String CONF_MONITOR_LICENSE = "monitor.license";
    private Logger logger;
    private Map<String, Object> license;

    private LicenseManager(String license, Logger logger) {
        Map<String, Object> map = null;
        try {
            map = LicenseUtil.parseLicense(license);
        } catch (Exception e) {
            String text = "illegal license.";
            logger.err(text);
            System.exit(0);
        }

        this.license = map;
        this.logger = logger;
    }

    private static LicenseManager s_lm;
    public static void init(String license, Logger logger) {
    	s_lm = new LicenseManager(license, logger);
    }
    public static LicenseManager getInst() throws UnInitilized {
        if (s_lm == null) {
            throw new UnInitilized("License Manager init failed.");
        }
        return s_lm;
    }


//    public static String createLicense(Object... params) {
//
//        Long expires = -1L;
//        Long space = 100 * 1024L * 1024 * 1024 * 1024L;
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("expires", expires);
//        map.put("space", space);
//
//        return XCodeUtil.xEncode(map);
//    }

//    public static void main(String[] args) {
//
//        String l = createLicense();
//        l = "7MVK1JVGzhfAScieHthRAVQfwFGdWZucWFHHEE9RRYwIgUsMQgSWXsGQ25zKTExE1VBcgdDdWJtdWFIAld4Bg7-gVriAAAAAA";
//        System.out.println("license:\t" + l);
//
//         Map<String, Object> map = parseLicense(l);
//         System.out.println("expires: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date((Long)map.get("expires"))));
//         System.out.println("user: " + map.get("users_max"));
//         System.out.println("space: " + new monitor.utils.unit.BitUnit(map.get("space").toString()).toStrWithUnit());
//    }

    public boolean validateLocal() {
    	return validateHardware() && validateExpiredTime();
    }
    private boolean validateHardware() {
        boolean ret = false;
        try {
            Object hardware = this.license.get(LicenseUtil.s_hardware);
            if(null != hardware) {
                String systemInfo = OS.getSystemInfo();
                systemInfo = systemInfo.hashCode() + "";
                systemInfo = systemInfo.substring(3, 5);
                if(hardware instanceof String) {
                    String hardwareInfo = (String) hardware;
                    if(true == hardwareInfo.contains(systemInfo)) {
                    	ret = true;
                    }
                }
            }
        } catch (Exception e) {
            logger.err("error license, " , e);
            System.err.println("error license");
        }
        return ret;
    }
    private boolean validateExpiredTime() {
        boolean ret = false;
        try {
            Object value = this.license.get(LicenseUtil.s_endDate);
            if (null != value) {
                long expires = Long.parseLong(value.toString()) * TimeUnit.VALUE_DAY;
                if (expires == -1 || expires > System.currentTimeMillis())
                	ret = true;
            }
        } catch (Exception e) {
            logger.err("error license, " , e);
            System.err.println("error license");
        }
    	
        return ret;
    }
    public void run() {
    	if (false == validateLocal()) {
            logger.err("error license");
            System.err.println("error license");
            System.exit(0);            	
    	}
    }

}

class LicenseUtil {
	public static final String s_endDate = "endDate";
	public static final String s_company = "company";
	public static final String s_hardware = "hardware";
	
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseLicense(String license) {
        List<Object> list = (List<Object>) JSONObject.parse(StringUtils.getString(license));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(s_endDate, list.get(0));
        map.put(s_company, list.get(1));
        map.put(s_hardware, list.get(2));
        list.clear();

        return map;
    }
}