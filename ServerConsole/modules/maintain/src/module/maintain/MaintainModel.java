package module.maintain;

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

import module.SDK.info.MaintainInfo;
import monitor.service.db.MySql;
import monitor.utils.DBUtil;

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
					+ "`host_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`maintain_id`, `host_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_MAINTAIN_GROUP + "` (" 
					+ "`maintain_id` bigint(20) NOT NULL,"
					+ "`group_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`maintain_id`, `group_id`)"
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
			String sql = "SELECT `maintain_id`,`name`,`content`,`start_time`,`end_time` FROM `" + TABLE_MAINTAIN + "` ";
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
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_MAINTAIN + "'";

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

	public boolean addMaintain(MaintainInfo maintainInfo) throws SQLException {
		if (null == maintainInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_MAINTAIN + "` "
					+ "(`name`,`content`,`start_time`,`end_time`) "
					+ "values (?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, maintainInfo.name);
			pstmt.setString(2, maintainInfo.content);
			pstmt.setTimestamp(3, maintainInfo.start_time);
			pstmt.setTimestamp(4, maintainInfo.end_time);
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editMaintainByMaintainId(MaintainInfo maintainInfo) throws SQLException {
		if (null == maintainInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_MAINTAIN + "` SET "
                    + "`name`=?,`content`=?,`start_time`=?,`end_time`=? WHERE `maintain_id`=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maintainInfo.name);
			pstmt.setString(2, maintainInfo.content);
			pstmt.setTimestamp(3, maintainInfo.start_time);
			pstmt.setTimestamp(4, maintainInfo.end_time);
			pstmt.setLong(5, maintainInfo.maintain_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public MaintainInfo getMaintainByMaintainName(String maintainName) throws SQLException {
		if (null == maintainName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `maintain_id`,`name`,`content`,`start_time`,`end_time` FROM `"
					+ TABLE_MAINTAIN + "` WHERE `name`=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, maintainName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseMaintain(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public MaintainInfo getMaintainByMaintainId(Long maintain_id) throws SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `maintain_id`,`name`,`content`,`start_time`,`end_time` FROM `"
					+ TABLE_MAINTAIN + "` WHERE `maintain_id`=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, maintain_id);
			ResultSet rs = pstmt.executeQuery();
			
			return parseMaintain(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean deleteMaintainByMaintainId(Long maintain_id) throws SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_MAINTAIN + "` WHERE `maintain_id`=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, maintain_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public boolean addMaintainHost(Long maintain_id, List<Long> hostIdList) throws SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1 || null == hostIdList || hostIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_MAINTAIN_HOST + "` (`maintain_id`, `host_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long templateId : hostIdList) {
	            String tmp = DBUtil.wrapParams(maintain_id, templateId);
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
	
	public boolean addMaintainGroup(Long maintain_id, List<Long> groupIdList) throws SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1 || null == groupIdList || groupIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_MAINTAIN_GROUP + "` (`maintain_id`, `group_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long templateId : groupIdList) {
	            String tmp = DBUtil.wrapParams(maintain_id, templateId);
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
	
	public boolean deleteMaintainHostByMaintainId(Long maintain_id) throws SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_MAINTAIN_HOST + "` WHERE `maintain_id`=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, maintain_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public boolean deleteMaintainGroupByMaintainId(Long maintain_id) throws SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_MAINTAIN_GROUP + "` WHERE `maintain_id`=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, maintain_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getMaintainHostByMaintainID(Long maintain_id) throws SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `maintain_id`, `host_id` FROM `" + TABLE_MAINTAIN_HOST + "`;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            return parseMaintain_HostOrGroupMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getMaintainGroupByMaintainID(Long maintain_id) throws SQLException {
		if (null == maintain_id || maintain_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `maintain_id`, `group_id` FROM `" + TABLE_MAINTAIN_GROUP + "`;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            return parseMaintain_HostOrGroupMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}

	private Map<Long, List<Long>> parseMaintain_HostOrGroupMap(ResultSet rs) throws SQLException {
		Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();

        while (rs.next()) {
            Long maintainId = rs.getLong(1);
            Long id = rs.getLong(2);
            List<Long> list = getHostIdOrGroupIdList(maintainId, map);

            if (null == list) {
                list = new ArrayList<Long>();
                map.put(maintainId, list);
            }
            list.add(id);
        }

        return map;
	}

	private List<Long> getHostIdOrGroupIdList(Long hostIdOrGroupId, Map<Long, List<Long>> map) {
		Set<Long> keys = map.keySet();
        for (Long key : keys) {
            if (hostIdOrGroupId.equals(key)) {
                List<Long> list = map.get(key);
                return list;
            }
        }
        return null;
	}
}
