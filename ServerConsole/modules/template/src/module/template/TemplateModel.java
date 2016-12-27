package module.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.TemplateInfo;
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
	
	public List<TemplateInfo> listTemplate(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "select `group_id`,`name`,`memo`,`ctime`,`mtime` from `" + TABLE_TEMPLATE + "` ";
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
            
            return parseTemplates(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}
	
	public Integer countTemplate() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS from information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_TEMPLATE + "'";

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

	private List<TemplateInfo> parseTemplates(ResultSet rs) throws SQLException {
		List<TemplateInfo> listTemps = new LinkedList<>();
		while (rs.next()) {
			TemplateInfo temp = new TemplateInfo();
			temp.template_id = rs.getLong("template_id");
			temp.name = rs.getString("name");
			temp.ctime = rs.getTimestamp("ctime");
			temp.mtime = rs.getTimestamp("mtime");
			
			listTemps.add(temp);
		}
		
		return listTemps;
	}
	
	@SuppressWarnings("unused")
	private TemplateInfo parseTemplate(ResultSet rs) throws SQLException {
		TemplateInfo temp = new TemplateInfo();
		temp.template_id = rs.getLong("template_id");
		temp.name = rs.getString("name");
		temp.ctime = rs.getTimestamp("ctime");
		temp.mtime = rs.getTimestamp("mtime");
		
		return temp;
	}
}
