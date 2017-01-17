package module.host;

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

import module.SDK.info.HostInfo;
import monitor.service.db.MySql;
import monitor.utils.DBUtil;
import monitor.utils.Time;

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
					+ "`group_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`host_id`, `group_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST_TEMPLATE + "` (" 
					+ "`host_id` bigint(20) NOT NULL,"
					+ "`template_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`host_id`, `template_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_HOST_HISTORY +  "` (" 
					+ "`host_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(128) NOT NULL,"
					+ "`ip` varchar(16) NOT NULL,"
					+ "`monitored` varchar(32) NOT NULL DEFAULT 'Agent',"
					+ "`status` varchar(32) NOT NULL,"
					+ "`interval` varchar(32) NOT NULL DEFAULT 60,"
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
			String sql = "SELECT `host_id`,`name`,`ip`,`monitored`,`status`,`interval`,`memo`,`ctime`,`mtime` FROM `" + TABLE_HOST + "` ";
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
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_HOST + "'";

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

	public boolean addHost(HostInfo hostInfo) throws SQLException {
		if (null == hostInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_HOST + "` "
					+ "(`name`,`ip`,`monitored`,`status`,`interval`,`memo`,`ctime`,`mtime`) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, hostInfo.name);
			pstmt.setString(2, hostInfo.ip);
			pstmt.setString(3, hostInfo.monitored);
			pstmt.setString(4, hostInfo.status);
			pstmt.setInt(5, hostInfo.interval);
			pstmt.setString(6, hostInfo.memo);
			pstmt.setTimestamp(7, Time.getTimestamp());
			pstmt.setTimestamp(8, Time.getTimestamp());
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editHostByHostId(HostInfo hostInfo) throws SQLException {
		if (null == hostInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_HOST + "` SET "
                    + "`name`=?,`ip`=?,`monitored`=?,`status`=?,`interval`=?,`memo`=?,`mtime`=? WHERE `host_id`=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hostInfo.name);
            pstmt.setString(2, hostInfo.ip);
            pstmt.setString(3, hostInfo.monitored);
            pstmt.setString(4, hostInfo.status);
            pstmt.setInt(5, hostInfo.interval);
            pstmt.setString(6, hostInfo.memo);
            pstmt.setTimestamp(7, Time.getTimestamp());
            pstmt.setLong(8, hostInfo.host_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	/**
	 * 通过 IP 获取主机信息
	 * @param ip
	 * @return
	 * @throws SQLException
	 */
	public HostInfo getHostByIP(String ip) throws SQLException {
		if (null == ip) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `host_id`,`name`,`ip`,`monitored`,`status`,`interval`,`memo`,`ctime`,`mtime` FROM `"
					+ TABLE_HOST + "` WHERE `ip`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, ip);
			ResultSet rs = pstmt.executeQuery();
			
			return parseHost(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public HostInfo getHostByName(String hostName) throws SQLException {
		if (null == hostName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `host_id`,`name`,`ip`,`monitored`,`status`,`interval`,`memo`,`ctime`,`mtime` FROM `"
					+ TABLE_HOST + "` WHERE `name`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, hostName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseHost(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	/**
	 * 通过 主机 Id 获取主机信息
	 * @param host_id
	 * @return
	 * @throws SQLException
	 */
	public HostInfo getHostByHostId(Long host_id) throws SQLException {
		if (null == host_id || host_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT `host_id`,`name`,`ip`,`monitored`,`status`,`interval`,`memo`,`ctime`,`mtime` FROM `"
                    + TABLE_HOST + "` WHERE `host_id`=?;";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, host_id);
            ResultSet rs = pstmt.executeQuery();
            
            return parseHost(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public boolean addHostGroups(Long host_id, List<Long> groupIdList) throws SQLException {
		if (null == host_id || host_id.longValue() < 1 || null == groupIdList || groupIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_HOST_GROUP + "` (`host_id`, `group_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long groupId : groupIdList) {
	            String tmp = DBUtil.wrapParams(host_id, groupId);
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
	
	public boolean deleteHostGroupByHostId(Long host_id) throws SQLException {
		if (null == host_id || host_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_HOST_GROUP + "` WHERE `host_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, host_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public boolean addHostTemplates(Long host_id, List<Long> templateIdList) throws SQLException {
		if (null == host_id || host_id.longValue() < 1 || null == templateIdList || templateIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_HOST_TEMPLATE + "` (`host_id`, `template_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long templateId : templateIdList) {
	            String tmp = DBUtil.wrapParams(host_id, templateId);
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
	
	public boolean deleteHostTemplateByHostId(Long host_id) throws SQLException {
		if (null == host_id || host_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_HOST_TEMPLATE + "` WHERE `host_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, host_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public boolean deleteHostByHostId(Long host_id) throws SQLException {
		if (null == host_id || host_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_HOST + "` WHERE `host_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, host_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getHostGroupsByHostId(Long host_id) throws SQLException {
		if (null == host_id || host_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `host_id`, `group_id` FROM `" + TABLE_HOST_GROUP + "` WHERE `host_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, host_id);
            ResultSet rs = pstmt.executeQuery();

            return parseHost_GroupOrTemplateMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getHostTemplatesByHostId(Long host_id) throws SQLException {
		if (null == host_id || host_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `host_id`, `template_id` FROM `" + TABLE_HOST_TEMPLATE+ "` WHERE `host_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, host_id);
            ResultSet rs = pstmt.executeQuery();

            return parseHost_GroupOrTemplateMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	private Map<Long, List<Long>> parseHost_GroupOrTemplateMap(ResultSet rs) throws SQLException {
        Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();

        while (rs.next()) {
            Long hostId = rs.getLong(1);
            Long id = rs.getLong(2);
            List<Long> list = getGroupIdOrTemplateIdList(hostId, map);

            if (null == list) {
                list = new ArrayList<Long>();
                map.put(hostId, list);
            }
            list.add(id);
        }

        return map;
    }
	
	private List<Long> getGroupIdOrTemplateIdList(Long groupIdOrTemplateId, Map<Long, List<Long>> map) {
        Set<Long> keys = map.keySet();
        for (Long key : keys) {
            if (groupIdOrTemplateId.equals(key)) {
                List<Long> list = map.get(key);
                return list;
            }
        }
        return null;
    }
}
