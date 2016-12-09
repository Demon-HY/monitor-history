package module.host;

import java.sql.Connection;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class HostModel {
	
	private MySql mysql;
	private static final String TABLE_HOST = "host";
	private static final String TABLE_GROUP = "group";
	private static final String TABLE_HOST_GROUP = "host_group";
	
	public HostModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}
	
	private void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
        try {
			String hostSql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST + "` ("
				+ "`name` varchar(64) NOT NULL,"
				+ "`ip` varchar(16) NOT NULL,"
				+ "`monitored` varchar(16) NOT NULL,"
				+ "`host_alive_check_interval` int(16) NOT NULL,"
				+ "UNIQUE KEY (`type`, `value`)\n"
	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(hostSql);

			String groupSql = "CREATE TABLE IF NOT EXISTS `" + TABLE_GROUP + "` ("
                + "`token` varchar(128) PRIMARY KEY,"
                + "`uid` bigint(20) UNSIGNED NOT NULL,"
                + "`expires` datetime NOT NULL,"
                + "`ctime` datetime NOT NULL,"
                + "`ip` varchar(32) DEFAULT NULL,"
                + "`device` varchar(8) DEFAULT NULL"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            conn.createStatement().executeUpdate(groupSql);
		} catch (SQLException e) {
			throw new SQLException("SQL create failed...");
		} finally {
		    if (conn != null) {
		        conn.close();
		    }
		}
	}

}
