package module.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import module.SDK.info.TokenInfo;
import module.SDK.inner.IAuthApi;
import monitor.service.db.MySql;

public class AuthModel implements IAuthApi.IAuthModel {
	
	private MySql mysql;
	private static final String TABLE_LOGIN_ID = "login_id";
	private static final String TABLE_TOKEN = "token";
	
	public AuthModel(MySql mysql) throws SQLException {
		this.mysql = mysql;
		initTable();
	}
	
	private void initTable() throws SQLException {
        Connection conn = this.mysql.getConnection();
        try {
			String sqlLoginId = "CREATE TABLE IF NOT EXISTS `" + TABLE_LOGIN_ID + "` ("
				+ "`uid` bigint(20) NOT NULL,"
				+ "`type` varchar(16) NOT NULL,"
				+ "`value` varchar(16) NOT NULL,"
				+ "UNIQUE KEY (`type`, `value`)\n"
	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			conn.createStatement().executeUpdate(sqlLoginId);

			String sqlToken = "CREATE TABLE IF NOT EXISTS `" + TABLE_TOKEN + "` ("
                + "`token` varchar(128) PRIMARY KEY,"
                + "`uid` bigint(20) UNSIGNED NOT NULL,"
                + "`expires` datetime NOT NULL,"
                + "`ctime` datetime NOT NULL,"
                + "`ip` varchar(32) DEFAULT NULL,"
                + "`device` varchar(8) DEFAULT NULL"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            conn.createStatement().executeUpdate(sqlToken);
		} catch (SQLException e) {
			throw new SQLException("SQL create failed...");
		} finally {
		    if (conn != null) {
		        conn.close();
		    }
		}
	}
	
	/**********************************************************************************************************/
	public boolean addToken(TokenInfo tokenInfo) throws SQLException {
        if (tokenInfo == null) {
            throw new IllegalArgumentException();
        }
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();
            String sql = "INSERT INTO `" + TABLE_TOKEN + "` (`token`, `uid`, `expires`, `ctime`, `ip`, `device`) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tokenInfo.token);
            pstmt.setLong(2, tokenInfo.uid);
            pstmt.setTimestamp(3, tokenInfo.expires);
            pstmt.setTimestamp(4, tokenInfo.ctime);
            pstmt.setString(5, tokenInfo.ip);
            pstmt.setString(6, tokenInfo.device);
            
            return pstmt.executeUpdate() == 1 ? true : false;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public TokenInfo getTokenInfo(String token) throws SQLException {
        if (null == token) {
            throw new IllegalArgumentException();
        }
        Connection conn = null;
        try {
        	conn = this.mysql.getConnection();

            String sql = "SELECT `uid`, `expires` , `ctime`, `ip`, `device` FROM `token` WHERE `token` = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new TokenInfo(token, rs.getLong(1), rs.getTimestamp(2), rs.getTimestamp(3), rs.getString(4), rs.getString(5));
            }
            return null;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	public boolean deleteToken(String token) throws SQLException {
		if (null == token) {
            throw new IllegalArgumentException();
        }
		Connection conn = null;
		try {
			conn = this.mysql.getConnection();
			
			String sql = "DELETE FROM `" + TABLE_TOKEN + "` WHERE `token` = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, token);
            return pstmt.executeUpdate() == 1 ? true : false;
		} finally {
            if (conn != null) {
                conn.close();
            }
        }
	}
	/****************************************************************************************/
	
	public boolean setLoginId(String type, String value, Long uid) throws SQLException {
        if (null == type || value == null || null == uid) {
            throw new IllegalArgumentException();
        }
        Connection conn = null;
        try {
        	conn = this.mysql.getConnection();

            String sql = "INSERT INTO `login_id` (`type`, `value`, `uid`) VALUES (?, ?, ?) on duplicate key update `value` = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            pstmt.setString(2, value);
            pstmt.setLong(3, uid);
            pstmt.setString(4, value);
            pstmt.executeUpdate();

            return true;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
	
	/**
	 * 验证登录 Id
	 * @return 用户 uid
	 */
	public Long checkLoginId(String type, String value) throws SQLException {
		if (null == type || value == null) {
            throw new IllegalArgumentException();
        }
        Connection conn = null;
        try {
            conn = this.mysql.getConnection();

            String sql = "SELECT `uid` FROM `" + TABLE_LOGIN_ID + "` WHERE `type` = ? and `value` = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            pstmt.setString(2, value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
	}

}
