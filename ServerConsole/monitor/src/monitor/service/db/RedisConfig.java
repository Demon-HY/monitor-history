package monitor.service.db;

import java.util.HashMap;
import java.util.Map;

public class RedisConfig {

	public static final String CONF_MONITOR_REDIS_HOST = "monitor.redis.host";
    public static final String CONF_MONITOR_REDIS_PORT = "monitor.redis.port";

    public static final String CONF_MONITOR_REDIS_POOL_MAX_ACTIVE = "monitor.redis.pool.maxActive";
    public static final String CONF_MONITOR_REDIS_POOL_MAX_IDLE = "monitor.redis.pool.maxIdle";
    public static final String CONF_MONITOR_REDIS_POOL_MAX_WAIT = "monitor.redis.pool.maxWait";
    
    public static final String CONF_MONITOR_REDIS_POOL_TIME_BETWEEN_EVICTION_RUNS_MILLIS = "monitor.redis.pool.timeBetweenEvictionRunsMillis";
    public static final String CONF_MONITOR_REDIS_POOL_NUM_TEST_PER_EVICTION_RUN = "monitor.redis.pool.numTestsPerEvictionRun";
    public static final String CONF_MONITOR_REDIS_POOL_MIN_EVICTABLE_TIME_MILLIS = "monitor.redis.pool.minEvictableIdleTimeMillis";
    
    public static final Map<String, int[]> statusData = new HashMap<String, int[]>(){
		private static final long serialVersionUID = 1L;
	{
    	put("latest", new int[]{0, 600});
    	put("10mins", new int[]{600, 600});
    	put("30mins", new int[]{1800, 600});
    	put("60mins", new int[]{3600, 600});
    }};
}
