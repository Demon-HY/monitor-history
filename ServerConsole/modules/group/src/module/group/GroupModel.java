package module.group;

import java.sql.Connection;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class GroupModel {

	public static final String TABLE_GROUP = "group";
	private MySql mysql;

	public GroupModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_GROUP + "` (" 
					+ "`group_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`memo` varchar(1024) DEFAULT NULL,"
					+ "`ctime` datetime DEFAULT NULL,"
                    + "`mtime` datetime DEFAULT NULL,"
					+ "PRIMARY KEY (`group_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
}
