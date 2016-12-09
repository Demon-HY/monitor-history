package monitor.service.db;

//@javadoc

public class MysqlConfig {

    public static final String CONF_MONITOR_MYSQL_DB = "monitor";
    
    public static final String CONF_MONITOR_MYSQL_HOST = "monitor.mysql.host";
    public static final String CONF_MONITOR_MYSQL_PORT = "monitor.mysql.port";
    public static final String CONF_MONITOR_MYSQL_PARAMS = "monitor.mysql.params";
    public static final String CONF_MONITOR_MYSQL_USER = "monitor.mysql.user";
    public static final String CONF_MONITOR_MYSQL_PSW = "monitor.mysql.password";

    public static final String CONF_MONITOR_MYSQL_POOL_MAX_ACTIVE = "monitor.mysql.pool.maxActive";
    public static final String CONF_MONITOR_MYSQL_POOL_MAX_IDLE = "monitor.mysql.pool.maxIdle";
    public static final String CONF_MONITOR_MYSQL_POOL_MAX_WAIT = "monitor.mysql.pool.maxWait";
    
    public static final String CONF_MONITOR_MYSQL_POOL_TIME_BETWEEN_EVICTION_RUNS_MILLIS = "monitor.mysql.pool.timeBetweenEvictionRunsMillis";
    public static final String CONF_MONITOR_MYSQL_POOL_NUM_TEST_PER_EVICTION_RUN = "monitor.mysql.pool.numTestsPerEvictionRun";
    public static final String CONF_MONITOR_MYSQL_POOL_MIN_EVICTABLE_TIME_MILLIS = "monitor.mysql.pool.minEvictableIdleTimeMillis";
    
}
