package monitor.service.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestHttpServer {

	public static class MyFilter implements Filter {

        public void destroy() {
            System.out.println("Stopping filter");
        }

        public void doFilter(ServletRequest request, ServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            System.out.println("doFilter called with: " + request);

            chain.doFilter(request, response);
        }

        public void init(FilterConfig filterConfig) throws ServletException {
            Enumeration<String> enums = filterConfig.getInitParameterNames();
            String str = filterConfig.getServletContext().getContextPath();
            System.out.println(str);
            while (enums.hasMoreElements()) {
                String param = (String) enums.nextElement();
                System.out.println(param + ":" + filterConfig.getInitParameter(param));
            }
        }

    }
	
	public static class HelloServlet extends HttpServlet
	{
        private static final long serialVersionUID = 5665973623094237024L;

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	    {
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.getWriter().println("<h1>Hello SimpleServlet</h1>");
	    }
	}
	
	public static class RootServlet extends HttpServlet
	{

		private static final long serialVersionUID = -7089254496023968592L;

		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	    {
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.getWriter().println("RootServlet");
	    }
	}
	
	public static class HiServlet extends HttpServlet
	{
        private static final long serialVersionUID = 6765925934715380029L;

        protected void service(HttpServletRequest request, HttpServletResponse response) 
				throws ServletException, IOException {
			
			System.out.println(request.isAsyncSupported());
			
			request.setAttribute("hello", 1);
			System.out.println(request.getAttribute("hello"));
			
			System.out.println(request.getDispatcherType());
			
			/* ------------------------------------------------------------ */
			response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.getWriter().println(request.getMethod());
	        response.getWriter().println("<h1>Hi SimpleServlet</h1>");
		}
	}
	
	public static void main(String[] args) throws Exception {
		
	    HttpServer.ServerConfig conf = new HttpServer.ServerConfig();
	    conf.host = "0.0.0.0";
	    conf.port = 8081;
		HttpServer.init("/data", conf);
		
		HttpServer server = HttpServer.getInst("m1");
//		server.registFileService("nginx", "nginx");
//		server.registApiService("hello", new HelloServlet());
//		server.registApiService("hi", new HiServlet());
//		server.registJspService("jsp");//统映射多模块的jsp资源路径
//		
//		server = HttpServer.getInst("m2");
//        server.registJspService("html");
//		
//        server.registApiServiceFree("", new RootServlet());
        Map<String, String> params = new HashMap<String, String>();
        params.put("xs", "sss");
		server.registApiService(null, MyFilter.class, params);
		
		HttpServer.startService();
		
		// 访问   http://localhost:8081/jsp/*.jsp
		// 会遍历查找  m1模块下的xxx/jsp目录下的*.jsp  以及     m2模块下的xxx/html目录下的*.jsp
	}

}
