package module.web.event;

import monitor.service.http.Env;

import java.util.LinkedList;
import java.util.List;

import module.SDK.event.Event;
import module.SDK.event.EventType;

public class LoadPageScriptEvent extends Event{
	
	public Env env;
	
    public final List<String> scripts = new LinkedList<String>();

    public final String page;
    
    public LoadPageScriptEvent(String page) {
    	this.page = page;
	}

	public void addScript(String script) {
        
    	scripts.add(script);
    }

    public static enum Type implements EventType {
    	/**
    	 * 加载静态文件
    	 */
    	LoadPageScript
    }
}
