package module.maintain;

import java.sql.Connection;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class MaintainModel {

	public static final String TABLE_MAINTAIN = "maintain";
	public static final String TABLE_MAINTAIN_HOST = "maintain_host";
	public static final String TABLE_MAINTAIN_GROUP = "maintain_group";
	private MySql mysql;

	public MaintainModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_MAINTAIN + "` ("
					+ "`maintain_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`content` varchar(4096) NOT NULL,"
					+ "`start_time` datetime DEFAULT NULL,"
                    + "`end_time` datetime DEFAULT NULL,"
					+ "PRIMARY KEY (`maintain_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_MAINTAIN_HOST + "` (" 
					+ "`maintain_id` bigint(20) NOT NULL,"
					+ "`host_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_MAINTAIN_GROUP + "` (" 
					+ "`maintain_id` bigint(20) NOT NULL,"
					+ "`group_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
}
