package module.template;

import java.sql.Connection;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class TemplateModel {

	public static final String TABLE_TEMPLATE = "template";
	public static final String TABLE_TEMP_SERVER = "template_service";
	public static final String TABLE_TEMP_TRIGGER = "template_trigger";
	private MySql mysql;

	public TemplateModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_TEMPLATE + "` (" 
					+ "`template_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`ctime` datetime DEFAULT NULL,"
                    + "`mtime` datetime DEFAULT NULL,"
					+ "PRIMARY KEY (`template_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_TEMP_SERVER + "` (" 
					+ "`template_id` bigint(20) NOT NULL,"
					+ "`service_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_TEMP_TRIGGER + "` (" 
					+ "`template_id` bigint(20) NOT NULL,"
					+ "`trigger_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
}
