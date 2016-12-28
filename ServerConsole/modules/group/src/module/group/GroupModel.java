package module.group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.GroupInfo;
import monitor.service.db.MySql;

public class GroupModel {

	public static final String TABLE_GROUP = "group";
	public static final String TABLE_GROUP_TEMPLATE = "group_template";
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
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_GROUP_TEMPLATE + "` (" 
					+ "`host_id` bigint(20) NOT NULL,"
					+ "`template_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}

	public List<GroupInfo> listGroup(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "select `group_id`,`name`,`memo`,`ctime`,`mtime` from `" + TABLE_GROUP + "` ";
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
            
            return parseGroups(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}
	
	public Integer countGroup() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS from information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_GROUP + "'";

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
	
	private List<GroupInfo> parseGroups(ResultSet rs) throws SQLException {
		List<GroupInfo> listGroups = new LinkedList<>();
		while (rs.next()) {
			GroupInfo group = new GroupInfo();
			group.group_id = rs.getLong("group_id");
			group.name = rs.getString("name");
			group.memo = rs.getString("memo");
			group.ctime = rs.getTimestamp("ctime");
			group.mtime = rs.getTimestamp("mtime");
			
			listGroups.add(group);
		}
		return listGroups;
	}
	
	@SuppressWarnings("unused")
	private GroupInfo parseGroup(ResultSet rs) throws SQLException {
		GroupInfo group = null;
		if (rs.next()) {
			group = new GroupInfo();
			group.group_id = rs.getLong("group_id");
			group.name = rs.getString("name");
			group.memo = rs.getString("memo");
			group.ctime = rs.getTimestamp("ctime");
			group.mtime = rs.getTimestamp("mtime");
		}
		return group;
	}
}
