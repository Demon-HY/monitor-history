package module.SDK.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;

import monitor.exception.UnInitilized;
import monitor.service.db.MysqlConfig;
import monitor.service.db.PoolInfo;
import monitor.Config;

public class DBConnector {
	
	private DataSource ds;
	
	public DBConnector(DataSource dataSource) {
		this.ds = dataSource;
	}
	
	/**
	 * 获取数据库 monitor_3rdparty 连接。
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return this.ds.getConnection();
	}
	
	
	/*********************************************/

    private static DataSource dataSource3rdParty;
    private static final String DB_3RD_PARTY = "monitor_3rdParty";
	
	/**
	 * 获取DBUtils实例对象。
	 * @param moduleName
	 * @return
	 * @throws UnInitilized
	 */
	public static DBConnector getInst() throws UnInitilized {
		if (dataSource3rdParty == null) {
            throw new UnInitilized("DBConnector not inited.");
        }
		return new DBConnector(dataSource3rdParty);
	}
	
	public static void init() throws 
		InstantiationException, IllegalAccessException, 
		ClassNotFoundException, SQLException, UnInitilized {
		
		String dbUser_encrypted = Config.get(MysqlConfig.CONF_MONITOR_MYSQL_USER);
    	String dbPwd_encrypted = Config.get(MysqlConfig.CONF_MONITOR_MYSQL_PSW);
        
		PoolInfo pool = new PoolInfo(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_HOST),
        Integer.parseInt(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_PORT)),
        dbUser_encrypted,
        dbPwd_encrypted,
        Config.get(MysqlConfig.CONF_MONITOR_MYSQL_PARAMS),
        Integer.parseInt(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_MAX_ACTIVE)),
        Integer.parseInt(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_MAX_IDLE)),
        Long.parseLong(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_MAX_WAIT)),
        Long.parseLong(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_TIME_BETWEEN_EVICTION_RUNS_MILLIS)),
        Integer.parseInt(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_NUM_TEST_PER_EVICTION_RUN)),
        Long.parseLong(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_MIN_EVICTABLE_TIME_MILLIS)));
        
		dataSource3rdParty = createDataSource(pool, DB_3RD_PARTY);
    }
	
	private static DataSource createDataSource(PoolInfo pool, String database)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException, UnInitilized {

        assureDatabase(pool.host, pool.port, pool.params, database, pool.user, pool.psw);
        
        String url = String.format("%s:%s/%s?%s", pool.host, pool.port, database, pool.params);
        
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, pool.user, pool.psw);

        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        poolableConnectionFactory.setValidationQuery("SELECT 1");
        poolableConnectionFactory.setDefaultReadOnly(false);
        poolableConnectionFactory.setDefaultAutoCommit(true);
        
        GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        
        connectionPool.setMaxTotal(pool.maxActive);
        connectionPool.setMaxIdle(pool.maxIdle);
        connectionPool.setMaxWaitMillis(pool.maxWait);
        connectionPool.setTestOnBorrow(false);
        connectionPool.setTestOnReturn(false);
        connectionPool.setTestWhileIdle(true);
        connectionPool.setNumTestsPerEvictionRun(pool.numTestsPerEvictionRun);
        connectionPool.setTimeBetweenEvictionRunsMillis(pool.timeBetweenEvictionRunsMillis);
        connectionPool.setMinEvictableIdleTimeMillis(pool.minEvictableIdleTimeMillis);
        
        poolableConnectionFactory.setPool(connectionPool);

        PoolingDataSource<PoolableConnection> ds = new PoolingDataSource<>(connectionPool);
        
        // Test
        Connection conn = ds.getConnection();
        conn.close();
        
        return ds;
    }

    private static void assureDatabase(String host, int port, String params, String database, String user, String psw) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, UnInitilized {
        String url = String.format("%s:%s?%s", host, port, params);
        
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection(url, user, psw);
        Statement statement = con.createStatement();
        
        String sql = "CREATE DATABASE IF NOT EXISTS " + database + " default character set utf8 collate utf8_general_ci";
        statement.executeUpdate(sql);
        
        sql = String.format("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'", database);
        ResultSet rs = statement.executeQuery(sql);
        if (!rs.next()) {
            throw new UnInitilized("Needed database not exists.");
        }
        statement.close();
        con.close();
    }
	
}


//class LicenseUtil {
//	public static final String s_endDate = "endDate";
//	public static final String s_quotaLimit = "quotaLimit";
//	public static final String s_userLimit = "userLimit";
//	public static final String s_company = "company";
//	public static final String s_hardware = "hardware";
//	
//    @SuppressWarnings("unchecked")
//    public static Map<String, Object> parseLicense(String license) {
//        List<Object> list = (List<Object>) JSONObject.parse(StringUtils.getString(license));
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put(s_endDate, list.get(0));
//        map.put(s_quotaLimit, list.get(1));
//        map.put(s_userLimit, list.get(2));
//        map.put(s_company, list.get(3));
//        map.put(s_hardware, list.get(4));
//        list.clear();
//
//        return map;
//    }
//    
//    public static Pair<String, String> genDatabaseInfo(String company) {
//        String dbUser = _subString(StringUtils.setString(company), 4, 10);
//        String dbPwd = _subString(StringUtils.setString(dbUser), 4, 10);
//        return new Pair<String, String>(dbUser, dbPwd);
//    }
//    
//    private static String _subString(String str, int begin, int len) {
//    	int end = begin + len;
//    	if (end >= str.length())
//    		end = str.length() - 1;
//    	return str.substring(begin, end);
//    }
//}