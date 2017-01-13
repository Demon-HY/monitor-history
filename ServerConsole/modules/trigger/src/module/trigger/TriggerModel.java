package module.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.ExpressionInfo;
import module.SDK.info.TriggerInfo;
import monitor.service.db.MySql;

public class TriggerModel {

	public static final String TABLE_TRIGGER = "trigger";
	public static final String TABLE_EXPRESSION = "expression";
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
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_EXPRESSION + "` (" 
					+ "`expression_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`trigger_id` bigint(20) NOT NULL,"
					+ "`service_id` bigint(20) NOT NULL,"
					+ "`index_id` bigint(20) NOT NULL,"
					+ "`key` varchar(32) NOT NULL," 			// 只监控专门指定的指标key，这里的key是monitor客户端返回的具体指标
					+ "`operator_type` varchar(8) NOT NULL," 	// 运算符
					+ "`func` varchar(32) NOT NULL," 			// 数据处理方式 
					+ "`params` varchar(64) NOT NULL," 			// 参数
					+ "`threshold` bigint(20) NOT NULL," 		// 阈值
					+ "`logic_type` varchar(8) NOT NULL," 		// 与一个条件的逻辑关系
					+ "PRIMARY KEY (`expression_id`)"
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
			String sql = "SELECT `trigger_id`,`name`,`severity`,`enabled`,`memo` FROM `" + TABLE_TRIGGER + "` ";
			String factors = "";
			String limit = "";
            if (null != pageIndex && pageIndex > 0 && null != pageSize && pageSize > 0) {
                limit = String.format(" limit %s, %s", (pageIndex - 1) * pageSize, pageSize);
            }
            sql = String.format("%s %s %s", sql, (factors.length() > 0 ? "WHERE" : ""), factors);
            
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
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_TRIGGER + "'";

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
	
