package module.group;

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

import module.SDK.info.GroupInfo;
import monitor.service.db.MySql;
import monitor.utils.DBUtil;
import monitor.utils.Time;

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
					+ "`group_id` bigint(20) NOT NULL,"
					+ "`template_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`group_id`, `template_id`)"
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
			String sql = "SELECT `group_id`,`name`,`memo`,`ctime`,`mtime` FROM `" + TABLE_GROUP + "` ";
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
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_GROUP + "'";

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

	public boolean addGroup(GroupInfo groupInfo) throws SQLException {
		if (null == groupInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_GROUP + "` "
					+ "(`name`,`memo`,`ctime`,`mtime`) "
					+ "values (?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, groupInfo.name);
			pstmt.setString(2, groupInfo.memo);
			pstmt.setTimestamp(3, Time.getTimestamp());
			pstmt.setTimestamp(4, Time.getTimestamp());
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editGroupByGroupId(GroupInfo groupInfo) throws SQLException {
		if (null == groupInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_GROUP + "` SET "
                    + "`name`=?,`memo`=?,`mtime`=? WHERE `group_id`=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupInfo.name);
            pstmt.setString(2, groupInfo.memo);
            pstmt.setTimestamp(3, Time.getTimestamp());
            pstmt.setLong(4, groupInfo.group_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public GroupInfo getGroupByGroupName(String groupName) throws SQLException {
		if (null == groupName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `group_id`,`name`,`memo`,`ctime`,`mtime` FROM `"
					+ TABLE_GROUP + "` WHERE `name`=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, groupName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseGroup(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public GroupInfo getGroupByGroupId(Long group_id) throws SQLException {
		if (null == group_id || group_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT `group_id`,`name`,`memo`,`ctime`,`mtime` FROM `"
                    + TABLE_GROUP + "` WHERE `group_id`=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, group_id);
            ResultSet rs = pstmt.executeQuery();
            
            return parseGroup(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public boolean deleteGroupByGroupId(Long group_id) throws SQLException {
		if (null == group_id || group_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_GROUP + "` WHERE `group_id`=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, group_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public boolean addGroupTemplate(Long group_id, List<Long> templateIdList) throws SQLException {
		if (null == group_id || group_id.longValue() < 1 || null == templateIdList || templateIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_GROUP_TEMPLATE + "` (`group_id`, `template_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long templateId : templateIdList) {
	            String tmp = DBUtil.wrapParams(group_id, templateId);
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
	
	public boolean deleteGroupTemplateByGroupId(Long group_id) throws SQLException {
		if (null == group_id || group_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_GROUP_TEMPLATE + "` WHERE `group_id`=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, group_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getGroupTemplateByGroupId(Long group_id) throws SQLException {
		if (null == group_id || group_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `group_id`, `template_id` FROM `" + TABLE_GROUP_TEMPLATE+ "` WHERE `group_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, group_id);
            ResultSet rs = pstmt.executeQuery();

            return parseGroupTemplateMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	private Map<Long, List<Long>> parseGroupTemplateMap(ResultSet rs) throws SQLException {
        Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();

        while (rs.next()) {
            Long groupId = rs.getLong(1);
            Long templateId = rs.getLong(2);
            List<Long> list = getTemplateIdList(groupId, map);

            if (null == list) {
                list = new ArrayList<Long>();
                map.put(groupId, list);
            }
            list.add(templateId);
        }

        return map;
    }
	
	private List<Long> getTemplateIdList(Long groupId, Map<Long, List<Long>> map) {
        Set<Long> keys = map.keySet();
        for (Long key : keys) {
            if (groupId.equals(key)) {
                List<Long> list = map.get(key);
                return list;
            }
        }
        return null;
    }
}
