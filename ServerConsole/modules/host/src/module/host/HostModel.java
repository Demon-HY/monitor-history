package module.host;

import java.sql.Connection;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class HostModel {

	public static final String TABLE_HOST = "host";
	public static final String TABLE_HOST_HISTORY = "host_history";
	private MySql mysql;

	public HostModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST +  "` (" 
					+ "`host_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(128) NOT NULL,"
					+ "`ip` varchar(16) NOT NULL,"
					+ "`monitored` varchar(32) NOT NULL DEFAULT 'Agent',"
					+ "`status` varchar(32) NOT NULL,"
					+ "`interval` int(11) NOT NULL DEFAULT 60,"	// 主机存活状态监测间隔
					+ "`memo` varchar(1024) DEFAULT NULL,"
					+ "`ctime` datetime DEFAULT NULL,"
                    + "`mtime` datetime DEFAULT NULL,"
					+ "PRIMARY KEY (`host_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST_HISTORY +  "` (" 
					+ "`host_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(128) NOT NULL,"
					+ "`ip` varchar(16) NOT NULL,"
					+ "`monitored` varchar(32) NOT NULL DEFAULT 'Agent',"
					+ "`status` varchar(32) NOT NULL,"
					+ "`interval` varchar(32) NOT NULL DEFAULT 60,"	// 主机存活状态监测间隔
					+ "`memo` varchar(1024) DEFAULT NULL,"
					+ "`ctime` datetime DEFAULT NULL,"
                    + "`mtime` datetime DEFAULT NULL,"
					+ "PRIMARY KEY (`host_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
}
