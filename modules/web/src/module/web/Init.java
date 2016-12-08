package module.web;

import monitor.service.http.ApiGateway;
import monitor.service.http.HttpServer;
import monitor.service.log.Logger;
import monitor.Config;

public class Init {

    public static final String MODULE_NAME = "web";

    public static void init(String moduleDir) throws Exception {

        String schema = Config.get(WebConfig.CONF_WEB_SCHEME);
        if (schema != null) {
            schema = schema.trim().toLowerCase();
            switch (schema) {
            case "http" :
            case "https" : WebConfig.webSchema = schema; break;
            default : throw new IllegalArgumentException("not expected web scheme");
            }
        }
        
        String domain = Config.get(WebConfig.CONF_WEB_DOMAIN);
        WebConfig.webDomain = domain != null ? domain.trim() : null;
        
        String policy = Config.get(WebConfig.CONF_WEB_REDIRECT_POLICY);
        WebConfig.webRedirectPolicy = policy != null ? policy.trim() : null;
        
        WebApi webApi = new WebApi();

        WebApi.init();

        HttpServer server = HttpServer.getInst(MODULE_NAME);
        server.registApiService("api", new ApiGateway(new WebHttpApi(webApi), MODULE_NAME, Logger.getInst(MODULE_NAME)));
        server.registFileService("web");
        server.registApiServiceFree("/", new Redirection.RedirectionServlet());
        server.registApiService("redirect", new Redirection.JumpToWebServlet());
        server.registApiService("crossDomain", new Redirection.CrossDomainServlet());
    }
}
