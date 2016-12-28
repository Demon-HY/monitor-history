package module.host;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.HostInfo;
import monitor.service.db.MySql;

public class HostModel {

	public static final String TABLE_HOST = "host";
	public static final String TABLE_HOST_GROUP = "host_group";
	public static final String TABLE_HOST_TEMPLATE = "host_template";
	public static final String TABLE_HOST_HISTORY = "host_history";
	private MySql mysql;

	public HostModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST +  "` (" 
					+ "`host_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(128) NOT NULL,"
					+ "`ip` varchar(16) NOT NULL,"
					+ "`monitored` varchar(32) NOT NULL DEFAULT 'Agent',"
					+ "`status` varchar(32) NOT NULL,"
					+ "`interval` int(11) NOT NULL DEFAULT 60,"	// 主机存活状态监测间隔
					+ "`memo` varchar(1024) DEFAULT NULL,"
					+ "`ctime` datetime DEFAULT NULL,"
                    + "`mtime` datetime DEFAULT NULL,"
					+ "PRIMARY KEY (`host_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST_GROUP + "` (" 
					+ "`host_id` bigint(20) NOT NULL,"
					+ "`group_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST_TEMPLATE + "` (" 
					+ "`host_id` bigint(20) NOT NULL,"
					+ "`template_id` bigint(20) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST_HISTORY +  "` (" 
					+ "`host_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(128) NOT NULL,"
					+ "`ip` varchar(16) NOT NULL,"
					+ "`monitored` varchar(32) NOT NULL DEFAULT 'Agent',"
					+ "`status` varchar(32) NOT NULL,"
					+ "`interval` varchar(32) NOT NULL DEFAULT 60,"	// 主机存活状态监测间隔
					+ "`memo` varchar(1024) DEFAULT NULL,"
					+ "`ctime` datetime DEFAULT NULL,"
                    + "`mtime` datetime DEFAULT NULL,"
					+ "PRIMARY KEY (`host_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}

	public List<HostInfo> listHost(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			String sql = "select `host_id`,`name`,`ip`,`monitored`,`status`,`interval`,`memo`,`ctime`,`mtime` from `" + TABLE_HOST + "` ";
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
            
            return parseHosts(rs);
		} finally {
			if (conn != null) {
                conn.close();
            }
		}
	}

	private List<HostInfo> parseHosts(ResultSet rs) throws SQLException {
		List<HostInfo> listHosts = new LinkedList<HostInfo>();
		while (rs.next()) {
//			HostInfo host = new HostInfo(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4), rs.getInt(5),
//					rs.getString(6),rs.getTimestamp(7),rs.getTimestamp(8));
			HostInfo host = new HostInfo();
			host.host_id = rs.getLong("host_id");
			host.name = rs.getString("name");
			host.ip = rs.getString("ip");
			host.monitored = rs.getString("monitored");
			host.status = rs.getString("status");
			host.interval = rs.getInt("interval");
			host.memo = rs.getString("memo");
			host.ctime = rs.getTimestamp("ctime");
			host.mtime = rs.getTimestamp("mtime");
			
			listHosts.add(host);
		}
		return listHosts;
	}
	
	@SuppressWarnings("unused")
	private HostInfo parseHost(ResultSet rs) throws SQLException {
//			HostInfo host = new HostInfo(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4), rs.getInt(5),
//					rs.getString(6),rs.getTimestamp(7),rs.getTimestamp(8));
			HostInfo host = null;
			if (rs.next()) {
				host = new HostInfo();
				host.host_id = rs.getLong("host_id");
				host.name = rs.getString("name");
				host.ip = rs.getString("ip");
				host.monitored = rs.getString("monitored");
				host.status = rs.getString("status");
				host.interval = rs.getInt("interval");
				host.memo = rs.getString("memo");
				host.ctime = rs.getTimestamp("ctime");
				host.mtime = rs.getTimestamp("mtime");
			}
		return host;
	}

	public Integer countHost() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS from information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_HOST + "'";

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
}
