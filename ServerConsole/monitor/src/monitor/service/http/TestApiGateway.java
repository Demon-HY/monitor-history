package monitor.service.http;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import monitor.service.log.Logger;
import monitor.utils.ServletUtil;
import monitor.service.http.protocol.JsonProtocol;
import monitor.service.http.protocol.JsonReq;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;

class TestApiGateway {

	@ApiGateway.ApiMethod
	public void hello(Env env) throws IOException {
		/* ------------------------------------------------------------ */
		env.response.setContentType("text/html");
		env.response.setStatus(HttpServletResponse.SC_OK);
		env.response.getWriter().println(env.reqId);
		env.response.getWriter().println("<h1>hello</h1>");
	}
	
	@ApiGateway.ApiMethod(apiName="haha")
	public void hi(Env env) throws IOException {
		/* ------------------------------------------------------------ */
		env.response.setContentType("text/html");
		env.response.setStatus(HttpServletResponse.SC_OK);
		env.response.getWriter().println(env.reqId);
		env.response.getWriter().println("<h1>hi</h1>");
	}
	
	@ApiGateway.ApiMethod(protocol=JsonProtocol.class)
	public JsonResp add(JsonReq req) {
		int a = Integer.parseInt(req.params.get("a").toString());
		int b = Integer.parseInt(req.params.get("b").toString());
		
		int sum = a + b;
		
		JsonResp resp = new JsonResp(RetStat.OK.toString());
		resp.resultMap.put("sum", sum);
		return resp;
	}
	
	@ApiGateway.ApiMethod(protocol=JsonProtocol.class)
	public JsonResp join(JsonReq req) {
		String a = req.params.get("a").toString();
		String b = req.params.get("b").toString();
		req.env.logParam("a", a);
		req.env.logParam("b", b);
		
		req.env.startTimming();
		String sum = a + "," + b;
		req.env.logTiming("concate");
		
		JsonResp resp = new JsonResp(RetStat.OK.toString());
		resp.resultMap.put("sum", sum);
		return resp;
	}
	
	@ApiGateway.ApiMethod
	public void download(Env env) throws IOException {
		Map<String, String> params = ServletUtil.decodeQueryString(env.request.getQueryString());
		if (params == null) {
			ServletUtil.sendHttpResponse(env.response, HttpServletResponse.SC_BAD_REQUEST, "");
			return;
		}
		
		@SuppressWarnings("unused")
        String fileName = params.get("name");
		// TODO Do something
		
		int contentLength = 100;
		env.response.setContentLength(contentLength);
		env.response.setContentType("application/octet-stream");
		ServletOutputStream ostream = env.response.getOutputStream();
		for (int i = 0; i < contentLength; i++) {
			ostream.write((new String("a")).getBytes());
		}
	}
	
	@ApiGateway.ApiMethod(protocol=JsonProtocol.class, option=JsonProtocol.PARSE_COOKIE | JsonProtocol.PARSE_HTTP_PARAM)
	public JsonResp upload(JsonReq req) throws IOException {
		
		String filename = (String)req.params.get("name");
		
		@SuppressWarnings("unused")
        byte[] body = null;
		try {
			body = ServletUtil.readPostData(req.env.request, 1024 * 1024 *100);
		} catch (Exception e) {
			req.env.getLogger().err("Read post data exception", e);
			return new JsonResp(RetStat.ERR_BAD_PARAMS.toString());
		}
		
		// TODO Do something
		
		JsonResp resp = new JsonResp(RetStat.OK.toString());
		resp.resultMap.put("name", filename);
		return resp;
	}
	
	public static void main(String[] args) throws Exception {
		Logger.init("/tmp/log/", "debug", "false", "-1");
		HttpServer.ServerConfig conf = new HttpServer.ServerConfig();
        conf.host = "0.0.0.0";
        conf.port = 8081;
		HttpServer.init("/data", conf);
		HttpServer server = HttpServer.getInst("apitest");
		server.registApiService("jsonapi", new ApiGateway(new TestApiGateway(), "", Logger.getInst("test")));
		
		HttpServer.startService();
	}
}