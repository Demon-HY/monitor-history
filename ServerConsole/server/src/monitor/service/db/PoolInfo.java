package monitor.service.db;

/**
 * 数据库连接信息
 * @author monitor
 *
 */
public class PoolInfo {

    public String host;
    public int port;
    public String user;
    public String psw;
    public String params;
    
    public int maxActive;
    public int maxIdle;
    public long maxWait;
    public long timeBetweenEvictionRunsMillis;
    public int numTestsPerEvictionRun;
    public long minEvictableIdleTimeMillis;
    
    public PoolInfo(String host, int port, String user, String psw, String params, int maxActive, int maxIdle, long maxWait,
            long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis) {
        
        this.host = host;
        this.port = port;
        this.user = user;
        this.psw = psw;
        this.params = params;
        
        this.maxActive = maxActive;
        this.maxIdle = maxIdle;
        this.maxWait = maxWait;
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        
    }
    
    /**
     * 没有用户名、密码、编码格式
     */
    public PoolInfo(String host, int port, int maxActive, int maxIdle, long maxWait, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis) {
        this.host = host;
        this.port = port;
        
        this.maxActive = maxActive;
        this.maxIdle = maxIdle;
        this.maxWait = maxWait;
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
}
