package module.web;

import java.util.List;

import module.web.event.LoadPageScriptEvent;
import module.SDK.SdkCenter;
import module.SDK.inner.IEventHub;
import monitor.Config;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;

public class WebApi {    
    public WebApi() {
    }
    
    private static WebApi webApi;

    public static void init() {
        webApi = new WebApi();
    }
    public static WebApi getInst() throws UnInitilized {
        if (webApi == null) {
            throw new UnInitilized();
        }
        return webApi;
    }

    /*-------------------------------------------*/
    
	public List<String> loadPageScript(Env env, String page) throws LogicalException {
		
		LoadPageScriptEvent lps = new LoadPageScriptEvent(page);
		lps.env = env;
		IEventHub eventHub = (IEventHub) SdkCenter.getInst().queryInterface(IEventHub.name, SdkCenter.ToString() + "InnerKey" + Config.ToString());
		eventHub.dispatchEvent(LoadPageScriptEvent.Type.LoadPageScript, lps);
		
		return null;
	}
    
}
