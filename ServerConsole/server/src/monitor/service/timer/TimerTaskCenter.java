package monitor.service.timer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TimerTaskCenter {

    public Timer timer;
    public TickTask tickTask;
    public Map<String, Item> tasks;
    public static TimerTaskCenter timerTaskCenter;

    public static TimerTaskCenter getInst() {
        if(null == timerTaskCenter)
            timerTaskCenter = new TimerTaskCenter();

        return timerTaskCenter;
    }

    private TimerTaskCenter() {
        tasks = new ConcurrentHashMap<String, Item>();
        timer = new Timer();
        tickTask = new TickTask();
        timer.schedule(tickTask, 0L, 1000);
    }

    public void setTask(ITask task, long tickcount, String key) {
        if(null != task && 0 < tickcount && null != key && 0 != key.length()) {
            Item item = new Item(task, tickcount);
            tasks.put(key, item);
        }
    }

    public boolean cancelTask(String key) {
        if(tasks.containsKey(key)) {
            tasks.remove(key);
            return true;
        }
        return false;
    }

    public interface ITask {
        public void run();
    }

    class TickTask extends TimerTask {
        @Override
        public void run() {
            Collection<Item> items = tasks.values();
            Iterator<Item> iterator = items.iterator();
            while (iterator.hasNext()) {
            	iterator.next().onTask();
            }
        }
    }

    class Item {
        public ITask task;
        long tickCount;
        long count;
        long delay;
        long last;
        long now;

        public Item(ITask task, long tickcount) {
            this.task = task;
            this.tickCount = tickcount;
            this.count = 0L;
            this.delay = 0L;
            this.last = 0L;
            this.now = 0L;
        }

        public void onTask() {
            if (++count >= tickCount) {
            	last = now > 0 ? now : System.currentTimeMillis();
            	now = System.currentTimeMillis();
                try {
                	new Runnable() {
                        @Override
                        public void run() {
                            task.run();
                        }
                    }.run();
                } catch(Exception e) {
                } finally {
                    count = 0L;
                }
                
                delay += (now - last) % (1000 * tickCount);
                count += delay / 1000;
                delay = delay > 1000 ? delay % 1000 : delay;
                now = count > 0 ? now + (tickCount - count) * 1000 : now;
            }
        }
    }
}

//class MyMain {
//    public static void main(String[] args) {
//        TimerTaskCenter.ITask task = new TimerTaskCenter.ITask() {
//            @Override
//            public void run() {
//                System.out.println(111);
//            }
//        };
//        TimerTaskCenter.ITask task2 = new TimerTaskCenter.ITask() {
//            @Override
//            public void run() {
//                System.out.println(222);
//            }
//        };
//        TimerTaskCenter.getInst().setTask(task, 10L, "test");
////        TimerTaskCenter.getInst().cancelTask("test");
//        
//        TimerTaskCenter.getInst().setTask(task2, 5L, "test2");
//    }
//}