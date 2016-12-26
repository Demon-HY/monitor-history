package module.action;

import java.sql.Connection;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class ActionModel {

	public static final String TABLE_ACTION = "action";
	public static final String TABLE_ACTION_OPERATION = "action_operation";
	public static final String TABLE_ACTION_HOST = "action_host";
	public static final String TABLE_ACTION_GROUP = "action_group";
	public static final String TABLE_ACTION_TRIGGER = "action_trigger";
	private MySql mysql;

	public ActionModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_ACTION + "` (" 
					+ "`action_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(64) NOT NULL,"
					+ "`interval` int(11) NOT NULL,"
					+ "`notice` tinyint(1) NOT NULL,"
					+ "`subject` varchar(1024) DEFAULT NULL,"
					+ "`message` varchar(4096) DEFAULT NULL,"
					+ "`enabled` tinyint(1) NOT NULL,"
					+ "PRIMARY KEY (`action_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_ACTION_OPERATION + "` (" 
					+ "`action_operation_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`action_id` bigint(20) NOT NULL,"
					+ "`name` varchar(64) NOT NULL,"
					+ "`step` int(4) NOT NULL,"
					+ "`action_type` varchar(32) NOT NULL," // 动作类型：email，sms，RunScript
					+ "`msg_format` varchar(1024) NOT NULL,"
					+ "PRIMARY KEY (`action_operation_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_ACTION_HOST + "` (" 
					+ "`action_id` bigint(20) NOT NULL,"
					+ "`host_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_ACTION_GROUP + "` (" 
					+ "`action_id` bigint(20) NOT NULL,"
					+ "`group_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_ACTION_TRIGGER + "` (" 
					+ "`action_id` bigint(20) NOT NULL,"
					+ "`trigger_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
}
