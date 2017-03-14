package monitor.service.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import monitor.service.db.MySql;

public class CronModel {
    
    protected MySql mysql;
    
    public CronModel(MySql mysql) throws SQLException {
        this.mysql = mysql;

        this.initTable();
    }

    private void initTable() throws SQLException {
        Connection conn = this.mysql.getConnection();
        try {
            String cronTable = "CREATE TABLE IF NOT EXISTS `cron` (\n" + 
                    "`task_id` varchar(255) NOT NULL,\n" + 
                    "`last_run` bigint(20) NOT NULL,\n" + 
                    "`status` int NOT NULL,\n" + 
                    "`ver` int NOT NULL,\n" + 
                    "`attr` varchar(4096) NOT NULL,\n" + 
                    "PRIMARY KEY(`task_id`)\n" + 
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            conn.createStatement().executeUpdate(cronTable);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    
    }
    
    public CronTask getCrontTask(String taskId) throws SQLException {
        if (taskId == null) {
            throw new IllegalArgumentException();
        }
        
        String sql = "SELECT `task_id`, `last_run`, `status`, `ver`, `attr` FROM `cron` WHERE `task_id`=?;";
        Connection conn = this.mysql.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, taskId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                CronTask cronTask = new CronTask(rs.getString(1), rs.getLong(2), rs.getLong(3), rs.getLong(4), rs.getString(5));
                return cronTask;
            }

            return null;
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }

    public boolean AddCronTask(CronTask cronTask) throws SQLException {
        if (cronTask == null) {
            throw new IllegalArgumentException();
        }
        
        Connection conn = this.mysql.getConnection();
        try {
            String sql = "INSERT INTO `cron` (`task_id`, `last_run`, `status`, `ver`, `attr`) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cronTask.taskId);
            pstmt.setLong(2, cronTask.lastRunTm);
            pstmt.setLong(3, cronTask.status);
            pstmt.setLong(4, cronTask.ver);
            pstmt.setString(5, cronTask.attr);

            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    return false;
                }
                throw e;
            }
            
            return true;
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }
    
    public boolean UpdateCronTask(CronTask cronTask, long lastVer) throws SQLException {
        if (cronTask == null) {
            throw new IllegalArgumentException();
        }
        
        Connection conn = this.mysql.getConnection();
        try {
            String sql = "UPDATE `cron` SET `last_run` = ?, `status` = ?, `ver` = ?, `attr` = ? WHERE `task_id` = ? AND `ver` = ?;";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cronTask.lastRunTm);
            pstmt.setLong(2, cronTask.status);
            pstmt.setLong(3, cronTask.ver);
            pstmt.setString(4, cronTask.attr);
            pstmt.setString(5, cronTask.taskId);
            pstmt.setLong(6, lastVer);

            int affectedRows = pstmt.executeUpdate();
            assert affectedRows <= 1;
            return affectedRows == 1;
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }
    
    public boolean setCronTaskStatus(String taskId, long status) throws SQLException {
        if (taskId == null) {
            throw new IllegalArgumentException();
        }
        
        Connection conn = this.mysql.getConnection();
        try {
            String sql = "UPDATE `cron` SET `status` = ? WHERE `task_id` = ?;";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, status);
            pstmt.setString(2, taskId);

            int affectedRows = pstmt.executeUpdate();
            assert affectedRows <= 1;
            return affectedRows == 1;
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }
    
    /* ------------------------------------------------------------ */

    public static class CronTask {
        public String taskId;
        public long lastRunTm;
        public long status;
        public long ver;
        public String attr;
        
        public CronTask(String taskId, long lastRunTm, long status, long ver, String attr) {
            this.taskId = taskId;
            this.lastRunTm = lastRunTm;
            this.status = status;
            this.ver = ver;
            this.attr = attr;
        }
    }
    
}
