package module.template;

import module.SDK.SdkCenter;
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
}
