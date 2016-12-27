package module.template;

import java.sql.SQLException;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.SdkCenter;
import module.SDK.info.TemplateInfo;
import module.SDK.inner.IBeans;
import module.SDK.inner.ITemplateApi;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;

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
}
