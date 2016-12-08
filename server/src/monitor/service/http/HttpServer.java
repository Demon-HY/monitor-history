package monitor.service.http;

import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration.ClassList;

import monitor.exception.UnInitilized;
import monitor.Config;
import monitor.ModuleMgr;

import org.eclipse.jetty.webapp.WebAppContext;

public class HttpServer {

//	public static final String HTTP_URL = "api/v1/";
	
    public static class ServerConfig {
        public String host;
        public int port;
        
        public boolean useSSL = false;
        public int sslPort;
        public String sslKeyStorePath;
        public String sslKeyStorePwd;
        
        public boolean customizeThreadPool = false;
        public int maxThread = 0;
        public int minThread = 0;
        public int threadIdleTimeout = 0;
        
        public int ioTimeout = 30000;
    }
    
    private static final String separator = "/";
	private String moduleName;
	
	private HttpServer(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String getServerHome() {
	    String home = serverHome == null ? ModuleMgr.getModulePath(moduleName) : serverHome;
	    return home;
	}
	
	/**
	 * 注册 Jsp 服务，路径前缀为 /{module_name}/{subDir}/
	 */
	public void registJspService(String subDir) {
        String resourcePath = getServerHome() + "/" + moduleName + "/" + subDir;
        resourcePaths.add(resourcePath);
    }
	
	/**
	 * 注册静态文件服务，路径前缀为 /{module_name}/
	 */
	public void registFileService(String subDir) {
		registFileService(null, subDir);
	}
	
	/**
	 * 注册静态文件服务，路径前缀为 /{module_name}/{urlPrefix}/
	 */
	public void registFileService(String urlPrefix, String subDir) {
		ResourceHandler handler = new ResourceHandler();
		handler.setResourceBase(getServerHome() + separator + moduleName + separator + subDir);
		
		String contextPath = separator + moduleName + (null == urlPrefix ? "" : separator + urlPrefix);
		ContextHandler context = new ContextHandler(contextPath);
		context.setHandler(handler);
		handlers.add(context);
	}
	
	/**
     * 注册静态文件服务，指定文件所在文件夹的绝对路径： absoluteDirPath
     * 路径前缀为 /{module_name}/{urlPrefix}/
     */
    public void registFileServiceAbsolute(String urlPrefix, String absoluteDirPath) {
        ResourceHandler handler = new ResourceHandler();
        handler.setResourceBase(absoluteDirPath);
        
        String contextPath = separator + moduleName + (null == urlPrefix ? "" : separator + urlPrefix);
        ContextHandler context = new ContextHandler(contextPath);
        context.setHandler(handler);
        handlers.add(context);
    }
	
	/**
	 * 注册 Servlet 服务，路径前缀为 /{module_name}/
	 */
	public void registApiService(Servlet servlet) {
		registApiService(null, servlet);
	}

	/**
	 * 注册 Servlet 服务，路径前缀为 /{module_name}/{urlPrefix}/
	 * 可添加监听器
	 */
	public void registApiService(String urlPrefix, Servlet servlet) {
        String contextPath = separator + moduleName + (null == urlPrefix ? "" : separator + urlPrefix);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(contextPath);
        context.addServlet(new ServletHolder(servlet), "/*");
        handlers.add(context);
    }
	
	/**
	 * 注册任意路径前缀的 Servlet 服务，路径前缀为 /{preifx}/ ,注意
	 * 1. 如果路径产生冲突，该接口可能把其它模块注册的接口覆盖
	 * 2. 可以使用此接口定义 "/" 的默认 Handler
	 */
	public void registApiServiceFree(String prefix, Servlet servlet) {
		String contextPath = separator + prefix;
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextPath);
		context.addServlet(new ServletHolder(servlet), "/*");
		handlers.add(context);
		
		// TODO 目前无法很好实现定义 "/" 的默认 Handler。当 preifx 传 "/" 时，所有未匹配到的路径都会被匹配到这个 Handler
	}
	
	/**
	 * 过滤器
	 * @param urlPrefix
	 * @param filter
	 * @param params
	 */
	public void registApiService(String urlPrefix, Class<? extends Filter> filter, Map<String, String> params) {
		String contextPath = separator + moduleName + (null == urlPrefix ? "" : separator + urlPrefix);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextPath);
		