	public boolean addTrigger(TriggerInfo triggerInfo) throws SQLException {
		if (null == triggerInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_TRIGGER + "` "
					+ "(`name`,`severity`,`enabled`,`memo`) "
					+ "values (?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, triggerInfo.name);
			pstmt.setInt(2, triggerInfo.severity);
			pstmt.setInt(3, triggerInfo.enabled);
			pstmt.setString(4, triggerInfo.memo);
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editTriggerByTriggerId(TriggerInfo triggerInfo) throws SQLException {
		if (null == triggerInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_TRIGGER + "` SET "
                    + "`name`=?,`severity`=?,`enabled`=?,`memo`=? "
                    + " WHERE `trigger_id`=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, triggerInfo.name);
			pstmt.setInt(2, triggerInfo.severity);
			pstmt.setInt(3, triggerInfo.enabled);
			pstmt.setString(4, triggerInfo.memo);
            pstmt.setLong(5, triggerInfo.trigger_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public TriggerInfo getTriggerByName(String triggerName) throws SQLException {
		if (null == triggerName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `trigger_id`,`name`,`severity`,`enabled`,`memo` FROM `"
					+ TABLE_TRIGGER + "` WHERE `name`=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, triggerName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseTrigger(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public TriggerInfo getTriggerByTriggerId(Long trigger_id) throws SQLException {
		if (null == trigger_id || trigger_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT `trigger_id`,`name`,`severity`,`enabled`,`memo` FROM `"
                    + TABLE_TRIGGER + "` WHERE `trigger_id`=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, trigger_id);
            ResultSet rs = pstmt.executeQuery();
            
            return parseTrigger(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public boolean deleteTriggerByTriggerId(Long trigger_id) throws SQLException {
		if (null == trigger_id || trigger_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_TRIGGER + "` WHERE `trigger_id`=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, trigger_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	/******************************************* Trigger Expression ********************************************/
	public List<ExpressionInfo> listExpression(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "SELECT `trigger_id`,`expression_id`,`service_id`,`index_id`,`key`,`operator_type`,`func`,`params`,"
					+ "`threshold`,`logic_type` FROM `" + TABLE_EXPRESSION + "` ";
			
			String factors = "";
			String limit = "";
            if (null != pageIndex && pageIndex > 0 && null != pageSize && pageSize > 0) {
                limit = String.format(" limit %s, %s", (pageIndex - 1) * pageSize, pageSize);
            }
            sql = String.format("%s %s %s", sql, (factors.length() > 0 ? "WHERE" : ""), factors);
            
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
            
            return parseExpressions(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}
	
	public Integer countExpression() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_EXPRESSION + "'";

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
	
	private List<ExpressionInfo> parseExpressions(ResultSet rs) throws SQLException {
		List<ExpressionInfo> listExpressions = new LinkedList<>();
		while (rs.next()) {
			ExpressionInfo expression = new ExpressionInfo();
			expression.expression_id = rs.getLong("expression_id");
			expression.trigger_id = rs.getLong("trigger_id");
			expression.service_id = rs.getLong("service_id");
			expression.index_id = rs.getLong("index_id");
			expression.key = rs.getString("key");
			expression.operator_type = rs.getString("operator_type");
			expression.func = rs.getString("func");
			expression.params = rs.getString("params");
			expression.threshold = rs.getDouble("threshold");
			expression.logic_type = rs.getString("logic_type");
			
			listExpressions.add(expression);
		}
		return listExpressions;
	}
	
	private ExpressionInfo parseExpression(ResultSet rs) throws SQLException {
		ExpressionInfo expression = new ExpressionInfo();
		if (rs.next()) {
			expression.expression_id = rs.getLong("expression_id");
			expression.trigger_id = rs.getLong("trigger_id");
			expression.service_id = rs.getLong("service_id");
			expression.index_id = rs.getLong("index_id");
			expression.key = rs.getString("key");
			expression.operator_type = rs.getString("operator_type");
			expression.func = rs.getString("func");
			expression.params = rs.getString("params");
			expression.threshold = rs.getDouble("threshold");
			expression.logic_type = rs.getString("logic_type");
		}
		return expression;
	}

	public boolean addExpression(ExpressionInfo expressionInfo) throws SQLException {
		if (null == expressionInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_EXPRESSION + "` "
					+ "(`trigger_id`,`service_id`,`index_id`,`key`,`operator_type`,`func`,`params`,`threshold`,`logic_type`) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, expressionInfo.trigger_id);
			pstmt.setLong(2, expressionInfo.service_id);
			pstmt.setLong(3, expressionInfo.index_id);
			pstmt.setString(4, expressionInfo.key);
			pstmt.setString(5, expressionInfo.operator_type);
			pstmt.setString(6, expressionInfo.func);
			pstmt.setString(7, expressionInfo.params);
			pstmt.setDouble(8, expressionInfo.threshold);
			pstmt.setString(9, expressionInfo.logic_type);
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editExpressionByExpressionId(ExpressionInfo expressionInfo) throws SQLException {
		if (null == expressionInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_EXPRESSION + "` SET "
                    + "`trigger_id`=?,`service_id`=?,`index_id`=?,`key`=?,`operator_type`=?,`func`=?,`params`=?,`threshold`=?,`logic_type`=? "
                    + "WHERE `expression_id`=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, expressionInfo.trigger_id);
			pstmt.setLong(2, expressionInfo.service_id);
			pstmt.setLong(3, expressionInfo.index_id);
			pstmt.setString(4, expressionInfo.key);
			pstmt.setString(5, expressionInfo.operator_type);
			pstmt.setString(6, expressionInfo.func);
			pstmt.setString(7, expressionInfo.params);
			pstmt.setDouble(8, expressionInfo.threshold);
			pstmt.setString(9, expressionInfo.logic_type);
            pstmt.setLong(10, expressionInfo.expression_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public ExpressionInfo getExpressionByName(String expressionName) throws SQLException {
		if (null == expressionName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `expression_id`,`trigger_id`,`service_id`,`index_id`,`key`,`operator_type`,"
					+ "`func`,`params`,`threshold`,`logic_type` FROM `"
					+ TABLE_EXPRESSION + "` WHERE `name`=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, expressionName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseExpression(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public ExpressionInfo getExpressionByExpressionId(Long expression_id) throws SQLException {
		if (null == expression_id || expression_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT `expression_id`,`trigger_id`,`service_id`,`index_id`,`key`,`operator_type`,"
					+ "`func`,`params`,`threshold`,`logic_type` FROM `"
                    + TABLE_EXPRESSION + "` WHERE `expression_id`=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, expression_id);
            ResultSet rs = pstmt.executeQuery();
            
            return parseExpression(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public boolean deleteExpressionByExpressionId(Long expression_id) throws SQLException {
		if (null == expression_id || expression_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_EXPRESSION + "` WHERE `expression_id`=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, expression_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
}
