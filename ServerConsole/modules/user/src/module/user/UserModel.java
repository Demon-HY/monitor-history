package module.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.alibaba.fastjson.JSONObject;

import module.SDK.info.UserInfo;
import module.SDK.inner.IUserApi;
import monitor.service.db.MySql;
import monitor.utils.Time;

public class UserModel implements IUserApi.IUserModel{

	protected MySql mysql;
	
	private static final String TABLE_USER = "user";
	private static final String TABLE_USER_RECYCLE = "user_recycle";
	
	public UserModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}
	
	private void initTable() throws SQLException {
        Connection conn = this.mysql.getConnection();
        try {
			String sqlUser = "CREATE TABLE IF NOT EXISTS `" + TABLE_USER + "` ("
				+ "`uid` bigint(11) NOT NULL AUTO_INCREMENT,"
				+ "`name` varchar(20) DEFAULT NULL,"
				+ "`phone` int(11) DEFAULT NULL,"
				+ "`email` varchar(64) DEFAULT NULL,"
				+ "`nick` varchar(64) DEFAULT NULL,"
				+ "`password` varchar(255) NOT NULL,"
				+ "`qq` varchar(16) DEFAULT NULL,"
				+ "`type` int(1) NOT NULL DEFAULT 1,"
				+ "`status` int(1) NOT NULL DEFAULT 1,"
				+ "`exattr` varchar(10240) DEFAULT NULL,"
				+ "`ctime` datetime NOT NULL,"
				+ "`mtime` datetime DEFAULT NULL,"
				+ "`load_time` datetime DEFAULT NULL,"
				+ "PRIMARY KEY (`uid`)"
	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sqlUser);
            
            String sqlUserRecycle = "CREATE TABLE IF NOT EXISTS `" + TABLE_USER_RECYCLE + "` ("
				+ "`uid` bigint(11) NOT NULL AUTO_INCREMENT,"
				+ "`name` varchar(20) DEFAULT NULL,"
				+ "`phone` int(11) DEFAULT NULL,"
				+ "`email` varchar(64) DEFAULT NULL,"
				+ "`nick` varchar(64) DEFAULT NULL,"
				+ "`password` varchar(255) NOT NULL,"
				+ "`qq` int(13) DEFAULT NULL,"
				+ "`type` int(1) NOT NULL DEFAULT 1,"
				+ "`status` int(1) NOT NULL DEFAULT 1,"
				+ "`exattr` varchar(10240) DEFAULT NULL,"
				+ "`ctime` datetime NOT NULL,"
				+ "`mtime` datetime DEFAULT NULL,"
				+ "`load_time` datetime DEFAULT NULL,"
				+ "PRIMARY KEY (`uid`)"
	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sqlUserRecycle);
		} catch (SQLException e) {
			throw new SQLException("SQL create failed...");
		} finally {
		    if (conn != null) {
		        conn.close();
		    }
		}
	}

	public boolean createUser(UserInfo user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException();
        }

        Connection conn = this.mysql.getConnection();
        try {
            final String sql = "INSERT INTO `" + TABLE_USER + "` "
            		+ "(`name`,`phone`,`email`,`nick`,`password`, `qq`,`type`,`status`,`exattr`,`ctime`) "
            		+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.name);
            pstmt.setString(2, user.phone);
            pstmt.setString(3, user.email);
            pstmt.setString(4, user.nick);
            pstmt.setString(5, user.password);
            pstmt.setString(6, user.qq);
            pstmt.setInt(7, user.type);
            pstmt.setInt(8, user.status);
            pstmt.setString(9, JSONObject.toJSONString(user.exattr));
            pstmt.setTimestamp(10, Time.getTimestamp());
            
            return pstmt.executeUpdate() == 1 ? true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public UserInfo getUserInfoByUid(Long uid) throws SQLException {
		if (uid <= 0) {
            throw new IllegalArgumentException();
        }
		Connection conn = this.mysql.getConnection();
		try {
			final String sql  = "SELECT `uid`,`name`,`phone`,`email`,`nick`,`password`, `qq`,`type`,`status`,`exattr`,"
            		+ "`ctime`,`mtime`,`load_time` FROM `" + TABLE_USER + "` WHERE `uid` = ?";
            
			PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, uid);
            ResultSet rs = pstmt.executeQuery();
            
            return parseUser(rs);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}

	public boolean setUserAttr(Long uid, String key, Object value) throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = String.format("UPDATE `%s` SET `%s`=?,`mtime`=? WHERE `uid`=?", TABLE_USER, key);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, value);
            pstmt.setTimestamp(2, Time.getTimestamp());
            pstmt.setLong(3, uid);
            return pstmt.executeUpdate() == 1 ? true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	public boolean setUserAttrs(UserInfo userInfo) throws SQLException {
		Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            final String sql = "UPDATE `" + TABLE_USER + "` SET `phone`,`email`,`nick`,`qq`,`exattr`,`mtime` WHERE `uid`=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userInfo.phone);
            pstmt.setString(2, userInfo.email);
            pstmt.setString(3, userInfo.nick);
            pstmt.setString(4, userInfo.qq);
            pstmt.setString(5, JSONObject.toJSONString(userInfo.exattr));
            pstmt.setTimestamp(6, userInfo.mtime);
            pstmt.setLong(7, userInfo.uid);
            
            return pstmt.executeUpdate() == 1 ? true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}

	/**
	 * 通过用户名/邮箱/手机号查找用户
	 * @param name
	 * @param email
	 * @param phone
	 * @return
	 * @throws SQLException 
	 */
	public UserInfo findUser(String name, String email, String phone) throws SQLException {
		if (null == name && email == null && phone == null) {
            throw new IllegalArgumentException();
        }
		Connection conn = this.mysql.getConnection();
		try {
			String factors = "";
            if (null != name) {
                factors += " `name` = '" + StringEscapeUtils.escapeSql(name) + "' ";
            }
            if (null != email) {
                factors = String.format("%s%s `email` = '%s'", factors, (factors.length() > 0 ? " or " : ""), StringEscapeUtils.escapeSql(email));
            }
            if (null != phone) {
                factors = String.format("%s%s `phone` = '%s'", factors, (factors.length() > 0 ? " or " : ""), StringEscapeUtils.escapeSql(phone));
            }
            
            String sql = "SELECT `uid`,`name`,`phone`,`email`,`nick`,`password`,`qq`,`type`,`status`,`exattr`,"
            		+ "`ctime`,`mtime`,`load_time` FROM `" + TABLE_USER + "` ";
            sql = String.format("%s%s%s", sql, (factors.length() > 0 ? "where" : ""), factors);
			PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            return parseUser(rs);
        } finally {
        	if (null != conn) {
                conn.close();
            }
        }
	}
	
	@SuppressWarnings("unchecked")
    private UserInfo parseUser(ResultSet rs) throws SQLException {
        UserInfo user = null;
        if (rs.next()) {
            String attrStr = rs.getString(10);
            Map<String, Object> exattr = null;
            if (null != attrStr) {
                exattr = JSONObject.parseObject(attrStr, Map.class);
            }
            user = new UserInfo(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getInt(8), rs.getInt(9), 
                    exattr, rs.getTimestamp(11), rs.getTimestamp(12), rs.getTimestamp(13));
        }
        return user;
    }
	
