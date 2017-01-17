package module.service;

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

import module.SDK.info.IndexInfo;
import module.SDK.info.ServiceInfo;
import monitor.service.db.MySql;
import monitor.utils.DBUtil;

public class ServiceModel {

	public static final String TABLE_SERVICE = "service";
	public static final String TABLE_INDEX = "index";
	public static final String TABLE_SERVICE_INDEX = "service_index";
	private MySql mysql;

	public ServiceModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}

	public void initTable() throws SQLException {
		Connection conn = this.mysql.getConnection();
		try {
			String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_SERVICE + "` (" 
					+ "`service_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`interval` int(10) NOT NULL DEFAULT 5,"
					+ "`plugin_name` varchar(255) NOT NULL DEFAULT 'n/a',"
					+ "`has_sub_service` tinyint(1) NOT NULL,"
					+ "`memo` varchar(1024) DEFAULT NULL,"
					+ "PRIMARY KEY (`service_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_INDEX + "` (" 
					+ "`index_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(32) NOT NULL,"
					+ "`key` varchar(32) NOT NULL,"
					+ "`type` varchar(32) NOT NULL DEFAULT 'int'," // 指标数据类型,默认 int
					+ "`memo` varchar(1024) DEFAULT NULL,"
                    + "PRIMARY KEY (`index_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_SERVICE_INDEX + "` (" 
					+ "`service_id` bigint(20) NOT NULL,"
					+ "`index_id` bigint(20) NOT NULL,"
					+ "PRIMARY KEY (`service_id`, `index_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}

	/******************************************* Service ********************************************/
    public List<ServiceInfo> listService(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `service_id`,`name`,`interval`,`plugin_name`,`has_sub_service`,`memo` FROM `" + TABLE_SERVICE + "` ";
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
            
            return parseServices(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

	public Integer countService() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT TABLE_ROWS FROM information_schema.`TABLES` WHERE TABLE_NAME = '" + TABLE_SERVICE + "'";
            
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
	
	private List<ServiceInfo> parseServices(ResultSet rs) throws SQLException {
        List<ServiceInfo> listServices = new LinkedList<>();
        while (rs.next()) {
            ServiceInfo service = new ServiceInfo();
            service.service_id = rs.getLong("service_id");
            service.name = rs.getString("name");
            service.interval = rs.getInt("interval");
            service.plugin_name = rs.getString("plugin_name");
            service.has_sub_service = rs.getInt("has_sub_service");
            service.memo = rs.getString("memo");

            listServices.add(service);
        }
        return listServices;
    }
    
	private ServiceInfo parseService(ResultSet rs) throws SQLException {
    	ServiceInfo service = new ServiceInfo();
    	if (rs.next()) {
    		service.service_id = rs.getLong("service_id");
            service.name = rs.getString("name");
            service.interval = rs.getInt("interval");
            service.plugin_name = rs.getString("plugin_name");
            service.has_sub_service = rs.getInt("has_sub_service");
            service.memo = rs.getString("memo");
    	}
        return service;
    }
    
	public boolean addService(ServiceInfo serviceInfo) throws SQLException {
		if (null == serviceInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_SERVICE + "` "
					+ "(`name`,`interval`,`plugin_name`,`has_sub_service`,`memo`) "
					+ "values (?, ?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, serviceInfo.name);
			pstmt.setLong(2, serviceInfo.interval);
			pstmt.setString(3, serviceInfo.plugin_name);
			pstmt.setInt(4, serviceInfo.has_sub_service);
			pstmt.setString(5, serviceInfo.memo);
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editServiceByServiceId(ServiceInfo serviceInfo) throws SQLException {
		if (null == serviceInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_SERVICE + "` SET "
                    + "`name`=?,`interval`=?,`plugin_name`=?,`has_sub_service`=?,`memo`=? WHERE `service_id`=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, serviceInfo.name);
			pstmt.setLong(2, serviceInfo.interval);
			pstmt.setString(3, serviceInfo.plugin_name);
			pstmt.setInt(4, serviceInfo.has_sub_service);
			pstmt.setString(5, serviceInfo.memo);
            pstmt.setLong(6, serviceInfo.service_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public ServiceInfo getServiceByServiceName(String serviceName) throws SQLException {
		if (null == serviceName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `service_id`,`name`,`interval`,`plugin_name`,`has_sub_service`,`memo` FROM `"
					+ TABLE_SERVICE + "` WHERE `name`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, serviceName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseService(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public ServiceInfo getServiceByServiceId(Long service_id) throws SQLException {
		if (null == service_id || service_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `service_id`,`name`,`interval`,`plugin_name`,`has_sub_service`,`memo` FROM `"
					+ TABLE_SERVICE + "` WHERE `service_id`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, service_id);
			ResultSet rs = pstmt.executeQuery();
			
			return parseService(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean deleteServiceByServiceId(Long service_id) throws SQLException {
		if (null == service_id || service_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_SERVICE + "` WHERE `service_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, service_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
    /******************************************* Index ********************************************/
    public List<IndexInfo> listIndex(Integer pageIndex, Integer pageSize, String order, String sort) 
    		throws SQLException {
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `index_id`,`service_id`,`name`,`key`,`type`,`memo` FROM `" + TABLE_SERVICE_INDEX + "` ";
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
            
            return parseIndexs(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
    
	public Integer countIndex() throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "SELECT count(*) FROM `" + TABLE_SERVICE_INDEX + "` ";

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
	
	private List<IndexInfo> parseIndexs(ResultSet rs) throws SQLException {
    	List<IndexInfo> listIndexs = new LinkedList<>();
        while (rs.next()) {
        	IndexInfo index = new IndexInfo();
        	index.index_id = rs.getLong("index_id");
        	index.name = rs.getString("name");
        	index.key = rs.getString("key");
        	index.type = rs.getString("type");
        	index.memo = rs.getString("memo");

        	listIndexs.add(index);
        }
        return listIndexs;
	}
	
	private IndexInfo parseIndex(ResultSet rs) throws SQLException {
		IndexInfo index = new IndexInfo();
		if (rs.next()) {
			index.index_id = rs.getLong("index_id");
        	index.name = rs.getString("name");
        	index.key = rs.getString("key");
        	index.type = rs.getString("type");
        	index.memo = rs.getString("memo");
		}
		return index;
	}

	public boolean addIndex(IndexInfo indexInfo) throws SQLException {
		if (null == indexInfo) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "INSERT INTO `" + TABLE_INDEX + "` "
					+ "(`name`,`key`,`type`,`memo`) "
					+ "values (?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, indexInfo.name);
			pstmt.setString(2, indexInfo.key);
			pstmt.setString(3, indexInfo.type);
			pstmt.setString(4, indexInfo.memo);
			
			return pstmt.executeUpdate() == 1 ?  true : false;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean editIndexByIndexId(IndexInfo indexInfo) throws SQLException {
		if (null == indexInfo) {
			throw new IllegalArgumentException();
		}
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_INDEX + "` SET "
                    + "`name`=?,`key`=?,`type`=?,`memo`=? WHERE `index_id`=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, indexInfo.name);
			pstmt.setString(2, indexInfo.key);
			pstmt.setString(3, indexInfo.type);
			pstmt.setString(4, indexInfo.memo);
            pstmt.setLong(5, indexInfo.index_id);
            
            return pstmt.executeUpdate() == 1 ?  true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public IndexInfo getIndexByIndexName(String indexName) throws SQLException {
		if (null == indexName) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `index_id`,`name`,`key`,`type`,`memo` FROM `"
					+ TABLE_INDEX + "` WHERE `name`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, indexName);
			ResultSet rs = pstmt.executeQuery();
			
			return parseIndex(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public IndexInfo getIndexByIndexId(Long index_id) throws SQLException {
		if (null == index_id || index_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			final String sql = "SELECT `index_id`,`name`,`key`,`type`,`memo` FROM `"
					+ TABLE_INDEX + "` WHERE `index_id`=?;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, index_id);
			ResultSet rs = pstmt.executeQuery();
			
			return parseIndex(rs);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public boolean deleteIndexByIndexId(Long index_id) throws SQLException {
		if (null == index_id || index_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_INDEX + "` WHERE `index_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, index_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public boolean addServiceIndexs(Long service_id, List<Long> indexIdList) throws SQLException {
		if (null == service_id || service_id.longValue() < 1 || null == indexIdList || indexIdList.size() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
	    try {
	        conn = this.mysql.getConnection();
	        String sql = "INSERT INTO `" + TABLE_SERVICE_INDEX + "` (`service_id`, `index_id`) VALUES %s;";

	        List<String> list = new ArrayList<String>();
	        for (Long indexId : indexIdList) {
	            String tmp = DBUtil.wrapParams(indexIdList, indexId);
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
	
	public Map<Long, List<Long>> getServiceIndexsByServiceId(Long service_id) throws SQLException {
		if (null == service_id || service_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "SELECT `service_id`, `index_id` FROM `" + TABLE_SERVICE_INDEX + "` WHERE `service_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, service_id);
            ResultSet rs = pstmt.executeQuery();

            return parseServiceIndexMap(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}

	private Map<Long, List<Long>> parseServiceIndexMap(ResultSet rs) throws SQLException {
		Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();

        while (rs.next()) {
            Long serviceId = rs.getLong(1);
            Long indexId = rs.getLong(2);
            List<Long> list = getIndexIdList(serviceId, map);

            if (null == list) {
                list = new ArrayList<Long>();
                map.put(serviceId, list);
            }
            list.add(indexId);
        }

        return map;
	}

	private List<Long> getIndexIdList(Long serviceId, Map<Long, List<Long>> map) {
		Set<Long> keys = map.keySet();
        for (Long key : keys) {
            if (serviceId.equals(key)) {
                List<Long> list = map.get(key);
                return list;
            }
        }
        return null;
	}
	
	public boolean deleteServiceIndexsByServiceId(Long service_id) throws SQLException {
		if (null == service_id || service_id.longValue() < 1) {
			throw new IllegalArgumentException();
		}
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "DELETE FROM `" + TABLE_SERVICE_INDEX + "` WHERE `service_id`=?;";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, service_id);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
