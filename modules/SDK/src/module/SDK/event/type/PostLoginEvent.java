package module.SDK.event.type;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.LoginInfo;
import monitor.exception.LogicalException;
import monitor.service.http.Env;

public class PostLoginEvent extends Event {

    public static enum Type implements EventType {
    	/**
    	 * 事件类型标识：登录后
    	 */
        POST_LOGIN,
        POST_PHONE_LOGIN
    }
    
    public Env env;
    public String account;
    public String password;
    public String type;
    public Long tokenAge;
    public String phone;
    
    public LogicalException logicalException;
    public LoginInfo loginInfo;
    
    /**
     * 事件构造函数
     * 
     * @param env
     * @param account 账号
     * @param password 密码
     * @param type 账号类型
     * @param tokenAge token寿命
     * @param e
     * @param loginInfo
     */
    public PostLoginEvent(Env env, String account, String password, 
            String type, Long tokenAge, LogicalException e, LoginInfo loginInfo) {
        this.logicalException = e;
        this.loginInfo = loginInfo;
        this.env = env;
        this.account = account;
        this.password = password;
        this.type = type;
        this.tokenAge = tokenAge;
    }
    
    public PostLoginEvent(Env env, String phone, String type, Long tokenAge,
            LogicalException e, LoginInfo loginInfo) {
        this.logicalException = e;
        this.loginInfo = loginInfo;
        this.env = env;
        this.phone = phone;
        this.type = type;
        this.tokenAge = tokenAge;
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
