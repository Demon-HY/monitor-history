package monitor.service.cron;

import java.util.TimerTask;

public class TimerTest {
    
    /* ------------------------------------------------------------ */
    
    static class Task extends TimerTask {

        String name;
        private int counter;
        private long lastRun;
        
        public Task(String name) {
            this.counter = 0;
            this.lastRun = 0;
            this.name = name;
        }
        
        @Override
        public void run() {
            long delay = (System.currentTimeMillis() - this.lastRun);
            System.out.println(this.name + " " + this.counter + " " + delay + " ");
            this.lastRun = System.currentTimeMillis();
            this.counter++;
            
            try {
                if (this.counter < 4)
                    Thread.sleep(300);
                
                if (this.counter > 6)
                    this.cancel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public static void testLocalTask(String[] args) {
        Task t = new Task("A");
        Cron.getInst().schedule(t, 2000, 1000);
    }
    
    /* ------------------------------------------------------------ */
    
//    public static MySql getMySql() {
//        try {
//            String mysqlConfFile = "/etc/monitor/server.properties";
//            System.out.println("Using " + mysqlConfFile + " for db config");
//            
//            Config.init(mysqlConfFile);
//            PoolInfo pool = new PoolInfo(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_HOST),
//            Integer.parseInt(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_PORT)),
//            Config.get(MysqlConfig.CONF_MONITOR_MYSQL_USER),
//            Config.get(MysqlConfig.CONF_MONITOR_MYSQL_PSW),
//            Config.get(MysqlConfig.CONF_MONITOR_MYSQL_PARAMS),
//            Integer.parseInt(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_MAX_ACTIVE)),
//            Integer.parseInt(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_MAX_IDLE)),
//            Long.parseLong(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_MAX_WAIT)),
//            Long.parseLong(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_TIME_BETWEEN_EVICTION_RUNS_MILLIS)),
//            Integer.parseInt(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_NUM_TEST_PER_EVICTION_RUN)),
//            Long.parseLong(Config.get(MysqlConfig.CONF_MONITOR_MYSQL_POOL_MIN_EVICTABLE_TIME_MILLIS)));
//            
//            MySql.init(pool);
//            return MySql.getInst("");
//        } catch (Exception e) {
//            System.err.println("Initialize MySql exception.");
//            e.printStackTrace();
//            return null;
//        }
//    }
    
    public static class GlobalTask extends GlobalTimerTask {

        int counter = 0;
        long lastRun;
        
        @Override
        public void globalRun() {
            long delay = (System.currentTimeMillis() - this.lastRun);
            System.out.println(this.counter + " " + delay + " ");
            this.lastRun = System.currentTimeMillis();
            this.counter++;
        }
    }
    
    public static void testGlobalTask() {
//        try {
//            GlobalTimerTask.init(getMySql());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        
        GlobalTask task = new GlobalTask();
        Cron.getInst().schedule(task, 2000, 1000);
    }
    
    /* ------------------------------------------------------------ */
    
//    public static void main(String[] args) {
//        testGlobalTask();
//    }
}
