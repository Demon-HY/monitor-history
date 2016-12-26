package module.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import module.SDK.info.ServiceInfo;
import monitor.service.db.MySql;

public class ServiceModel {

	public static final String TABLE_SERVICE = "service";
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
			
			sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_SERVICE_INDEX + "` (" 
					+ "`service_index_id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`service_id` bigint(20) NOT NULL,"
					+ "`name` varchar(32) NOT NULL,"
					+ "`key` varchar(32) NOT NULL,"
					+ "`type` varchar(32) NOT NULL DEFAULT 'int'," // 指标数据类型,默认 int
					+ "`memo` varchar(1024) DEFAULT NULL,"
                    + "PRIMARY KEY (`service_index_id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sql);
		} finally {
			conn.close();
		}
	}

    public List<ServiceInfo> listService(Integer pageIndex, Integer pageSize, String order, String sort) throws SQLException {
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "select `service_id`,`name`,`interval`,`plugin_name`,`has_sub_service`,`memo` from `" + TABLE_SERVICE + "` ";
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
            
            return parseServices(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private List<ServiceInfo> parseServices(ResultSet rs) throws SQLException {
        List<ServiceInfo> listServices = new LinkedList<ServiceInfo>();
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
}
