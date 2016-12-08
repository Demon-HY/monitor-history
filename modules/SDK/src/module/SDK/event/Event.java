package module.SDK.event;

//@javadoc

/**
 * 事件接口
 */
public class Event {
    
    /**
     * 是否停止事件广播
     */
    public boolean stopDispatch = false;
    
    /**
     * 告知逻辑是否继续往下走
     */
    public boolean isContinue = true;
    
    /**
     * 返回码<br>
     * isContinue为false时，将返回码封装到异常，并抛出
     */
    public String stat;
    
    /**
     * 缘由<br>
     * isContinue为false时的说明
     */
    public String breakReason;
    
    /**
     * 被处理的次数
     */
    public int iteration = 0;
    
    /**
     * 最后一个处理该事件的类
     */
    public Class<?> lastHandler;

}
