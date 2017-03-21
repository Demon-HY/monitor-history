package monitor.service.http;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import monitor.service.log.Logger;
import monitor.utils.LanguageUtil;
import monitor.utils.ServletUtil;
import monitor.service.http.protocol.DefaultProtocol;
import monitor.service.http.protocol.Protocol;
import monitor.service.http.protocol.RetStat;

/**
 * Api 接口的网关，HttpServlet 的实现类
 * @author monitor
 *
 */
public class ApiGateway extends HttpServlet {

    private static final long serialVersionUID = -464726855286758239L;
    public static final String SERVER_HEADER = "dServer/0.1";
    
    @Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ApiMethod {
        String apiName() default "";
        Class<?> protocol() default DefaultProtocol.class;
        int option() default -1;
        String strOption() default "";
    }
	
    public static class MethodInfo {
    	public Method m;
    	public String name;
    	public Protocol protocol;
    }
    /* ------------------------------------------------------------ */
    
	private Object apiSuite;
    private String moduleName;
    private Logger logger;
	private Map<String, MethodInfo> apiMap = new HashMap<String, MethodInfo>();
	
	public ApiGateway(Object apiSuite, String moduleName, Logger logger) {
		this.apiSuite = apiSuite;
		this.moduleName = moduleName;
		this.logger = logger;
		
		this._loadApiMap();
	}
	
	public void listLoadedApis() {
		for (Map.Entry<String, MethodInfo> e: this.apiMap.entrySet()) {
			System.out.println(e.getKey());
		}
	}
	
	private void _loadApiMap() {
		Class<?> c = this.apiSuite.getClass();
		for (Method m: c.getDeclaredMethods()) {
			
			// Find ApiMethod
			Annotation apiMethodAnno = null;
			for (Annotation a: m.getDeclaredAnnotations()) {
				if (a.annotationType().equals(ApiMethod.class)){
				    apiMethodAnno = a;
					break;
				}
			}
			if (apiMethodAnno == null) {
				continue;
			}
			
			// Add ApiMethod
			MethodInfo method = new MethodInfo();
			method.m = m;
			
			ApiMethod apiMethod = (ApiMethod)apiMethodAnno;
			method.name = apiMethod.apiName();
			if (method.name.length() == 0) {
				method.name = m.getName();
			}
			
			try {
				Class<?> protocolClass = apiMethod.protocol();
				method.protocol = (Protocol)protocolClass.newInstance();
				if (apiMethod.option() != -1){
					method.protocol.setOption(apiMethod.option());
				}
				method.protocol.setStrOption(apiMethod.strOption());
				
				if (!method.protocol.isValidMethod(m)) {
					this.logger.err(String.format("Invalid method, %s %s", this.moduleName, method.name));
					continue;
				}
			} catch (Exception e) {
				this.logger.err(String.format("Create protocol instance exception, %s %s", 
						this.moduleName, method.name), e);
				continue;
			}
			
			apiMap.put(method.name, method);
		}
	}
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setHeader("Server", SERVER_HEADER);
		Env env = new Env(request, response, this.moduleName);
		
		MethodInfo method = null;
		try {
		    method = _service(env);
		} catch (Exception e) {
			env.getLogger().err("Unknown Exception", e);
		}
		
		// Write access log
		logAccess(method, env);
	}
	
	protected MethodInfo _service(Env env) throws ServletException, IOException {
		
		env.request.setCharacterEncoding("UTF-8");
		env.response.setCharacterEncoding("UTF-8");
        
		String apiName = getApiName(env);
		MethodInfo method = apiMap.get(apiName);
		
		if (method == null) {
			ServletUtil.sendHttpResponse(env.response,  HttpServletResponse.SC_NOT_FOUND,  "{\"stat\": \"ERR_API_NOT_FOUND\"}");
		} else {
			try {
		        method.protocol.process(env, this.apiSuite, method.m);
			} catch (Exception e) {
				env.getLogger().err("API Processing Error", e);
				
				ServletUtil.sendHttpResponse(env.response, 
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
						"{\"stat\": \"ERR_SERVER_EXCEPTION\", " +
						"\"errText\": \"" + LanguageUtil.getInst().getText(RetStat.ERR_SERVER_EXCEPTION, env.Language) + "\"}");
			}
		}
		
		return method;
	}
	
	protected void logAccess(MethodInfo method, Env env) {
		final char SEP = '\t';
		StringBuffer sb = new StringBuffer(256);
		sb.append(env.ip);
		sb.append(SEP);
		sb.append(env.reqId);
		sb.append(SEP);
		sb.append(System.currentTimeMillis() - env.reqStartTm);
		sb.append(SEP);
		sb.append(env.response.getStatus());
		sb.append(SEP);
		sb.append(method == null ? getApiName(env) : method.name);
		sb.append(SEP);
		sb.append(env.stat);
		sb.append(SEP);
		sb.append(env.device);
		sb.append(SEP);
		for (Map.Entry<String, Long> e: env.timings.entrySet()) {
			sb.append(e.getKey());
			sb.append('=');
			sb.append(e.getValue());
			sb.append(',');
		}
		sb.append(SEP);
		for (Map.Entry<String, Object> e: env.logParams.entrySet()) {
			sb.append(e.getKey());
			sb.append('=');
			sb.append(e.getValue());
			sb.append(',');
		}
		sb.append(SEP);
		if (env.errMsg != null){
			sb.append(env.errMsg);
		}
		
		this.logger.acc(sb.toString());
	}

	private static String getApiName(Env env) {
	    String path = env.request.getPathInfo().substring(1);
	    int index = path.indexOf("/");
	    if (-1 != index) {
	        path = path.substring(0, index);
	        return path;
	    }
	    return path;
	}
}
