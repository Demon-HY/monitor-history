package module.web;

import java.util.List;

import monitor.service.http.ApiGateway;
import monitor.service.http.protocol.JsonProtocol;
import monitor.service.http.protocol.JsonReq;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;

public class WebHttpApi{
    
    protected WebApi webApi;
    
    public WebHttpApi(WebApi webApi){
        this.webApi = webApi;
    }
    
    @ApiGateway.ApiMethod(protocol = JsonProtocol.class)
    public JsonResp loadPageScript(JsonReq req) throws Exception {
        
    	String page = req.paramGetString("page", true);
    	List<String> scripts = webApi.loadPageScript(req.env, page);
    	
    	JsonResp resp = new JsonResp(RetStat.OK);
    	resp.resultMap.put("rows", scripts);
        return resp;
    }
    
}
