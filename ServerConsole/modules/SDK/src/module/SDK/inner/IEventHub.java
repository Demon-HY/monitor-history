package module.SDK.inner;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.event.IListener;

public interface IEventHub {
    public static final String name = "IEventHub";
    /**
     * 高优先级
     */
    public static final int PRIORITY_HIGHER = 5;
    
    /**
     * 正常优先级
     */
    public static final int PRIORITY_NORMAL = 10;
    
    /**
     * 低优先级
     */
    public static final int PRIORITY_LOWER = 15;
    
    
    public void registListener(EventType eventType, IListener listener);
    public void registListener(EventType eventType, IListener listener, int priority);
    public void dispatchEvent(EventType eventType, Event event);

}
