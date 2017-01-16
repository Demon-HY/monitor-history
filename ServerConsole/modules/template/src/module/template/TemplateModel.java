package module.template;

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

import module.SDK.info.TemplateInfo;
import monitor.service.db.MySql;
import monitor.utils.DBUtil;
import monitor.utils.Time;

public class TemplateModel {

	public static final String TABLE_TEMPLATE = "template";
	public static final String TABLE_TEMPLATE_SERVICE = "template_service";
	public static final String TABLE_TEMPLATE_TRIGGER = "template_trigger";
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
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_TEMPLATE_SERVICE + "` (" 
					+ "`template_id` bigint(20) NOT NULL,"
					+ "`service_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`template_id`, `service_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_TEMPLATE_TRIGGER + "` (" 
					+ "`template_id` bigint(20) NOT NULL,"
					+ "`trigger_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`template_id`, `trigger_id`)"
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
			String sql = "SELECT `template_id`,`name`,`ctime`,`mtime` FROM `" + TABLE_TEMPLATE + "` ";
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
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_TEMPLATE + "'";

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
	
	private TemplateInfo parseTemplate(ResultSet rs) throws SQLException {
		TemplateInfo temp = new TemplateInfo();
		temp.template_id = rs.getLong("template_id");
		temp.name = rs.getString("name");
		temp.ctime = rs.getTimestamp("ctime");
		temp.mtime = rs.getTimestamp("mtime");
		
		return temp;
	}

	public boolean addTemplate(TemplateInfo templateInfo) throws SQLException {
		if (null == templateInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_TEMPLATE + "` "
					+ "(`name`,`ctime`,`mtime`) "
					+ "values (?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateInfo.name);
			pstmt.setTimestamp(2, Time.getTimestamp());
			pstmt.setTimestamp(3, Time.getTimestamp());
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editTemplateByTemplateId(TemplateInfo templateInfo) throws SQLException {
		if (null == templateInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_TEMPLATE + "` SET "
                    + "`name`=?,`mtime`=? WHERE `template_id`=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, templateInfo.name);
			pstmt.setTimestamp(2, Time.getTimestamp());
            pstmt.setLong(3, templateInfo.template_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public TemplateInfo getTemplateByName(String templateName) throws SQLException {
		if (null == templateName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `template_id`,`name`,`ctime`,`mtime` FROM `"
					+ TABLE_TEMPLATE + "` WHERE `name`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseTemplate(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public TemplateInfo getTemplateByTemplateId(Long template_id) throws SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT `template_id`,`name`,`ctime`,`mtime` FROM `"
                    + TABLE_TEMPLATE + "` WHERE `template_id`=?;";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, template_id);
            ResultSet rs = pstmt.executeQuery();
            
            return parseTemplate(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public boolean addTemplateService(Long template_id, List<Long> serviceIdList) throws SQLException {
		if (null == template_id || template_id.longValue() < 1 || null == serviceIdList || serviceIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_TEMPLATE_SERVICE + "` (`template_id`, `service_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long serviceId : serviceIdList) {
	            String tmp = DBUtil.wrapParams(template_id, serviceId);
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
	
	public boolean addTemplateTrigger(Long template_id, List<Long> triggerIdList) throws SQLException {
		if (null == template_id || template_id.longValue() < 1 || null == triggerIdList || triggerIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_TEMPLATE_TRIGGER + "` (`template_id`, `trigger_id`) VALUES %s";

	        List<String> list = new ArrayList<String>();
	        for (Long triggerId : triggerIdList) {
	            String tmp = DBUtil.wrapParams(template_id, triggerId);
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
	
	public boolean deleteTemplateServiceByTemplateId(Long template_id) throws SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_TEMPLATE_SERVICE + "` WHERE `template_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, template_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public boolean deleteTemplateTriggerByTemplateId(Long template_id) throws SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_TEMPLATE_TRIGGER + "` WHERE `template_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, template_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public boolean deleteTemplateByTemplateId(Long template_id) throws SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_TEMPLATE + "` WHERE `template_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, template_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getTemplateServiceByTemplateId(Long template_id) throws SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `template_id`, `service_id` FROM `" + TABLE_TEMPLATE_SERVICE + "` WHERE `template_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, template_id);
            ResultSet rs = pstmt.executeQuery();

            return parseTemplate_ServiceOrTriggerMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public Map<Long, List<Long>> getTemplateTriggerByTemplateId(Long template_id) throws SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `template_id`, `trigger_id` FROM `" + TABLE_TEMPLATE_TRIGGER + "` WHERE `template_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, template_id);
            ResultSet rs = pstmt.executeQuery();

            return parseTemplate_ServiceOrTriggerMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	private Map<Long, List<Long>> parseTemplate_ServiceOrTriggerMap(ResultSet rs) throws SQLException {
        Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();

        while (rs.next()) {
            Long templateId = rs.getLong(1);
            Long id = rs.getLong(2);
            List<Long> list = getServiceIdOrTriggerIdList(templateId, map);

            if (null == list) {
                list = new ArrayList<Long>();
                map.put(templateId, list);
            }
            list.add(id);
        }

        return map;
    }
	
	private List<Long> getServiceIdOrTriggerIdList(Long serviceIdOrTemplateId, Map<Long, List<Long>> map) {
        Set<Long> keys = map.keySet();
        for (Long key : keys) {
            if (serviceIdOrTemplateId.equals(key)) {
                List<Long> list = map.get(key);
                return list;
            }
        }
        return null;
    }

}
