package module.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.ActionInfo;
import module.SDK.info.OperationInfo;
import monitor.service.db.MySql;
import monitor.utils.DBUtil;

public class ActionModel {

	public static final String TABLE_ACTION = "action";
	public static final String TABLE_ACTION_OPERATION = "action_operation";
	public static final String TABLE_ACTION_HOST = "action_host";
	public static final String TABLE_ACTION_GROUP = "action_group";
	public static final String TABLE_ACTION_TRIGGER = "action_trigger";
	public static final String TABLE_OPERATION = "operation";
	public static final String TABLE_OPERATION_USER = "TABLE_OPERATION_USER";
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
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_OPERATION + "` (" 
					+ "`operation_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(64) NOT NULL,"
					+ "`step` int(4) NOT NULL,"
					+ "`action_type` varchar(32) NOT NULL," // 动作类型：email，sms，RunScript
					+ "`msg_format` varchar(1024) NOT NULL,"
					+ "PRIMARY KEY (`operation_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_ACTION_OPERATION + "` (" 
					+ "`action_id` bigint(20) NOT NULL,"
					+ "`operation_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`action_id`, `operation_id`)"
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
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_OPERATION_USER + "` (" 
					+ "`operation_id` bigint(20) NOT NULL,"
					+ "`uid` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`operation_id`, `uid`)"
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
			String sql = "SELECT `action_id`,`name`,`interval`,`notice`,`subject`,`message`,`enabled` FROM `" + TABLE_ACTION + "` ";
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
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_ACTION + "'";

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
	
	public boolean addAction(ActionInfo actionInfo) throws SQLException {
		if (null == actionInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_ACTION + "` "
					+ "(`name`,`interval`,`notice`,`subject`,`message`,`enabled`) "
					+ "values (?, ?, ?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, actionInfo.name);
			pstmt.setLong(2, actionInfo.interval);
			pstmt.setInt(3, actionInfo.notice);
			pstmt.setString(4, actionInfo.subject);
			pstmt.setString(5, actionInfo.message);
			pstmt.setInt(6, actionInfo.enabled);
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editActionByActionId(ActionInfo actionInfo) throws SQLException {
		if (null == actionInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_ACTION + "` SET "
                    + "`name`=?,`interval`=?,`notice`=?,`subject`=?,`message`=?,`enabled`=? WHERE `action_id`=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, actionInfo.name);
			pstmt.setLong(2, actionInfo.interval);
			pstmt.setInt(3, actionInfo.notice);
			pstmt.setString(4, actionInfo.subject);
			pstmt.setString(5, actionInfo.message);
			pstmt.setInt(6, actionInfo.enabled);
            pstmt.setLong(7, actionInfo.action_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public ActionInfo getActionByActionName(String actionName) throws SQLException {
		if (null == actionName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `action_id`,`name`,`interval`,`notice`,`subject`,`message`,`enabled` FROM `"
					+ TABLE_ACTION + "` WHERE `name`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, actionName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseAction(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public ActionInfo getActionByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `action_id`,`name`,`interval`,`notice`,`subject`,`message`,`enabled` FROM `"
					+ TABLE_ACTION + "` WHERE `action_id`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, action_id);
			ResultSet rs = pstmt.executeQuery();
			
			return parseAction(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean deleteActionByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_ACTION + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	/******************************************* Action Operation ********************************************/
	public List<OperationInfo> listOperation(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "SELECT `operation_id`,`name`,`step`,`action_type`,`msg_format` FROM `" + TABLE_OPERATION + "` ";
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
            
            return parseOperations(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}
	
	public Integer countOperation() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_OPERATION + "'";

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
	
	private List<OperationInfo> parseOperations(ResultSet rs) throws SQLException {
		List<OperationInfo> listOperations = new LinkedList<>();
		while (rs.next()) {
			OperationInfo action = new OperationInfo();
			action.operation_id = rs.getLong("operation_id");
			action.name = rs.getString("name");
			action.step = rs.getInt("step");
			action.action_type = rs.getString("action_type");
			action.msg_format = rs.getString("msg_format");
			
			listOperations.add(action);
		}
		return listOperations;
	}
	
	private OperationInfo parseOperation(ResultSet rs) throws SQLException {
		OperationInfo actionOperation = new OperationInfo();
		if (rs.next()) {
			actionOperation.operation_id = rs.getLong("operation_id");
			actionOperation.name = rs.getString("name");
			actionOperation.step = rs.getInt("step");
			actionOperation.action_type = rs.getString("action_type");
			actionOperation.msg_format = rs.getString("msg_format");
		}
		return actionOperation;
	}

	public boolean addOperation(OperationInfo actionOperationInfo) throws SQLException {
		if (null == actionOperationInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_OPERATION + "` "
					+ "(`name`,`step`,`action_type`,`msg_format`) "
					+ "values (?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, actionOperationInfo.name);
			pstmt.setInt(2, actionOperationInfo.step);
			pstmt.setString(3, actionOperationInfo.action_type);
			pstmt.setString(4, actionOperationInfo.msg_format);
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editOperationByOperationId(OperationInfo actionOperationInfo) 
			throws SQLException {
		if (null == actionOperationInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_OPERATION + "` SET "
                    + "`name`=?,`step`=?,`action_type`=?,`msg_format`=? WHERE `operation_id`=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, actionOperationInfo.name);
			pstmt.setInt(2, actionOperationInfo.step);
			pstmt.setString(3, actionOperationInfo.action_type);
			pstmt.setString(4, actionOperationInfo.msg_format);
            pstmt.setLong(5, actionOperationInfo.operation_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public OperationInfo getOperationByName(String operationName) throws SQLException {
		if (null == operationName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `operation_id`,`name`,`step`,`action_type`,`msg_format` FROM `"
					+ TABLE_OPERATION + "` WHERE `name`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, operationName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseOperation(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public OperationInfo getOperationByOperationId(Long operation_id) throws SQLException {
		if (null == operation_id || operation_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `operation_id`,`action_id`,`name`,`step`,`action_type`,`msg_format` FROM `"
					+ TABLE_OPERATION + "` WHERE `operation_id`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, operation_id);
			ResultSet rs = pstmt.executeQuery();
			
			return parseOperation(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean deleteOperationByOperationId(Long operation_id) throws SQLException {
		if (null == operation_id || operation_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_OPERATION + "` WHERE `actionOperation_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, operation_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public boolean addActionOperations(Long action_id, List<Long> operationIdList) throws SQLException {
		if (null == action_id || action_id.longValue() < 1 || null == operationIdList || operationIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_ACTION_OPERATION + "` (`action_id`, `operation_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long operationId : operationIdList) {
	            String tmp = DBUtil.wrapParams(operationIdList, operationId);
	            list.add(tmp);
	        }
	        String datas = Arrays.toString(list.toArray());
	        datas = datas.substring(1, datas.length() - 1);

	        sql = String.format(sql, datas);

	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.executeUpdate();

	        return true;
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }
	}
	
	public boolean addActionHosts(Long action_id, List<Long> hostIdList) throws SQLException {
		if (null == action_id || action_id.longValue() < 1 || null == hostIdList || hostIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_ACTION_HOST + "` (`action_id`, `host_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long hostId : hostIdList) {
	            String tmp = DBUtil.wrapParams(hostIdList, hostId);
	            list.add(tmp);
	        }
	        String datas = Arrays.toString(list.toArray());
	        datas = datas.substring(1, datas.length() - 1);

	        sql = String.format(sql, datas);

	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.executeUpdate();

	        return true;
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }
	}
	
	public boolean addActionGroups(Long action_id, List<Long> groupIdList) throws SQLException {
		if (null == action_id || action_id.longValue() < 1 || null == groupIdList || groupIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_ACTION_GROUP + "` (`action_id`, `group_id`) VALUES %s;";

	        List<String> list = new ArrayList<String>();
	        for (Long groupId : groupIdList) {
	            String tmp = DBUtil.wrapParams(groupIdList, groupId);
	            list.add(tmp);
	        }
	        String datas = Arrays.toString(list.toArray());
	        datas = datas.substring(1, datas.length() - 1);

	        sql = String.format(sql, datas);

	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.executeUpdate();

	        return true;
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }
	}
	
	public boolean addActionTriggers(Long action_id, List<Long> triggerIdList) throws SQLException {
		if (null == action_id || action_id.longValue() < 1 || null == triggerIdList || triggerIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_ACTION_TRIGGER + "` (`action_id`, `trigger_id`) VALUES %s;";

	        List<String> list = new ArrayList<String>();
	        for (Long triggerId : triggerIdList) {
	            String tmp = DBUtil.wrapParams(triggerIdList, triggerId);
	            list.add(tmp);
	        }
	        String datas = Arrays.toString(list.toArray());
	        datas = datas.substring(1, datas.length() - 1);

	        sql = String.format(sql, datas);

	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.executeUpdate();

	        return true;
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }
	}
	
	public boolean addOperationUsers(Long operation_id, List<Long> userIdList) throws SQLException {
		if (null == operation_id || operation_id.longValue() < 1 || null == userIdList || userIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_OPERATION_USER + "` (`operation_id`, `user_id`) VALUES %s;";

	        List<String> list = new ArrayList<String>();
	        for (Long userId : userIdList) {
	            String tmp = DBUtil.wrapParams(userIdList, userId);
	            list.add(tmp);
	        }
	        String datas = Arrays.toString(list.toArray());
	        datas = datas.substring(1, datas.length() - 1);

	        sql = String.format(sql, datas);

	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.executeUpdate();

	        return true;
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }
	}
	
	public Map<Long, List<Long>> getOperationUsersByOperationId(Long operation_id) throws SQLException {
		if (null == operation_id || operation_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `operation_id`, `uid` FROM `" + TABLE_ACTION_OPERATION + "` WHERE `operation_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, operation_id);
            ResultSet rs = pstmt.executeQuery();

            return parseOperationUserMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getActionOperationsByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `action_id`, `operation_id` FROM `" + TABLE_ACTION_OPERATION + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            ResultSet rs = pstmt.executeQuery();

            return parseAction_OperationOrHostOrGroupOrTriggerMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getActionHostsByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `action_id`, `host_id` FROM `" + TABLE_ACTION_HOST + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            ResultSet rs = pstmt.executeQuery();

            return parseAction_OperationOrHostOrGroupOrTriggerMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getActionGroupsByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `action_id`, `group_id` FROM `" + TABLE_ACTION_GROUP + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            ResultSet rs = pstmt.executeQuery();

            return parseAction_OperationOrHostOrGroupOrTriggerMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getActionTriggersByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `action_id`, `trigger_id` FROM `" + TABLE_ACTION_TRIGGER + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            ResultSet rs = pstmt.executeQuery();

            return parseAction_OperationOrHostOrGroupOrTriggerMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	private Map<Long, List<Long>> parseAction_OperationOrHostOrGroupOrTriggerMap(ResultSet rs) throws SQLException {
        Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();

        while (rs.next()) {
            Long actionId = rs.getLong(1);
            Long id = rs.getLong(2);
            List<Long> list = getHostIdOrGroupIdOrTemplateIdList(actionId, map);

            if (null == list) {
                list = new ArrayList<Long>();
                map.put(actionId, list);
            }
            list.add(id);
        }

        return map;
    }
	
	private List<Long> getHostIdOrGroupIdOrTemplateIdList(Long actionId, Map<Long, List<Long>> map) {
        Set<Long> keys = map.keySet();
        for (Long key : keys) {
            if (actionId.equals(key)) {
                List<Long> list = map.get(key);
                return list;
            }
        }
        return null;
    }
	
	private Map<Long, List<Long>> parseOperationUserMap(ResultSet rs) throws SQLException {
        Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();

        while (rs.next()) {
            Long operationId = rs.getLong(1);
            Long uid = rs.getLong(2);
            List<Long> list = getUserIdList(operationId, map);

            if (null == list) {
                list = new ArrayList<Long>();
                map.put(operationId, list);
            }
            list.add(uid);
        }

        return map;
    }
	
	private List<Long> getUserIdList(Long operationId, Map<Long, List<Long>> map) {
        Set<Long> keys = map.keySet();
        for (Long key : keys) {
            if (operationId.equals(key)) {
                List<Long> list = map.get(key);
                return list;
            }
        }
        return null;
    }
	
	public boolean deleteActionOperationsByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_ACTION_OPERATION + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public boolean deleteActionHostsByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_ACTION_HOST + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public boolean deleteActionGroupsByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_ACTION_GROUP + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public boolean deleteActionTriggersByActionId(Long action_id) throws SQLException {
		if (null == action_id || action_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_ACTION_TRIGGER + "` WHERE `action_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, action_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public boolean deleteOperationUsersByOperationId(Long operation_id) throws SQLException {
		if (null == operation_id || operation_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_OPERATION_USER + "` WHERE `operation_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, operation_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
}
