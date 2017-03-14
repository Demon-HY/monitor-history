package monitor.service.http.protocol;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import monitor.exception.LogicalException;
import monitor.exception.ReadPostException;
import monitor.service.http.Env;
import monitor.utils.ServletUtil;

public class JsonProtocol implements Protocol {

    public static final int PARSE_JSON = 0x00000001;
    public static final int PARSE_HTTP_PARAM = 0x00000002;
    public static final int PARSE_COOKIE = 0x00000004;
    public static final int BIN_RESPONSE = 0x00000008;
    /**
     * 跳过 token 验证
     */
     public static final int ALLOW_ANONYMOUS = 0x00000010;

    public static final int DEFAULT_OPT = PARSE_HTTP_PARAM | PARSE_COOKIE | PARSE_JSON;
    public static final int BIN_OPTION = JsonProtocol.PARSE_COOKIE 
            | JsonProtocol.PARSE_HTTP_PARAM | JsonProtocol.PARSE_JSON | JsonProtocol.BIN_RESPONSE;
    /* ------------------------------------------------------------ */

    protected int option;
    protected long maxRequestBodyLength;

    public JsonProtocol() {
        this.option = DEFAULT_OPT;
        this.maxRequestBodyLength = 1024 * 1024; // 1MB
    }

    @Override
    public void process(Env env, Object apiSuite, Method m) throws Exception {
        JsonReq req = new JsonReq(env);

        // Parse query string
        parseQueryString(env, req);

        // Parse cookie
        parseCookie(env, req);

        // Parse request body as JSON to Java Map
        if (!parseBody(env, req)) {
            return;
        }

        // Invoke
        JsonResp resp = invoke(env, req, apiSuite, m);
        
        if (resp == null) {
            return;
        }
        
        // Serialize result to JSON
        sendJsonResponse(env, resp.stat, null, resp.resultMap, resp.errMsg, resp.cookies);
    }
    
    protected void parseQueryString(Env env, JsonReq req) {
        if ((this.option & PARSE_HTTP_PARAM) != 0) {
            
            Map<String, String> queryStringParams = ServletUtil
                    .decodeQueryString(env.request.getQueryString());
            if (queryStringParams != null) {
                req.params.putAll(queryStringParams);
            }
        }
    }
    
