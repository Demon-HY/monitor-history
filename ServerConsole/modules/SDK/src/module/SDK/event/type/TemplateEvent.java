package module.SDK.event.type;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.TemplateInfo;
import monitor.service.http.Env;

public class TemplateEvent extends Event {

	public enum Type implements EventType {
		/**
         * 事件类型标识：添加模板前</br>
         * 有效参数：</br>
         * templateInfo
         */
	    PRE_ADD_TEMPLATE,
	    /**
         * 事件类型标识：添加模板后</br>
         * 有效参数：</br>
         * templateInfo
         */
	    POST_ADD_TEMPLATE,
	    
	    /**
         * 事件类型标识：修改模板前</br>
         * 有效参数：</br>
         * templateInfo
         */
        PRE_EDIT_TEMPLATE,
        /**
         * 事件类型标识：修改模板后</br>
         * 有效参数：</br>
         * templateInfo
         */
        POST_EDIT_TEMPLATE,
        
        /**
         * 事件类型标识：删除模板前</br>
         * 有效参数：</br>
         * template_id
         */
        PRE_DELETE_TEMPLATE,
        /**
         * 事件类型标识：删除模板后</br>
         * 有效参数：</br>
         * templateInfo
         */
        POST_DELETE_TEMPLATE,
        
        /**
         * 事件类型标识：获取模板前</br>
         * 有效参数：</br>
         * order</br>
         * sort
         */
        PRE_LIST_TEMPLATE,
        /**
         * 事件类型标识：获取模板后</br>
         * 有效参数：</br>
         * pairTemplates
         */
        POST_LIST_TEMPLATE,
        
        /**
         * 事件类型标识：获取模板关联的服务前</br>
         * 有效参数：</br>
         * action_id
         */
        PRE_LIST_TEMPLATE_SERVICE,
        /**
         * 事件类型标识：获取模板关联的服务后</br>
         * 有效参数：</br>
         * templateRelationObjectIds
         */
        POST_LIST_TEMPLATE_SERVICE,
        
        /**
         * 事件类型标识：获取模板关联的触发器前</br>
         * 有效参数：</br>
         * action_id
         */
        PRE_LIST_TEMPLATE_TRIGGER,
        /**
         * 事件类型标识：获取模板关联的触发器后</br>
         * 有效参数：</br>
         * templateRelationObjectIds
         */
        POST_LIST_TEMPLATE_TRIGGER,
	}
	
	public Env env;
	public Long template_id;
	public String name;
	public Timestamp ctime;
	public Timestamp mtime;
	
	public String order;
	public String sort;
	public Pair<Integer, List<TemplateInfo>> pairTemplates;
	
	/** 模板关联的对象 Id 集合 */
	public Map<Long, List<Long>> templateRelationObjectIds;
	
	public TemplateInfo templateInfo;
	
	public TemplateEvent(){}
	public TemplateEvent(Env env){
		this.env = env;
	}

	/**
	 * 添加模板/修改模板/删除模板后
	 * @param env
	 * @param templateInfo
	 */
	public TemplateEvent(Env env, TemplateInfo templateInfo) {
		this.env = env;
		this.templateInfo = templateInfo;
	}
	
	/**
	 * 删除模板前/获取模板关联的对象前
	 * @param env
	 * @param template_id
	 */
	public TemplateEvent(Env env, Long template_id) {
		this.env = env;
		this.template_id = template_id;
	}
	
	/**
	 * 获取模板前
	 * @param env
	 * @param order
	 * @param sort
	 */
	public TemplateEvent(Env env, String order, String sort) {
		this.env = env;
		this.order = order;
		this.sort = sort;
	}
	
	/**
	 * 获取模板后
	 * @param env
	 * @param pairTemplates
	 */
	public TemplateEvent(Env env, Pair<Integer, List<TemplateInfo>> pairTemplates) {
		this.env = env;
		this.pairTemplates = pairTemplates;
	}
	
	/**
	 * 获取模板关联的对象后
	 * @param env
	 * @param templateRelationObjectIds
	 */
	public TemplateEvent(Env env, Map<Long, List<Long>> templateRelationObjectIds) {
		this.env = env;
		this.templateRelationObjectIds = templateRelationObjectIds;
	}
}
