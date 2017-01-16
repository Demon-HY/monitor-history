package module.SDK.event.type;

import java.sql.Timestamp;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.HostInfo;
import monitor.service.http.Env;

public class HostEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加主机前</br>
         * 有效参数：</br>
         * hostInfo
         */
	    PRE_ADD_HOST,
	    /**
         * 事件类型标识：添加主机后</br>
         * 有效参数：</br>
         * hostInfo
         */
	    POST_ADD_HOST,
	    
	    /**
         * 事件类型标识：修改主机前</br>
         * 有效参数：</br>
         * hostInfo
         */
        PRE_EDIT_HOST,
        /**
         * 事件类型标识：修改主机后</br>
         * 有效参数：</br>
         * hostInfo
         */
        POST_EDIT_HOST,
        
        /**
         * 事件类型标识：删除主机前</br>
         * 有效参数：</br>
         * host_id
         */
        PRE_DELETE_HOST,
        /**
         * 事件类型标识：删除主机后</br>
         * 有效参数：</br>
         * hostInfo
         */
        POST_DELETE_HOST,
        
        /**
         * 事件类型标识：查询主机前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_HOST,
        /**
         * 事件类型标识：查询主机后</br>
         * 有效参数：</br>
         * pairHosts
         */
        POST_LIST_HOST,
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
	
	public String order;
	public String sort;
	public Pair<Integer, List<HostInfo>> pairHosts;
	
	public HostInfo hostInfo;
	public HostEvent(){}
	
	public HostEvent(Env env) {
		this.env = env;
	}
	
	/**
	 * 添加主机/修改主机/删除主机后
	 * @param env
	 * @param hostInfo
	 */
	public HostEvent(Env env, HostInfo hostInfo) {
		this.env = env;
		this.hostInfo = hostInfo;
	}
	
	/**
	 * 删除主机前
	 * @param env
	 * @param host_id
	 */
	public HostEvent(Env env, Long host_id) {
		this.env = env;
		this.host_id = host_id;
	}
	
	/**
	 * 查询主机前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public HostEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 查询主机后
	 * @param env
	 * @param pairHosts
	 */
	public HostEvent(Env env, Pair<Integer, List<HostInfo>> pairHosts) {
		this.env = env;
		this.pairHosts = pairHosts;
	}
	
}
