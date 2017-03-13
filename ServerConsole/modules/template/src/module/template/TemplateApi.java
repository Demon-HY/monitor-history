package module.template;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.event.type.TemplateEvent;
import module.SDK.info.TemplateInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.ITemplateApi;
import module.SDK.stat.TemplateRetStat;

import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;

public class TemplateApi implements ITemplateApi{
	protected IBeans beans;
	protected TemplateModel templateModel;
	private static TemplateApi templateApi;
	private TemplateApi(IBeans beans, TemplateModel templateModel) throws LogicalException {
		this.beans = beans;
		this.templateModel = templateModel;
		
		SdkCenter.getInst().addInterface(ITemplateApi.name, this);
	}
	
	public static void init(IBeans beans, TemplateModel templateModel) throws LogicalException {
		templateApi = new TemplateApi(beans, templateModel);
	}
	
	public static TemplateApi getInst() throws UnInitilized {
		if (null == templateApi) {
			throw new UnInitilized();
		}
		return templateApi;
	}
	
	/**
	 * 获取模板列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<TemplateInfo>> listTemplate(Integer pageIndex, Integer pageSize,
			String order, String sort) throws SQLException {
		List<TemplateInfo> result = null;
		Integer count = null;
		result = this.templateModel.listTemplate(pageIndex, pageSize, order, sort);
		count = this.templateModel.countTemplate();
		
		return new Pair<Integer, List<TemplateInfo>>(count, result);
	}
	
	public TemplateInfo addTemplate(Env env, TemplateInfo templateInfo, List<Long> serviceIdList, List<Long> triggerIdList) 
			throws LogicalException, SQLException {
		// 发送添加模板前事件
		TemplateEvent templateEvent = new TemplateEvent(env, templateInfo);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.PRE_ADD_TEMPLATE, templateEvent);
		
		TemplateInfo temp = this.templateModel.getTemplateByName(templateInfo.name);
		if (null != temp) {
			throw new LogicalException(TemplateRetStat.ERR_TEMPLATE_NAME_EXISTED, 
					"TemplateApi.addTemplate add name(" + templateInfo.name + ") existed!");
		}
		
		this.templateModel.addTemplate(templateInfo);
		if (null != serviceIdList && serviceIdList.size() > 0) {
			this.templateModel.addTemplateServices(templateInfo.template_id, serviceIdList);
		}
		if (null != triggerIdList && triggerIdList.size() > 0) {
			this.templateModel.addTemplateTriggers(templateInfo.template_id, triggerIdList);
		}
		
		templateInfo = this.templateModel.getTemplateByName(templateInfo.name);
		
		// 发送添加模板后事件
		templateEvent = new TemplateEvent(env,  templateInfo);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.POST_ADD_TEMPLATE, templateEvent);
		
		return templateInfo;
	}
	
	public TemplateInfo editTemplate(Env env, TemplateInfo templateInfo, List<Long> serviceIdList, List<Long> triggerIdList) 
			throws LogicalException, SQLException {
		// 发送修改模板前事件
		TemplateEvent templateEvent = new TemplateEvent(env, templateInfo);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.PRE_EDIT_TEMPLATE, templateEvent);
		
		TemplateInfo temp = this.templateModel.getTemplateByTemplateId(templateInfo.template_id);
		if (null == temp) {
			throw new LogicalException(TemplateRetStat.ERR_TEMPLATE_ID_NOT_FOUND,
					"TemplateApi.editTemplate template_id(" + templateInfo.template_id + ") not found!");
		}
		
		this.templateModel.editTemplateByTemplateId(templateInfo);
		if (null != serviceIdList && serviceIdList.size() > 0) {
			this.templateModel.deleteTemplateServicesByTemplateId(templateInfo.template_id);
			this.templateModel.addTemplateServices(templateInfo.template_id, serviceIdList);
		} else {
			this.templateModel.deleteTemplateServicesByTemplateId(templateInfo.template_id);
		}
		if (null != triggerIdList && triggerIdList.size() > 0) {
			this.templateModel.deleteTemplateTriggersByTemplateId(templateInfo.template_id);
			this.templateModel.addTemplateTriggers(templateInfo.template_id, triggerIdList);
		} else {
			this.templateModel.deleteTemplateTriggersByTemplateId(templateInfo.template_id);
		}
		
		// 发送修改模板后事件
		templateEvent = new TemplateEvent(env,  templateInfo);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.POST_EDIT_TEMPLATE, templateEvent);
		
		return templateInfo;
	}
	
	public TemplateInfo deleteTemplate(Env env, Long template_id) throws LogicalException, SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new LogicalException(TemplateRetStat.ERR_TEMPLATE_ID_NOT_FOUND, 
					"TemplateApi.deleteTemplate template_id(" + template_id + ") not found!");
		}
		// 发送删除模板前事件
		TemplateEvent templateEvent = new TemplateEvent(env, template_id);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.PRE_DELETE_TEMPLATE, templateEvent);
		
		TemplateInfo templateInfo = this.templateModel.getTemplateByTemplateId(template_id);
		if (null == templateInfo) {
			throw new LogicalException(TemplateRetStat.ERR_TEMPLATE_ID_NOT_FOUND,
					"TemplateApi.deleteTemplate template_id(" + template_id + ") not found!");
		}
		
		this.templateModel.deleteTemplateByTemplateId(template_id);
		
		// 发送删除模板后事件
		templateEvent = new TemplateEvent(env,  templateInfo);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.POST_DELETE_TEMPLATE, templateEvent);
		
		return templateInfo;
	}
	
	public Map<Long, List<Long>> getTemplateServices(Env env, Long template_id) throws LogicalException, SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new LogicalException(TemplateRetStat.ERR_TEMPLATE_ID_NOT_FOUND,
					"TemplateApi.getTemplateService template_id(" + template_id + ") not found!");
		}
		// 发送获取模板关联的服务前事件
		TemplateEvent templateEvent = new TemplateEvent(env, template_id);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.PRE_LIST_TEMPLATE_SERVICE, templateEvent);
		
		TemplateInfo templateInfo = this.templateModel.getTemplateByTemplateId(template_id);
		if (null == templateInfo) {
			throw new LogicalException(TemplateRetStat.ERR_TEMPLATE_ID_NOT_FOUND,
					"TemplateApi.getTemplateService template_id(" + template_id + ") not found!");
		}
		
		Map<Long, List<Long>> templateServices = this.templateModel.getTemplateServicesByTemplateId(template_id);
		
		// 发送获取模板关联的服务后事件
		templateEvent = new TemplateEvent(env,  templateServices);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.POST_LIST_TEMPLATE_SERVICE, templateEvent);
		
		return templateServices;
	}
	
	public Map<Long, List<Long>> getTemplateTriggers(Env env, Long template_id) throws LogicalException, SQLException {
		if (null == template_id || template_id.longValue() < 1) {
			throw new LogicalException(TemplateRetStat.ERR_TEMPLATE_ID_NOT_FOUND,
					"TemplateApi.getTemplateTrigger template_id(" + template_id + ") not found!");
		}
		// 发送获取模板关联的触发器前事件
		TemplateEvent templateEvent = new TemplateEvent(env, template_id);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.PRE_LIST_TEMPLATE_TRIGGER, templateEvent);
		
		TemplateInfo templateInfo = this.templateModel.getTemplateByTemplateId(template_id);
		if (null == templateInfo) {
			throw new LogicalException(TemplateRetStat.ERR_TEMPLATE_ID_NOT_FOUND,
					"TemplateApi.getTemplateTrigger template_id(" + template_id + ") not found!");
		}
		
		Map<Long, List<Long>> templateTriggers = this.templateModel.getTemplateTriggersByTemplateId(template_id);
		
		// 发送获取模板关联的触发器后事件
		templateEvent = new TemplateEvent(env,  templateTriggers);
		this.beans.getEventHub().dispatchEvent(TemplateEvent.Type.POST_LIST_TEMPLATE_TRIGGER, templateEvent);
		
		return templateTriggers;
	}
}
