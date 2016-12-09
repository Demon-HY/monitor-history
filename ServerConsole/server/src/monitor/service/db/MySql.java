package monitor.service.db;

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

public class MySql {

    @SuppressWarnings("unused")
    private String moduleName;
    private DataSource dataSource;
    
    public MySql(String moduleName, DataSource dataSource) {
        this.moduleName = moduleName;
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    /* ------------------ Static Methods -------------------------- */
    
    private static DataSource dataSourceWithBinlog;
//    private static DataSource dataSourceWithoutBinlog;
//    private static final String DB_STD = "monitor";
    
    /**
     * 获取有binlog的数据库的连接池
     * 
     * @param moduleName
     * @return
     * @throws UnInitilized
     */
    public static MySql getInst(String moduleName) throws UnInitilized {
        if (dataSourceWithBinlog == null) {
            throw new UnInitilized("MySql not inited.");
        }

        return new MySql(moduleName, dataSourceWithBinlog);
    }

    /**
     * 获取没有binlog的数据库的连接池
     * 
     * @param moduleName
     * @return
     * @throws UnInitilized
     */
    // TODO 运维脚本中，关闭 monitor_nobinlog 库的 binlog
//    public static MySql getNoBinlogInst(String moduleName) throws UnInitilized {
//        if (dataSourceWithBinlog == null) {
//            throw new UnInitilized("MySql not inited.");
//        }
//
//        return new MySql(moduleName, dataSourceWithoutBinlog);
//    }
    
    public static void init(PoolInfo pool)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException, UnInitilized {
        
        dataSourceWithBinlog = createDataSource(pool, MysqlConfig.CONF_MONITOR_MYSQL_DB);
    }
    
    private static DataSource createDataSource(PoolInfo pool, String database)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException, UnInitilized {

        assureDatabase(pool.host, pool.port, pool.params, database, pool.user, pool.psw);
        
        String url = String.format("%s:%s/%s?%s", pool.host, pool.port, database, pool.params);
        
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, pool.user, pool.psw);

        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        poolableConnectionFactory.setValidationQuery("select 1");
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

    /**
     * 保证数据库被创建
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnInitilized
     */
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
