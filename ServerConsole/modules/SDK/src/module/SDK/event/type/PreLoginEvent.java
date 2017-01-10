package module.SDK.event.type;

import module.SDK.event.Event;
import module.SDK.event.EventType;
import module.SDK.info.LoginInfo;
import monitor.exception.LogicalException;
import monitor.service.http.Env;

/**
 * 登录前事件</br>
 * </br>
 * 事件监听方可以截获此事件，并设置 loginInfo，可以绕过正常登录逻辑，正接向客户端返回 token。
 */
public class PreLoginEvent extends Event {
    
    public static enum Type implements EventType {
    	/**
    	 * 事件类型标识：登录前
    	 */
        PRE_LOGIN,
        PRE_PHONE_LOGIN
    }
    
    public LoginInfo loginInfo;
    public LogicalException logicalException;
    
    public Env env;
    public String account;
    public String password;
    public String type;
    public Long tokenAge;
    public String phone;
    public String code;
    /**
     * 事件构造函数
     * 
     * @param env
     * @param account 账号
     * @param password 密码
     * @param type 账号类型
     * @param tokenAge token寿命
     * @param loginInfo
     */
    public PreLoginEvent(Env env, String account, String password, 
        String type, Long tokenAge) {
        this.env = env;
        this.account = account;
        this.password = password;
        this.type = type;
        this.tokenAge = tokenAge;
    }
    
    /**
     * 手机登陆的方式的事件构造函数
     * @param env
     * @param phone手机账号
     * @param code 验证码
     * @param tokenAge token过期时间
     */
    public PreLoginEvent(Env env, String phone, String code, Long tokenAge) {
        this.env = env;
        this.phone = phone;
        this.code = code;
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
