package module.SDK.event.type;

import java.util.Map;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.UserInfo;
import monitor.service.http.Env;

public class UserEvent extends Event {

	public enum Type implements EventType {
	    /**
         * 事件类型标识：设置用户属性前</br>
         * 有效参数：</br>
         * user</br>
         * attrs
         */
	    PRE_SET_USER_ATTR,
	    
	    /**
         * 事件类型标识：设置用户属性后</br>
         * 有效参数：</br>
         * user</br>
         * setAttrFlag
         */
	    POST_SET_USER_ATTR,
	    
	    /**
	     * 事件类型标识：删除用户前</br>
	     * 有效参数：</br>
         * user
	     */
	    PRE_DELETE_USER,
	    
	    /**
         * 事件类型标识：删除用户后</br>
         * 有效参数：</br>
         * user</br>
         * delAttrFlag
         */
	    POST_DELETE_USER,
	    
	    /**
         * 事件类型标识：注册用户前</br>
         * 有效参数：</br>
         * type</br>
         * account</br>
         * attrs
         */
	    PRE_REGISTER_USER,
	    
	    /**
         * 事件类型标识：注册用户后
         * 有效参数：</br>
         * user</br>
         * type</br>
         * account</br>
         */
	    POST_REGISTER_USER,
	    
        /**
         * 事件类型标识：重新设置密码之前</br>
         * 有效参数</br>
         * user
         */
        PRE_RESET_PSW,
        
        /**
         * 事件类型标识：重新设置密码之后</br>
         * 有效参数</br>
         * user
         */
        POST_RESET_PSW
	}
	
    public Env env;
    public Long uid;
    public String key;
    public Object value;
    public String type;
    public String account;
    public UserInfo user;
    public Map<String, Object> attrs;

    public boolean setAttrFlag;
    public boolean delAttrFlag;
    public boolean delUserFlag;
    public boolean recoverFlag;
    public boolean validateOldPwd = true;
    public boolean resetXsrvPwd = true;

    public String url;
    
    public UserEvent(Env env, UserInfo user, String key) {
        this.env = env;
        this.user = user;
        this.key = key;
        this.uid = user.uid;
    }

    public UserEvent(Env env, UserInfo user, String key, Object value) {
        this.env = env;
        this.user = user;
        this.key = key;
        this.value = value;
        this.uid = user.uid;
    }

    public UserEvent(Env env, UserInfo user) {
        this.env = env;
        this.user = user;
        this.uid = user.uid;
    }

    public UserEvent(Env env, UserInfo user, String type, String account) {
        this.env = env;
        this.user = user;
        this.uid = user.uid;
        this.type = type;
        this.account = account;
    }

    public UserEvent(Env env, String type, String account, Map<String, Object> attrs) {
        this.env = env;
        this.type = type;
        this.account = account;
        this.attrs = attrs;
    }

    public UserEvent(Env env, UserInfo user, Map<String, Object> attrs) {
        this.env = env;
        this.user = user;
        this.attrs = attrs;
        this.uid = user.uid;
    }
    
    public UserEvent(Env env, String url) {
        this.env = env;
        this.url = url;
    }
    
    public void returnFailure(String stat, String errMsg) {
        this.isContinue = false;
        this.stat = stat;
        this.breakReason = errMsg;
        this.stopDispatch = true;
    }
    
}
