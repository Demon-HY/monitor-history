package module.web.event;

import java.util.Map;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import monitor.service.http.Env;

public class PreRedirectEvent extends Event {
    
    public static enum Type implements EventType {
        /**
         * 事件类型标识：重定向之前
         */
        PRE_REDIRECT
    }
    
    public Env env;
    
    public int status;
    public Map<String, String> headers;
    public byte[] content;
    
    public void setReturnInfo(int status, Map<String, String> headers, byte[] content) {
        this.isContinue = false;
        this.status = status;
        this.headers = headers;
        this.content = content;
    }
    
}
