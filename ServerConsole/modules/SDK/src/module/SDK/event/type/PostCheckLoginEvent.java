package module.SDK.event.type;

import java.util.Map;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.LoginInfo;
import monitor.exception.LogicalException;
import monitor.service.http.Env;

public class PostCheckLoginEvent extends Event{
	 public static enum Type implements EventType {
	    	/**
	    	 * 事件类型标识：登录后
	    	 */
	        POST_CHECK_LOGIN
	    }

	    public LoginInfo loginInfo;
	    public LogicalException logicalException;
	    
	    public Env env;
	    public Map<String, Object> params;
	    public Boolean thirdCheck = false;
	    
	    /**
	     * 事件构造函数
	     */
	    public PostCheckLoginEvent(Env env, Map<String, Object> params, LoginInfo loginInfo) {
	        this.env = env;
	    	this.params = params;
	    	this.loginInfo = loginInfo;
	    }
	    
	    public void returnSuccess(LoginInfo loginInfo) {
	        this.loginInfo = loginInfo;
	        this.logicalException = null;
	    }
	    
	    public void returnFailure(String stat) {
	        this.logicalException = new LogicalException(stat, null);
	        this.loginInfo = null;
	    }
	    
	    public void returnFailure(String stat, String errMsg) {
	        this.logicalException = new LogicalException(stat, errMsg);
	        this.loginInfo = null;
	    }
}
