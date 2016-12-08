package module.initdata;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import monitor.exception.LogicalException;
import monitor.service.http.Env;
import monitor.utils.XProperties;
import module.SDK.info.UserInfo;
import module.SDK.inner.IBeans;

/**
 * 初始化用户
 * 		a. 根据配置文件中指定的参数，创建用户，默认是一个超级管理员(admin)和普通用户(test)
 * 
 * @author monitor
 */
public class InitUser {

	public static void initUser(Env env, XProperties properties, IBeans beans) 
			throws SQLException, LogicalException, NoSuchAlgorithmException {
        String _userNames = properties.getProperty("init.user");
        String[] names = _userNames.split(",");
        for (String name : names) {
            if (name.trim().length() == 0) {
                continue;
            }
            String password = properties.getProperty(String.format("init.user.%s.password", name));
            String attrStr = properties.getProperty(String.format("init.user.%s.attrs", name));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> attrs = (Map<String, Object>) JSONObject.parse(attrStr);
            
            Long uid = beans.getAuthApi().getAuthModel().checkLoginId("name", name);
            UserInfo user = new UserInfo();
            user.name = name;
            user.password = password;
            user.nick = (String) attrs.get("nick");
            user.email = (String) attrs.get("email");
            user.status = (int) attrs.get("status");
            user.type = (int) attrs.get("type");
            if (null == uid) {
                user = beans.getUserApi().userRegister(env, user);
            } else {
                Object status = attrs.get("status");
                if (status != null) {
                    beans.getUserApi().setUserAttr(env, uid, "status", status);
                }
            }
        }
    }
}
