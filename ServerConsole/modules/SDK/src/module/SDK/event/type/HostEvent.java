package module.SDK.event.type;

import java.sql.Timestamp;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.HostInfo;
import monitor.service.http.Env;

public class HostEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加主机前<br>
         * 有效参数：<br>
         * hostInfo
         */
	    PRE_ADD_HOST,
	    /**
         * 事件类型标识：添加主机后<br>
         * 有效参数：<br>
         * hostInfo
         */
	    POST_ADD_HOST,
	    /**
         * 事件类型标识：修改主机前<br>
         * 有效参数：<br>
         * hostInfo
         */
        PRE_EDIT_HOST,
        /**
         * 事件类型标识：修改主机后<br>
         * 有效参数：<br>
         * hostInfo
         */
        POST_EDIT_HOST,
	}
	
	public Env env;
	public Long host_id;
	public String name;
	public String ip;
	public String monitored;
	public String status;
	public Integer interval;
	public String memo;
	public Timestamp ctime;
	public Timestamp mtime;
	
	public HostInfo hostInfo;
	
	public HostEvent(Env env, HostInfo hostInfo) {
		this.env = env;
		this.hostInfo = hostInfo;
	}
}
