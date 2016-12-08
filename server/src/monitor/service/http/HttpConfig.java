package monitor.service.http;

import monitor.Config;

public class HttpConfig {

    public static final String CONF_MONITOR_HTTP_HOST = "monitor.http.host";
    public static final String CONF_MONITOR_HTTP_PORT = "monitor.http.port";

    public static final String CONF_MONITOR_HTTP_WEB_CONTEXT_PATH = "monitor.http.webContextPath";
    public static final String CONF_MONITOR_HTTP_WEB_WELCOM_PAGE = "monitor.http.webWelcomePage";

    public static final String CONF_MONITOR_HTTPS_PORT = "monitor.http.ssl.port";
    public static final String CONF_MONITOR_HTTPS_KEY_PATH = "monitor.http.ssl.kspath";
    public static final String CONF_MONITOR_HTTPS_KEY_PWD = "monitor.http.ssl.kspwd";

    public static final String CONF_MONITOR_THREAD_MAX = "monitor.http.thread.max";
    public static final String CONF_MONITOR_THREAD_MIN = "monitor.http.thread.min";
    public static final String CONF_MONITOR_THREAD_IDLE = "monitor.http.thread.idleTimeout";
    
    public static final String CONF_MONITOR_IO_TIMEOUT = "monitor.http.io.timeout";
    
    public static HttpServer.ServerConfig load() {
        HttpServer.ServerConfig conf = new HttpServer.ServerConfig();
        
        conf.port = Integer.parseInt(Config.get(CONF_MONITOR_HTTP_PORT));
        conf.host = Config.get(CONF_MONITOR_HTTP_HOST);
        
        if (Config.get(CONF_MONITOR_HTTPS_PORT) != null && 
            Config.get(CONF_MONITOR_HTTPS_KEY_PATH) != null &&
            Config.get(CONF_MONITOR_HTTPS_KEY_PWD) != null) {
            conf.useSSL = true;
            conf.sslPort = Integer.parseInt(Config.get(CONF_MONITOR_HTTPS_PORT));
            conf.sslKeyStorePath = Config.get(CONF_MONITOR_HTTPS_KEY_PATH);
            conf.sslKeyStorePwd = Config.get(CONF_MONITOR_HTTPS_KEY_PWD);
        }
        
        if (Config.get(CONF_MONITOR_THREAD_MAX) != null && 
            Config.get(CONF_MONITOR_THREAD_MIN) != null &&
            Config.get(CONF_MONITOR_THREAD_IDLE) != null) {
                conf.customizeThreadPool = true;
                conf.maxThread = Integer.parseInt(Config.get(CONF_MONITOR_THREAD_MAX));
                conf.minThread = Integer.parseInt(Config.get(CONF_MONITOR_THREAD_MIN));
                conf.threadIdleTimeout = Integer.parseInt(Config.get(CONF_MONITOR_THREAD_IDLE));
        }
        
        if (Config.get(CONF_MONITOR_IO_TIMEOUT) != null) {
            conf.ioTimeout = Integer.parseInt(Config.get(CONF_MONITOR_IO_TIMEOUT));
        }
        
        return conf;
    }
}
