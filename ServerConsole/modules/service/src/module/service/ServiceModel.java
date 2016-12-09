package module.service;

import java.sql.Connection;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class ServiceModel {

	public static final String TABLE_SERVICE = "service";
	public static final String TABLE_SERVICE_INDEX = "service_index";
	private MySql mysql;

	public ServiceModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_SERVICE + "` (" 
					+ "`service_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`interval` int(10) NOT NULL DEFAULT 5,"
					+ "`plugin_name` varchar(255) NOT NULL DEFAULT 'n/a',"
					+ "`has_sub_service` tinyint(1) NOT NULL,"
					+ "`memo` varchar(1024) DEFAULT NULL,"
					+ "PRIMARY KEY (`service_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_SERVICE_INDEX + "` (" 
					+ "`service_index_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`service_id` bigint(20) NOT NULL,"
					+ "`name` varchar(32) NOT NULL,"
					+ "`key` varchar(32) NOT NULL,"
					+ "`type` varchar(32) NOT NULL DEFAULT 'int'," // 指标数据类型,默认 int
                    + "PRIMARY KEY (`service_index_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
}