//	@SuppressWarnings({ "unchecked", "unused" })
//	public UserInfo queryUserInfoByNamePassword(String name, String password) throws SQLException {
//		Connection conn = this.mysql.getConnection();
//		try {
//			String sql = "SELECT * FROM `" + TABLE_USER + "` WHERE `name` = ? and `password` = ?";
//			PreparedStatement pstmt = conn.prepareStatement(sql);
//			pstmt.setString(1, name);
//			pstmt.setString(2, password);
//			ResultSet rs = pstmt.executeQuery();
//			
//			UserInfo user = null;
//            if (rs.next()) {
//                String attrStr = rs.getString(11);
//                Map<String, Object> exattr = null;
//                if (null != attrStr) {
//                	exattr = JSONObject.parseObject(attrStr, Map.class);
//                }
//                
////                user = new UserInfo(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
////                        rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getLong(8), rs.getInt(9), 
////                        rs.getString(10), exattr, rs.getLong(12), rs.getLong(13));
//            }
//
//            return user;
//		} finally {
//            if (conn != null) {
//                conn.close();
//            }
//        }
//	}
//	/**
//	 * 设置管理员默认密码
//	 */
//	public boolean setAdminDefaultInfo(XProperties properties) throws SQLException {
//		Long uid = checkLoginId("name", "admin");
//		if (uid < 1) {
//			Connection conn = this.mysql.getConnection();
//			try {
//				String sql = "insert into `" + TABLE_USER + "` (`phone`,`nickName`,`email`,`password`,`uuid`,`ctime`) "
//						+ "values (?, ?, ?, ?, ?, ?);";
//				PreparedStatement ps = conn.prepareStatement(sql);
//				ps.setString(1, "admin");
//				ps.setString(2, "管理员");
//				ps.setString(3, "1764496637@qq.com");
//				ps.setString(4, "P@ssw0rd");
//				ps.setLong(5, 0);
//				ps.setLong(6, Time.currentTimeMillis());
//				
//				return ps.executeUpdate() == 1 ? true : false;
//			} finally {
//	            if (conn != null) {
//	                conn.close();
//	            }
//	        }
//		}
//		return false;
//	}
}
