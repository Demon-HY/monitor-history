package module.trigger;

import java.sql.Connection;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class TriggerModel {

	public static final String TABLE_TRIGGER = "trigger";
	public static final String TABLE_TRIGGER_EXPRESSION = "trigger_expression";
	private MySql mysql;

	public TriggerModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_TRIGGER + "` (" 
					+ "`trigger_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`severity` tinyint(1) NOT NULL,"			// 告警级别:Information,Warning,Average,High,Diaster
					+ "`enabled` tinyint(1) NOT NULL," 		// 是否启动触发器
					+ "`memo` varchar(1024) DEFAULT NULL,"
					+ "PRIMARY KEY (`trigger_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_TRIGGER_EXPRESSION + "` (" 
					+ "`trigger_expression_id` bigint(20) NOT NULL,"
					+ "`trigger_id` bigint(20) NOT NULL,"
					+ "`service_id` bigint(20) NOT NULL,"
					+ "`service_index_id` bigint(20) NOT NULL,"
					+ "`key` varchar(32) NOT NULL," 			// 只监控专门指定的指标key，这里的key是monitor客户端返回的具体指标
					+ "`operator_type` varchar(8) NOT NULL," 	// 运算符
					+ "`func` varchar(32) NOT NULL," 			// 数据处理方式 
					+ "`params` varchar(64) NOT NULL," 			// 参数
					+ "`threshold` bigint(20) NOT NULL," 		// 阈值
					+ "`logic_type` varchar(8) NOT NULL," 		// 与一个条件的逻辑关系
					+ "PRIMARY KEY (`trigger_expression_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
}
