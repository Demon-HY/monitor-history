package module.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.TriggerExpressionInfo;
import module.SDK.info.TriggerInfo;
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
					+ "`trigger_expression_id` bigint(20) NOT NULL AUTO_INCREMENT,"
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
	
	/******************************************* Trigger ********************************************/
	public List<TriggerInfo> listTrigger(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "select `trigger_id`,`name`,`severity`,`enabled`,`memo` from `" + TABLE_TRIGGER + "` ";
			String factors = "";
			String limit = "";
            if (null != pageIndex && pageIndex > 0 && null != pageSize && pageSize > 0) {
                limit = String.format(" limit %s, %s", (pageIndex - 1) * pageSize, pageSize);
            }
            sql = String.format("%s %s %s", sql, (factors.length() > 0 ? "where" : ""), factors);
            
            if (null != sort && sort.length() > 0) {
            	sort = "`" + sort + "`";
            }
            // 防止输入的排序字段错误
            if (order.equals("asc")) {
                sql += " order by " + StringEscapeUtils.escapeSql(sort) + " asc ";
            } else {
                sql += " order by " + StringEscapeUtils.escapeSql(sort) + " desc ";
            }
            sql += limit;
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            return parseTriggers(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}
	
	public Integer countTrigger() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS from information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_TRIGGER + "'";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}

	private List<TriggerInfo> parseTriggers(ResultSet rs) throws SQLException {
		List<TriggerInfo> listTriggers = new LinkedList<>();
		while (rs.next()) {
			TriggerInfo trg = new TriggerInfo();
			trg.trigger_id = rs.getLong("trigger_id");
			trg.name = rs.getString("name");
			trg.severity = rs.getInt("severity");
			trg.enabled = rs.getInt("enabled");
			trg.memo = rs.getString("memo");
			
			listTriggers.add(trg);
		}
		
		return listTriggers;
	}
	
	@SuppressWarnings("unused")
	private TriggerInfo parseTrigger(ResultSet rs) throws SQLException {
		TriggerInfo trigger = new TriggerInfo();
		if (rs.next()) {
			TriggerInfo trg = new TriggerInfo();
			trg.trigger_id = rs.getLong("trigger_id");
			trg.name = rs.getString("name");
			trg.severity = rs.getInt("severity");
			trg.enabled = rs.getInt("enabled");
			trg.memo = rs.getString("memo");
		}
		
		return trigger;
	}
	
	/******************************************* Trigger Expression ********************************************/
	public List<TriggerExpressionInfo> listTriggerExpression(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "select `trigger_id`,`trigger_expression_id`,`service_id`,`service_index_id`,`key`,`operator_type`,`func`,`params`,"
					+ "`threshold`,`logic_type` from `" + TABLE_TRIGGER_EXPRESSION + "` ";
			
			String factors = "";
			String limit = "";
            if (null != pageIndex && pageIndex > 0 && null != pageSize && pageSize > 0) {
                limit = String.format(" limit %s, %s", (pageIndex - 1) * pageSize, pageSize);
            }
            sql = String.format("%s %s %s", sql, (factors.length() > 0 ? "where" : ""), factors);
            
            if (null != sort && sort.length() > 0) {
            	sort = "`" + sort + "`";
            }
            // 防止输入的排序字段错误
            if (order.equals("asc")) {
                sql += " order by " + StringEscapeUtils.escapeSql(sort) + " asc ";
            } else {
                sql += " order by " + StringEscapeUtils.escapeSql(sort) + " desc ";
            }
            sql += limit;
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            return parseTriggerExpressions(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}
	
	public Integer countTriggerExpression() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS from information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_TRIGGER_EXPRESSION + "'";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	private List<TriggerExpressionInfo> parseTriggerExpressions(ResultSet rs) throws SQLException {
		List<TriggerExpressionInfo> listExpressions = new LinkedList<>();
		while (rs.next()) {
			TriggerExpressionInfo expression = new TriggerExpressionInfo();
			expression.trigger_expression_id = rs.getLong("trigger_expression_id");
			expression.trigger_id = rs.getLong("trigger_id");
			expression.service_id = rs.getLong("service_id");
			expression.service_index_id = rs.getLong("service_index_id");
			expression.key = rs.getString("key");
			expression.operator_type = rs.getString("operator_type");
			expression.func = rs.getString("func");
			expression.params = rs.getString("params");
			expression.threshold = rs.getLong("threshold");
			expression.logic_type = rs.getString("logic_type");
			
			listExpressions.add(expression);
		}
		return listExpressions;
	}
	
	@SuppressWarnings("unused")
	private TriggerExpressionInfo parseTriggerExpression(ResultSet rs) throws SQLException {
		TriggerExpressionInfo expression = new TriggerExpressionInfo();
		if (rs.next()) {
			expression.trigger_expression_id = rs.getLong("trigger_expression_id");
			expression.trigger_id = rs.getLong("trigger_id");
			expression.service_id = rs.getLong("service_id");
			expression.service_index_id = rs.getLong("service_index_id");
			expression.key = rs.getString("key");
			expression.operator_type = rs.getString("operator_type");
			expression.func = rs.getString("func");
			expression.params = rs.getString("params");
			expression.threshold = rs.getLong("threshold");
			expression.logic_type = rs.getString("logic_type");
		}
		return expression;
	}
}
