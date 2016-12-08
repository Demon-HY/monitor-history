package monitor.service.cron;

//@javadoc

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Cron的设计目的是在无人值守的情况下，在指定的时间启动程序。
 * @author He
 *
 */
public class Cron extends Timer {
   
    public Cron(boolean isDaemon) {
        super(isDaemon);
    }
    
    /* ------------------------------------------------------------ */
    // 以下接口与 java.util.Timer 里的 schedule 接口相同
    
    public void schedule(TimerTask task, long delay) {
        super.schedule(task, delay);
    }
    
    public void schedule(TimerTask task, Date time) {
        super.schedule(task, time);
    }
    
    public void schedule(TimerTask task, long delay, long period) {
        processGlobalTimerTask(task, period);
        super.schedule(task, delay, period);
    }
    
    public void schedule(TimerTask task, Date firstTime, long period) {
        processGlobalTimerTask(task, period);
        super.schedule(task, firstTime, period);
    }
    
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        processGlobalTimerTask(task, period);
        super.scheduleAtFixedRate(task, delay, period);
    }
    
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        processGlobalTimerTask(task, period);
        super.scheduleAtFixedRate(task, firstTime, period);
    }
    
    protected void processGlobalTimerTask(TimerTask task, long period) {
        if (!(task instanceof GlobalTimerTask))
            return;
        
        GlobalTimerTask globalTimerTask = (GlobalTimerTask)task;
        globalTimerTask.setPeriod(period);
    }
    
    /* ------------------------------------------------------------ */
    protected static Cron cron = new Cron(false);
    
    public static Cron getInst() {
        return cron;
    }
}
