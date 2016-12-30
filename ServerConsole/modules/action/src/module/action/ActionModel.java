package module.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.ActionInfo;
import module.SDK.info.ActionOperationInfo;
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
					+ "`host_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`action_id`, `host_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			// 这里是绑定一组触发器，因为group下关联有多个触发器
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_ACTION_GROUP + "` (" 
					+ "`action_id` bigint(20) NOT NULL,"
					+ "`group_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`action_id`, `group_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_ACTION_TRIGGER + "` (" 
					+ "`action_id` bigint(20) NOT NULL,"
					+ "`trigger_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`action_id`, `trigger_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
	
	/******************************************* Action ********************************************/
	public List<ActionInfo> listAction(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "select `action_id`,`name`,`interval`,`notice`,`subject`,`message`,`enabled` from `" + TABLE_ACTION + "` ";
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
            
            return parseActions(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}

	public Integer countAction() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS from information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_ACTION + "'";

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
	
	private List<ActionInfo> parseActions(ResultSet rs) throws SQLException {
		List<ActionInfo> listActions = new LinkedList<>();
		while (rs.next()) {
			ActionInfo action = new ActionInfo();
			action.action_id = rs.getLong("action_id");
			action.name = rs.getString("name");
			action.interval = rs.getLong("interval");
			action.notice = rs.getInt("notice");
			action.subject = rs.getString("subject");
			action.message = rs.getString("message");
			action.enabled = rs.getInt("enabled");
			
			listActions.add(action);
		}
		return listActions;
	}
	
	@SuppressWarnings("unused")
	private ActionInfo parseAction(ResultSet rs) throws SQLException {
		ActionInfo listActions = new ActionInfo();
		ActionInfo action = new ActionInfo();
		if (rs.next()) {
			action.action_id = rs.getLong("action_id");
			action.name = rs.getString("name");
			action.interval = rs.getLong("interval");
			action.notice = rs.getInt("notice");
			action.subject = rs.getString("subject");
			action.message = rs.getString("message");
			action.enabled = rs.getInt("enabled");
		}
		return listActions;
	}
	
	/******************************************* Action Operation ********************************************/
	public List<ActionOperationInfo> listActionOperation(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "select `action_operation_id`,`action_id`,`name`,`step`,`action_type`,`msg_format` from `" + TABLE_ACTION_OPERATION + "` ";
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
            
            return parseActionOperations(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}
	
	public Integer countActionOperation() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS from information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_ACTION_OPERATION + "'";

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
	
	private List<ActionOperationInfo> parseActionOperations(ResultSet rs) throws SQLException {
		List<ActionOperationInfo> listActionOperations = new LinkedList<>();
		while (rs.next()) {
			ActionOperationInfo action = new ActionOperationInfo();
			action.action_operation_id = rs.getLong("action_operation_id");
			action.action_id = rs.getLong("action_id");
			action.name = rs.getString("name");
			action.step = rs.getInt("step");
			action.action_type = rs.getString("action_type");
			action.msg_format = rs.getString("msg_format");
			
			listActionOperations.add(action);
		}
		return listActionOperations;
	}
}
