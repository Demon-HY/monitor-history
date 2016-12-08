package module.SDK.http;

import monitor.service.http.protocol.JsonReq;
import module.SDK.info.LoginInfo;
import monitor.service.http.Env;

public class AuthedJsonReq extends JsonReq {
    
    public LoginInfo loginInfo;
    
    public AuthedJsonReq(Env env) {
        super(env);
    }
}
