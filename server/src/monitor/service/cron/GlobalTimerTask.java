package monitor.service.cron;

// @javadoc

import java.sql.SQLException;
import java.util.TimerTask;

import monitor.exception.UnInitilized;
import monitor.service.cron.CronModel.CronTask;
import monitor.service.db.MySql;
import monitor.utils.Time;

public abstract class GlobalTimerTask extends TimerTask{

    protected String taskId;
    protected long period;
    
    public GlobalTimerTask() {
        init(this.getClass().getName());
    }
    
    public GlobalTimerTask(String taskId) {
        init(taskId);
    }
    
    private void init(String taskId) {
        this.taskId = taskId;
        try {
            setTaskStatus(this.taskId, STATUS_RUNNING);
        } catch (SQLException | UnInitilized e) {
            e.printStackTrace();
        }
    }
    
    public void setPeriod(long period) {
        this.period = period;
    }

    @Override
    public final void run() {
        try {
            if (!aquire(taskId, period)) {
                return;
            }
        } catch (SQLException | UnInitilized e) {
            e.printStackTrace();
            return;
        }
        
        globalRun();
    }

    public boolean cancel() {
        try {
            setTaskStatus(this.taskId, STATUS_CANCELED);
        } catch (SQLException | UnInitilized e) {
            e.printStackTrace();
        }
        return super.cancel();
    }
    
    public abstract void globalRun();
    
    /* ------------------------------------------------------------ */
    
    static final int STATUS_RUNNING = 1;
    static final int STATUS_CANCELED = 2;
    
    protected static CronModel _cronModel;
    
    protected static boolean aquire(String taskId, long period) throws SQLException, UnInitilized {
        if (_cronModel == null) {
            throw new UnInitilized("GlobalTimerTask now initilized with db conn");
        }
        
        CronModel.CronTask cronTask = _cronModel.getCrontTask(taskId);
        long now = Time.currentTimeMillis();

        if (cronTask == null) {
            cronTask = new CronTask(taskId, now, STATUS_RUNNING, 1, "");
            return _cronModel.AddCronTask(cronTask);
        }
        
        // Task is cancel or task is already run by other monitor
        // 0.9 is nothing but for time adjustment
        if (cronTask.status == STATUS_CANCELED ||
            cronTask.lastRunTm + period * 0.9 > now) {
            return false;
        }
        
        long lastVer = cronTask.ver;
        cronTask.ver += 1;
        cronTask.lastRunTm = now;
        return _cronModel.UpdateCronTask(cronTask, lastVer);
    }
    
    protected static void setTaskStatus(String taskId, int status) throws SQLException, UnInitilized {
        if (_cronModel == null) {
            throw new UnInitilized("GlobalTimerTask now initilized with db conn");
        }
        _cronModel.setCronTaskStatus(taskId, status);
    }
    
    public static void init(MySql mysql) throws SQLException {
        _cronModel = new CronModel(mysql);
    }
}
