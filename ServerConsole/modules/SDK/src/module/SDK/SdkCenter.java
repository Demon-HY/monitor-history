package module.SDK;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import module.SDK.event.EventType;
import module.SDK.event.IListener;
import module.SDK.inner.IActionApi;
import module.SDK.inner.IAuthApi;
import module.SDK.inner.IBeans;
import module.SDK.inner.IEventHub;
import module.SDK.inner.IGroupApi;
import module.SDK.inner.IHostApi;
import module.SDK.inner.IMaintainApi;
import module.SDK.inner.IServiceApi;
import module.SDK.inner.ITemplateApi;
import module.SDK.inner.ITriggerApi;
import module.SDK.inner.IUserApi;
import monitor.exception.LogicalException;
import monitor.service.http.ApiGateway;
import monitor.service.http.HttpServer;
import monitor.service.log.Logger;
import monitor.utils.StringUtils;
    
public class SdkCenter {
    private static SdkCenter s_instance;
    public static SdkCenter getInst() {
        if (null == s_instance) {
        	s_instance = new SdkCenter();
        }
        return s_instance;
    }

    private EventHub mEventHub;
    public SdkCenter() {
        try {
            mEventHub = new EventHub();
            Sdk.sdk.mBeans = new Beans();
            Sdk.sdk.mInterfacePool = new HashMap<String, Object>();
            Sdk.sdk.mOuterInterface = new HashSet<String>();
            this.addInterface(IEventHub.name, mEventHub);
            addOuterInterface();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    /**
     * 添加对外的接口
     */
    private static void addOuterInterface() {
        Sdk sdk = Sdk.sdk;
        sdk.mOuterInterface.add(IEventHub.name);
    }

    public static String ToString() {   // 此方法不许修改，如需重构请申请
        return "monitor1.0";
    }

    /**
     * 获取模块的抽象接口类实例
     * 
     * @param interfaceName 要获取的接口名(IUserApi.name,...)
     * @param securityKey 秘钥，内部获取接口的秘钥是一个写死的字符串，以后可能会做成动态的，
     * 		第三方插件的秘钥目前还没设置，准备以后加入License的时候，从License里面获取
     * @return
     * @throws LogicalException
     */
	public Object queryInterface(String interfaceName, String securityKey) throws LogicalException {
		if (null == interfaceName || 0 == interfaceName.length()) {
			throw new LogicalException("INAVLIDATE INTERFACE NAME", "SdkCenter queryInterface error"); 
		}

		String innerKey = SecurityKey.innerKey;
//		String outerKey = SecurityKey.outerKey;
		
//    		if (false == innerKey.equals(securityKey))
//                throw new LogicalException("NOT SUPPORTED INTERFACE", interfaceName);

//		if (false == innerKey.equals(securityKey) && false == outerKey.equals(securityKey))
//		    throw new LogicalException("NOT SUPPORTED INTERFACE", interfaceName);
//
//		if (true == outerKey.equals(securityKey) && false == Sdk.sdk.mOuterInterface.contains(interfaceName))
//		    throw new LogicalException("NOT SUPPORTED INTERFACE", interfaceName);
//
		if (false == innerKey.equals(securityKey))
		    throw new LogicalException("NOT SUPPORTED INTERFACE", interfaceName);

		if (IBeans.name.equals(interfaceName)) {
			return Sdk.sdk.mBeans;
		}
		
		if (true == Sdk.sdk.mInterfacePool.containsKey(interfaceName))
			return Sdk.sdk.mInterfacePool.get(interfaceName);

		throw new LogicalException("NOT SUPPORTED INTERFACE", interfaceName);
	}

	/**
	 * 注册接口
	 * 
	 * @param interfaceName 接口名字(格式：接口类名.name)
	 * @param interfaceObject 接口对象实例
	 * @throws LogicalException
	 */
	public void addInterface(String interfaceName, Object interfaceObject) throws LogicalException {
		if (null == interfaceName || 0 == interfaceName.length()) {
			throw new LogicalException("INAVLIDATE INTERFACE NAME", "SdkCenter addInterface error"); 
		}
		if (null == interfaceObject) {
	        System.err.println(interfaceName);
			throw new LogicalException("NULL INSTANCE OBJECT", "SdkCenter addInterface error"); 
		}
		if (true == Sdk.sdk.mInterfacePool.containsKey(interfaceName)) {
	        System.err.println(interfaceName + " " + interfaceObject.getClass().getName());
			throw new LogicalException("INSTANCE OBJECT EXIST", "SdkCenter addInterface error");
		}
		Sdk.sdk.mInterfacePool.put(interfaceName, interfaceObject);
	}

	/**
	 * 注册监听
	 * 
	 * @param eventType 事件类型
	 * @param listener 捕捉事件后调用的实例
	 */
	public void registListener(EventType eventType, IListener listener) {
		mEventHub.registListener(eventType, listener);
	}
	
	/**
	 * 注册 HTTP API 接口
	 * 
	 * @param moduleName 模块名
	 * @param apiObject HTTP API 接口实例
	 * @throws Exception
	 */
	public void registHttpApi(String moduleName, Object apiObject) throws Exception {
		if (null != moduleName && moduleName.length() > 0 && null != apiObject)
			HttpServer.getInst(moduleName).registApiService("api", new ApiGateway(apiObject, moduleName, Logger.getInst(moduleName)));
	}
}

/**
 * 内部获取对象实例的方法
 * 
 * @author monitor
 */
class Beans implements IBeans {   
	private IEventHub eventHub = null;
    public IEventHub getEventHub() throws LogicalException {
        if (null == this.eventHub) {
            this.eventHub = (IEventHub) SdkCenter.getInst().queryInterface(IEventHub.name, SecurityKey.innerKey);
        }
        return this.eventHub;
    }
    
    private IUserApi userApi = null;
    public IUserApi getUserApi() throws LogicalException {
        if (null == this.userApi) {
            this.userApi = (IUserApi) SdkCenter.getInst().queryInterface(IUserApi.name, SecurityKey.innerKey);
        }
        return this.userApi;
    }
    
    private IAuthApi authApi = null;
    public IAuthApi getAuthApi() throws LogicalException {
    	if (null == this.authApi) {
    		this.authApi = (IAuthApi) SdkCenter.getInst().queryInterface(IAuthApi.name, SecurityKey.innerKey);
    	}
    	return this.authApi;
    }
    // monitor module
    private IActionApi actionApi = null;
    public IActionApi getActionApi() throws LogicalException {
    	if (null == this.actionApi) {
    		this.actionApi = (IActionApi) SdkCenter.getInst().queryInterface(IActionApi.name, SecurityKey.innerKey);
    	}
    	return this.actionApi;
    }
    
    private IGroupApi groupApi = null;
    public IGroupApi getGroupApi() throws LogicalException {
    	if (null == this.groupApi) {
    		this.groupApi = (IGroupApi) SdkCenter.getInst().queryInterface(IGroupApi.name, SecurityKey.innerKey);
    	}
    	return this.groupApi;
    }
    
    private IHostApi hostApi = null;
    public IHostApi getHostApi() throws LogicalException {
    	if (null == this.hostApi) {
    		this.hostApi = (IHostApi) SdkCenter.getInst().queryInterface(IHostApi.name, SecurityKey.innerKey);
    	}
    	return this.hostApi;
    }
    
    private IMaintainApi maintainApi = null;
    public IMaintainApi getMaintainApi() throws LogicalException {
    	if (null == this.maintainApi) {
    		this.maintainApi = (IMaintainApi) SdkCenter.getInst().queryInterface(IMaintainApi.name, SecurityKey.innerKey);
    	}
    	return this.maintainApi;
    }
    
    private IServiceApi serviceApi = null;
    public IServiceApi getServiceApi() throws LogicalException {
    	if (null == this.serviceApi) {
    		this.serviceApi = (IServiceApi) SdkCenter.getInst().queryInterface(IServiceApi.name, SecurityKey.innerKey);
    	}
    	return this.serviceApi;
    }
    
    private ITemplateApi templateApi = null;
    public ITemplateApi getTemplateApi() throws LogicalException {
    	if (null == this.templateApi) {
    		this.templateApi = (ITemplateApi) SdkCenter.getInst().queryInterface(ITemplateApi.name, SecurityKey.innerKey);
    	}
    	return this.templateApi;
    }
    
    private ITriggerApi triggerApi = null;
    public ITriggerApi getTriggerApi() throws LogicalException {
    	if (null == this.triggerApi) {
    		this.triggerApi = (ITriggerApi) SdkCenter.getInst().queryInterface(ITriggerApi.name, SecurityKey.innerKey);
    	}
    	return this.triggerApi;
    }
}

class Sdk {
    public static Sdk sdk = new Sdk();

    /** 内部接口池，提供给内部调用的对象实例 */
    public HashMap<String, Object> mInterfacePool;
    /** 外部接口池，提供给第三方调用的对象实例 */
    public HashSet<String> mOuterInterface;
    /** 
     * 接口句柄，内部模块调用其他模块都要用该句柄调用，
     * </br>禁止使用Eclipse的工程关联的方式调用 
     */
    public IBeans mBeans;
}

/**
 * 秘钥生成类
 * 
 * @author monitor
 */
class SecurityKey {
    public final static String innerKey = "monitor1.0InnerKeyP@ssw0rd";
//    public final static String outerKey = _outer();
//
//    private static String _outer() {
//        String license = Config.get("module.license");
//        return LicenseUtil.genOuterSecurityKey(LicenseUtil.parseLicense(license).get(LicenseUtil.s_company).toString());
//    }
}

/**
 * License 解析类，目前还没用到，准备再项目后期完成
 * 
 * @author monitor
 */
class LicenseUtil {
    //此类不要轻易修改
	public static final String s_endDate = "endDate";
	public static final String s_quotaLimit = "quotaLimit";
	public static final String s_userLimit = "userLimit";
	public static final String s_company = "company";
	public static final String s_hardware = "hardware";
	
	/**
	 * 解析 License
	 * 
	 * @param license
	 * @return
	 */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseLicense(String license) {
        List<Object> list = (List<Object>) JSONObject.parse(StringUtils.getString(license));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(s_endDate, list.get(0));
        map.put(s_quotaLimit, list.get(1));
        map.put(s_userLimit, list.get(2));
        map.put(s_company, list.get(3));
        map.put(s_hardware, list.get(4));
        list.clear();

        return map;
    }

    /**
     * 获取第三方插件调用其他模块接口时需要用到的秘钥
     * 
     * @param company 公司名
     * @return
     */
    public static String genOuterSecurityKey(String company) {
        String key = StringUtils.setString(company.hashCode() + company);
        return _subString(key, 4, 10);
    }
    
    private static String _subString(String str, int begin, int len) {
    	int end = begin + len;
    	if (end >= str.length())
    		end = str.length() - 1;
    	return str.substring(begin, end);
    }
}

