package module.SDK.event.type;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.GroupInfo;
import monitor.service.http.Env;

public class GroupEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加群组前</br>
         * 有效参数：</br>
         * groupInfo
         */
	    PRE_ADD_GROUP,
	    /**
         * 事件类型标识：添加群组后</br>
         * 有效参数：</br>
         * groupInfo
         */
	    POST_ADD_GROUP,
	    
	    /**
         * 事件类型标识：修改群组前</br>
         * 有效参数：</br>
         * groupInfo
         */
        PRE_EDIT_GROUP,
        /**
         * 事件类型标识：修改群组后</br>
         * 有效参数：</br>
         * groupInfo
         */
        POST_EDIT_GROUP,
        
        /**
         * 事件类型标识：删除群组前</br>
         * 有效参数：</br>
         * group_id
         */
        PRE_DELETE_GROUP,
        /**
         * 事件类型标识：删除群组后</br>
         * 有效参数：</br>
         * groupInfo
         */
        POST_DELETE_GROUP,
        
        /**
         * 事件类型标识：获取群组前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_GROUP,
        /**
         * 事件类型标识：获取群组后</br>
         * 有效参数：</br>
         * pairGroups
         */
        POST_LIST_GROUP,
        
        /**
         * 事件类型标识：获取群组关联的模板前</br>
         * 有效参数：</br>
         * action_id
         */
        PRE_LIST_GROUP_TEMPLATE,
        /**
         * 事件类型标识：获取群组关联的模板后</br>
         * 有效参数：</br>
         * groupRelationObjectIds
         */
        POST_LIST_GROUP_TEMPLATE,
	}
	
	public Env env;
	public Long group_id;
	public String name;
	public String memo;
	public Timestamp ctime;
	public Timestamp mtime;
	
	public String order;
	public String sort;
	public Pair<Integer, List<GroupInfo>> pairGroups;
	
	/** 群组关联的对象 Id 集合 */
	public Map<Long, List<Long>> groupRelationObjectIds;
	
	public GroupInfo groupInfo;
	public GroupEvent(){}
	public GroupEvent(Env env){
		this.env = env;
	}

	/**
	 * 添加群组/修改群组/删除群组后
	 * @param env
	 * @param groupInfo
	 */
	public GroupEvent(Env env, GroupInfo groupInfo) {
		this.env = env;
		this.groupInfo = groupInfo;
	}
	
	/**
	 * 删除群组前/获取群组关联的对象前
	 * @param env
	 * @param group_id
	 */
	public GroupEvent(Env env, Long group_id) {
		this.env = env;
		this.group_id = group_id;
	}
	
	/**
	 * 获取群组前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public GroupEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 获取群组后
	 * @param env
	 * @param pairGroups
	 */
	public GroupEvent(Env env, Pair<Integer, List<GroupInfo>> pairGroups) {
		this.env = env;
		this.pairGroups = pairGroups;
	}
	
	/**
	 * 获取群组关联的对象后
	 * @param env
	 * @param groupRelationObjectIds
	 */
	public GroupEvent(Env env, Map<Long, List<Long>> groupRelationObjectIds) {
		this.env = env;
		this.groupRelationObjectIds = groupRelationObjectIds;
	}
}