        ServletHandler handler = new ServletHandler();
        FilterHolder fh = handler.addFilterWithMapping(filter, "/*", EnumSet.of(DispatcherType.REQUEST));

        if (null != params) {
	        Set<String> keys = params.keySet();
	        for (String key : keys) {
	        	String value = params.get(key);
	        	fh.setInitParameter(key, value);
	        }
        }
        
        context.addFilter(fh, "/*", EnumSet.of(DispatcherType.REQUEST));
        context.setHandler(handler);
        
        handlers.add(context);
	}
	
	/* ------------------------------------------------------------ */
	
	public static HttpServer getInst(String moduleName) throws UnInitilized {
		if (jettyServer == null) {
			throw new UnInitilized("HttpServer not initialized yet.");
		}
		return new HttpServer(moduleName);
	}
	
	private static String serverHome;
	private static Server jettyServer;
	private static List<Handler> handlers;
	private static List<String> resourcePaths;
	
	public static void init(String serverHome, ServerConfig conf) throws Exception {
		if (jettyServer != null) {
			throw new Exception("HttpServer already initialized.");
		}
		
		QueuedThreadPool pool= new QueuedThreadPool();
		if (conf.customizeThreadPool) {
		    pool.setMaxThreads(conf.maxThread);
		    pool.setMinThreads(conf.minThread);
		    pool.setIdleTimeout(conf.threadIdleTimeout);
		}
		
		jettyServer = new Server(pool);
		
		// Set the http connector
		HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.addCustomizer(new ForwardedRequestCustomizer());
		ServerConnector http = new ServerConnector(jettyServer, 
		    new HttpConnectionFactory(httpConfig));
        http.setHost(conf.host);
        http.setPort(conf.port);
        http.setIdleTimeout(conf.ioTimeout);
        jettyServer.addConnector(http);
		
        // Set the https connector
        if (conf.useSSL) {
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(conf.sslKeyStorePath);
            sslContextFactory.setKeyStorePassword(conf.sslKeyStorePwd);
            sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
                    "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                    "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                    "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                    "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                    "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
            
            HttpConfiguration httpsConfig = new HttpConfiguration();
            httpsConfig.addCustomizer(new SecureRequestCustomizer());
            ServerConnector https = new ServerConnector(jettyServer,
                    new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
                    new HttpConnectionFactory(httpsConfig));
            https.setPort(conf.sslPort);
            https.setIdleTimeout(conf.ioTimeout);
            jettyServer.addConnector(https);
        }
        
        HttpServer.serverHome = serverHome;
		handlers = new LinkedList<Handler>();
		resourcePaths = new LinkedList<String>();
		
		removeServerHeader(jettyServer);
		
	}
	
	private static void addJspPaths() throws URISyntaxException {
	    
        if(resourcePaths.size() > 0){
            WebAppContext context = new WebAppContext();
            context.setInitParameter(
                    "org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
            
            String jspContextPath = Config.get(HttpConfig.CONF_MONITOR_HTTP_WEB_CONTEXT_PATH);
            if(jspContextPath == null){
                jspContextPath = "/";
            }
            context.setContextPath(jspContextPath);
            ResourceCollection resources = new ResourceCollection(resourcePaths.toArray(new String[0]));
            context.setBaseResource(resources);
            ClassList classlist = ClassList.setServerDefault(jettyServer);
            classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", 
                    "org.eclipse.jetty.annotations.AnnotationConfiguration");
            context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                    ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");

            handlers.add(context);
        }
        
	}
	
	public static void removeServerHeader(Server server) {
		for(Connector y : server.getConnectors()) {
		    for(ConnectionFactory x  : y.getConnectionFactories()) {
		        if(x instanceof HttpConnectionFactory) {
		            ((HttpConnectionFactory)x).getHttpConfiguration().setSendServerVersion(false);
		        }
		    }
		}
	}
	
	public static void startService() throws Exception {
	    addJspPaths();
	    
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(handlers.toArray(new Handler[0]));
		jettyServer.setHandler(contexts);
		
		jettyServer.start();
		jettyServer.join();
	}
	
	public static void join() throws InterruptedException {
		jettyServer.join();
	}
}
