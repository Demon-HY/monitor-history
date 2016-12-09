package monitor.service.http.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

public class JsonResp {
    public String stat;
    public String errMsg;
    public Map<String, Object> resultMap;
    public List<Cookie> cookies;

    public JsonResp() {
        this("");
    }

    public JsonResp(String stat) {
        this.stat = stat;
        this.resultMap = new HashMap<String, Object>();
    }
    
    public JsonResp(String stat, String errMsg) {
        this(stat);
        this.errMsg = errMsg;
    }

    public void addCookie(Cookie cookie) {
        if (null == cookie) {
            return;
        }
        if (null == cookies) {
            cookies = new ArrayList<Cookie>();
        }
        cookies.add(cookie);
    }
}