    protected void parseCookie(Env env, JsonReq req) {
        if ((this.option & PARSE_COOKIE) != 0) {
            
            Cookie[] cookies = env.request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    req.params.put(cookie.getName(), cookie.getValue());
                }
            }
        }
    }
    
    protected boolean parseBody(Env env, JsonReq req) throws Exception {
        if ((this.option & PARSE_JSON) != 0) {
            
            Map<String, Object> params = null;
            // Read HTTP request body
            byte[] body;
            try {
                body = ServletUtil.readPostData(env.request,
                        maxRequestBodyLength);
            } catch (IOException | ReadPostException e) {
                env.getLogger().err("Read post data exception", e);
                sendJsonResponse(env, RetStat.ERR_READ_POST_EXCEPTION, 
                        ErrTextFormatter.getErrText(env, null, RetStat.ERR_READ_POST_EXCEPTION), null);
                return false;
            }
            params = parseJsonBody(body, env);

            if (params == null) {
                sendJsonResponse(env, RetStat.ERR_INVALID_JSON, 
                        ErrTextFormatter.getErrText(env, null, RetStat.ERR_INVALID_JSON), null);
                return false;
            }

            if (req.params.size() == 0) {
                req.params = params;
            } else {
                req.params.putAll(params);
            }
        }
        return true;
    }
    
    protected JsonResp invoke(Env env, JsonReq req, Object apiSuite, Method m) throws Exception {
        JsonResp resp;
        try {
            // Invoke API
            resp = (JsonResp) m.invoke(apiSuite, req);
            
            if (null != resp && resp.stat.length() == 0) {
                env.getLogger().err("stat not set");
                resp.stat = RetStat.ERR_SERVER_EXCEPTION;
            }
            return resp;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            
            if (cause instanceof LogicalException) {
                LogicalException _cause = (LogicalException)cause;
                env.getLogger().err(_cause.stat + "\t" + _cause.errMsg);
                sendJsonResponse(env, _cause.stat, null, _cause.reaultMap, _cause.errMsg, null);
                return null;
            } else {
                env.getLogger().err("Process API exception", cause);
                sendJsonResponse(env, RetStat.ERR_SERVER_EXCEPTION, 
                        ErrTextFormatter.getErrText(env, null, RetStat.ERR_SERVER_EXCEPTION), null);
                return null;
            }
        } catch (Exception e) {
            env.getLogger().err("Process API exception", e);
            sendJsonResponse(env, RetStat.ERR_SERVER_EXCEPTION, 
                    ErrTextFormatter.getErrText(env, null, RetStat.ERR_SERVER_EXCEPTION), null);
            return null;
        }
    }
    
    protected Map<String, Object> parseJsonBody(byte[] body, Env env) {
        if (body.length == 0) {
            return new HashMap<String, Object>();
        }

        try {
            String bodyText = new String(body, "UTF-8");
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>)JSONObject.parse(bodyText);
            return params;
        } catch (Exception e) {
            env.errMsg = "Parse json exception";
            env.getLogger().err(env.errMsg, e);
            return null;
        }
    }

    public void sendJsonResponse(Env env, String stat, String errText, 
            Map<String, Object> resultMap, String errMsg, List<Cookie> cookies)
            throws Exception {
        env.stat = stat;
        
        if (env.response.isCommitted()) {
            env.getLogger().err("Response already been output some where, check your code please.");
            return;
        }
        
        env.response.addHeader("X-Cost", Integer.toString((int) (System
                .currentTimeMillis() - env.reqStartTm)));

        if (resultMap == null) {
            resultMap = new HashMap<String, Object>();
        }
        resultMap.put("stat", stat);

        if (errMsg != null) {
            env.errMsg = errMsg;
        }
        
        if (null == errText) {
            errText = ErrTextFormatter.getErrText(env, env.moduleName, stat);
        }
        resultMap.put("errText", errText);
        
        resultMap.putAll(env.extResultMap);
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                env.response.addCookie(cookie);
            }
        }

        // Serialize to JSON
        String jsonText  = JSONObject.toJSONString(resultMap);

        /*
         * 对于文件下载的接口，出错时不能返回 200 ，需要返回别的 HTTP 错误码。
         */
        int httpStatus = HttpServletResponse.SC_OK;
        if ((this.option & BIN_RESPONSE) != 0) {
            switch (stat) {
            case RetStat.OK:
                httpStatus = HttpServletResponse.SC_OK;
                break;
            case RetStat.ERR_BAD_PARAMS:
                httpStatus = HttpServletResponse.SC_BAD_REQUEST;
                break;
            case RetStat.ERR_FORBIDDEN:
                httpStatus = HttpServletResponse.SC_FORBIDDEN;
                break;
            case RetStat.ERR_NOT_FOUND:
                httpStatus = HttpServletResponse.SC_NOT_FOUND;
                break;
            default:
                httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        }
        
        ServletUtil.sendHttpResponse(env.response, httpStatus, jsonText);
    }

    public void sendJsonResponse(Env env, String stat, String errText, 
            Map<String, Object> resultMap) throws Exception {
        sendJsonResponse(env, stat, errText, resultMap, null, null);
    }

    @Override
    public void setOption(int option) {
        this.option = option;
    }

    public void setStrOption(String strOption) {}

    @Override
    public boolean isValidMethod(Method m) {
        Class<?>[] params = m.getParameterTypes();
        if (!(params.length == 1 && params[0].equals(JsonReq.class))) {
            return false;
        }

        if (m.getReturnType() == JsonResp.class) {
            return true;
        }
        return false;
    }
}
