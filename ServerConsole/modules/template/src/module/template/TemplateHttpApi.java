package module.template;

import monitor.exception.UnInitilized;

public class TemplateHttpApi {

	private TemplateApi templateApi;
	private static TemplateHttpApi templateHttpApi;
	private TemplateHttpApi(TemplateApi templateApi) {
		this.templateApi = templateApi;
	}
	
	public static void init(TemplateApi templateApi) {
		templateHttpApi = new TemplateHttpApi(templateApi);
	}
	
	public static TemplateHttpApi getInst() {
		if (null == templateHttpApi) {
			new UnInitilized();
		}
		return templateHttpApi;
	}
}
