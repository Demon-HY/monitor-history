package module.maintain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.MaintainInfo;
import monitor.service.db.MySql;

public class MaintainModel {

	public static final String TABLE_MAINTAIN = "maintain";
	public static final String TABLE_MAINTAIN_HOST = "maintain_host";
	public static final String TABLE_MAINTAIN_GROUP = "maintain_group";
	private MySql mysql;

	public MaintainModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_MAINTAIN + "` ("
					+ "`maintain_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`content` varchar(4096) NOT NULL,"
					+ "`start_time` datetime DEFAULT NULL,"
                    + "`end_time` datetime DEFAULT NULL,"
					+ "PRIMARY KEY (`maintain_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_MAINTAIN_HOST + "` (" 
					+ "`maintain_id` bigint(20) NOT NULL,"
					+ "`host_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_MAINTAIN_GROUP + "` (" 
					+ "`maintain_id` bigint(20) NOT NULL,"
					+ "`group_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}
	
	public List<MaintainInfo> listMaintain(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "select `maintain_id`,`name`,`content`,`start_time`,`end_time` from `" + TABLE_MAINTAIN + "` ";
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
            
            return parseMaintains(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}
	
	public Integer countMaintain() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS from information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_MAINTAIN + "'";

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

	private List<MaintainInfo> parseMaintains(ResultSet rs) throws SQLException {
		List<MaintainInfo> listMaintains = new LinkedList<>();
		while (rs.next()) {
			MaintainInfo main = new MaintainInfo();
			main.maintain_id = rs.getLong("maintain_id");
			main.name = rs.getString("name");
			main.content = rs.getString("content");
			main.start_time = rs.getTimestamp("start_time");
			main.end_time = rs.getTimestamp("end_time");
			
			listMaintains.add(main);
		}
		
		return listMaintains;
	}
	
	@SuppressWarnings("unused")
	private MaintainInfo parseMaintain(ResultSet rs) throws SQLException {
		MaintainInfo main = new MaintainInfo();
		if (rs.next()) {
			main.maintain_id = rs.getLong("maintain_id");
			main.name = rs.getString("name");
			main.content = rs.getString("content");
			main.start_time = rs.getTimestamp("start_time");
			main.end_time = rs.getTimestamp("end_time");
		}
		
		return main;
	}
}
