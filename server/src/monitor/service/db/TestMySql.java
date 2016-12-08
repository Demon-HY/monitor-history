package monitor.service.db;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//public class TestMySql {
//
//	public static void main(String[] args) throws Exception {
//		MySql.init("jdbc:mysql://localhost", "3306", "", "monitor", "root", "root", "5", "100", "30", "1000", "180");
//		
//		MySql mysql = MySql.getInst("module_test");
//		
//		Connection conn = mysql.getConnection();
//		
//		// Create table
//		String sql = "CREATE TABLE IF NOT EXISTS kv(`key` INT, `value` VARCHAR(64));";
//		Statement stmt = conn.createStatement();
//		System.out.println(stmt.executeUpdate(sql));
//		
//		// Insert 
//		sql = "INSERT INTO `kv` VALUES (?, ?);";
//		PreparedStatement pstmt = conn.prepareStatement(sql);
//		pstmt.setInt(1,  0);
//		pstmt.setString(2, "hello");
//		System.out.println(pstmt.executeUpdate());
//		
//		// Select
//		sql = "SELECT * FROM `kv`;";
//		stmt = conn.createStatement();
//		ResultSet rs = stmt.executeQuery(sql);
//		while (rs.next()) {
//			System.out.println(String.format("%d %s", rs.getInt(1), rs.getString(2)));
//		}
//		
//		// Update
//		sql = "UPDATE `kv` SET `value` = ?;";
//		pstmt = conn.prepareStatement(sql);
//		pstmt.setString(1, "hi");
//		System.out.println(pstmt.executeUpdate());
//		
//		// Delete
//		sql = "DELETE FROM `kv`;";
//		stmt = conn.createStatement();
//		System.out.println(stmt.executeUpdate(sql));
//		
//		// Drop table
//		System.out.println(conn.createStatement().executeUpdate("DROP TABLE `kv`"));
//		
//	}
//
//}
