package module.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;

import monitor.exception.LogicalException;
import monitor.service.http.Env;
import monitor.service.http.HttpConfig;
import monitor.utils.ByteArrayBuffer;
import monitor.utils.ServletUtil;
import module.web.event.PreRedirectEvent;
import module.SDK.SdkCenter;
import module.SDK.inner.IEventHub;
import monitor.Config;

public class Redirection {

    public static class RedirectionServlet extends HttpServlet {

        private static final long serialVersionUID = -7896965395239715327L;
        public RedirectionServlet() {
        }

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            
        	IEventHub eventHub = null;
			try {
				eventHub = (IEventHub) SdkCenter.getInst().queryInterface(IEventHub.name, SdkCenter.ToString() + "InnerKey" + Config.ToString());
			} catch (LogicalException e) {
				e.printStackTrace();
			}
            PreRedirectEvent event = new PreRedirectEvent();
            event.env = new Env(request, response, Init.MODULE_NAME);
            eventHub.dispatchEvent(PreRedirectEvent.Type.PRE_REDIRECT, event);
            
            if (event.isContinue) {
                String url = "/web/login.html";
                
                String host = request.getServerName();
                if (WebConfig.webDomain != null && !WebConfig.webDomain.equals(host)) {
                    
                    if ("error".equalsIgnoreCase(WebConfig.webRedirectPolicy)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    } else {
                        host = WebConfig.webDomain;
                        int port = request.getServerPort();
                        url = String.format("%s://%s:%s/web/login.html", request.getScheme(), WebConfig.webDomain, port);
                    }
                }
                
                if (WebConfig.webSchema != null && WebConfig.webSchema.trim().equals("https")) {
                    String portStr = Config.get(HttpConfig.CONF_MONITOR_HTTPS_PORT);
                    
                    int port = request.getServerPort();
                    if (portStr != null) {
                        port = Integer.parseInt(portStr);
                        url = String.format("%s://%s:%s/web/login.html", "https", host, port);
                    }
                }
                response.sendRedirect(url);
                
            } else {
                response.setStatus(event.status);
                if (null != event.headers) {
                    Set<String> keys = event.headers.keySet();
                    for (String key : keys) {
                        String value = event.headers.get(key);
                        response.addHeader(key, value);
                    }
                    if (null != event.content) {
                        OutputStream os = response.getOutputStream();
                        os.write(event.content);
                        os.flush();
                        os.close();
                    }
                }
            }
        }
    }
    
    public static class JumpToWebServlet extends HttpServlet {

        private static final long serialVersionUID = -9088715857855431015L;
        
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            
            String params = request.getParameter("params");
            byte[] tmp = Base64.decodeBase64(params.getBytes());
            params = new String(tmp);
            
            JSONObject jo = JSONObject.parseObject(params);
            String url = jo.getString("url");
            String token = jo.getString("token");
            
            Env env = new Env(request, response, Init.MODULE_NAME);
            env.logParam("url", url);
            env.logParam("token", token);
            logAcc(env, "redirect");
            
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            response.addCookie(cookie);
            
            response.sendRedirect(url);
        }
    }
    
    public static class CrossDomainServlet extends HttpServlet {

        private static final long serialVersionUID = -5457655703152963972L;
        
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            
        	InputStream is = req.getInputStream();
            byte[] data = new byte[1024];
            ByteArrayBuffer bab = new ByteArrayBuffer(1024);
            
            int len = 0;
            while ((len = is.read(data)) != -1) {
            	bab.append(data, 0, len);
            }
            is.close();
            data = bab.toByteArray();
        	
            String urlStr = req.getParameter("url");
            URL url = null;
            boolean illegalUrl = false;
            if (null != urlStr) {
            	urlStr = ServletUtil.decode(urlStr);
                try {
                	url = new URL(urlStr);
                    illegalUrl = true;
                } catch (Exception e) {}
            }
            
            if (!illegalUrl) {
                resp.sendRedirect("/");
                return;
            }
            
            String cookiePath = req.getParameter("cookiePath");
            if (cookiePath != null) {
                cookiePath = ServletUtil.decode(cookiePath);
            }
            
            Enumeration<String> headerNames = req.getHeaderNames();
            
            Map<String, String> headers = new HashMap<String, String>();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = req.getHeader(name);
                headers.put(name, value);
            }
            
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(6000);
            if (null != headers) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    String value = headers.get(key);
                    value = ServletUtil.decode(value);
                    httpConn.addRequestProperty(key, value);
                }
            }
            
            if (null != data && data.length > 0) {
            	httpConn.setRequestMethod("POST");
                OutputStream writer = httpConn.getOutputStream();
                writer.write(data);
                writer.flush();
                writer.close();
            } else {
            	httpConn.setRequestMethod("GET");
            }
            
            Map<String, List<String>> rspHs = httpConn.getHeaderFields();
            Set<String> names = rspHs.keySet();
            for (String name : names) {
            	if (name == null) {
            		continue;
            	}
                List<String> hs = rspHs.get(name);
                if (null != hs) {
                	String value = null;
                	if (name.equalsIgnoreCase("Set-Cookie")) {
                		for (String v : hs) {
                		    v = v.trim();
                		    if (v.length() > 0) {
                		        String[] ss = v.split(";");
                		        for (int i = 0; i < ss.length; i++) {
                		            String tmp = ss[i].trim();
                		            if (tmp.equalsIgnoreCase("HttpOnly")) {
                		                ss[i] = "";
                		            } else if (tmp.contains("path=") && cookiePath != null) {
                		                ss[i] = cookiePath;
                		            } else {
                		                ss[i] = tmp;
                		            }
                		        }
                		        v = "";
                		        for (String s : ss) {
                		            if (s.length() > 0) {
                		                v = String.format("%s%s", (v.length() > 0 ? v + ";" : ""), s);
                		            }
                		        }
                		        resp.addHeader(name, v);
                		    }
                		}
                	} else {
                	    value = Arrays.toString(hs.toArray());
                        value = value.substring(1, value.length()-1);
                        
                        resp.setHeader(name, value);
                	}
                }
            }
            
            OutputStream os = resp.getOutputStream();
            is = httpConn.getInputStream();
            data = new byte[1024];
            
            len = 0;
            while ((len = is.read(data)) != -1) {
            	os.write(data, 0, len);
            }
            is.close();
            os.flush();
            os.close();
            
            httpConn.disconnect();
        }
        
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doGet(req, resp);
        }
    }
    
    private static void logAcc(Env env, String method) {
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
        sb.append(method);
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
    }
}
