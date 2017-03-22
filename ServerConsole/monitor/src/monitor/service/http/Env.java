package monitor.service.http;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import monitor.exception.UnInitilized;
import monitor.service.log.Logger;
import monitor.utils.LanguageUtil;

public class Env {
	
    /**
     * HTTP 请求所对应的模块名称
     */
	public String moduleName;
	
	/**
	 * Serrvlet HTTP 请求对象
	 */
	public HttpServletRequest request;
	
	/**
	 * Servlet HTTP 返回对象
	 */
	public HttpServletResponse response;
	
	/**
	 * 客户端 IP
	 */
	public String ip;
	
	/**
	 * 客户端 MAC地址
	 */
	public String mac;
	
	/**
	 * 客户端设备类型 (有可能为 NULL)
	 */
	public String device;
	
	/**
	 * 全局唯一的请求 ID, 将会被写入访问日志和错误日志
	 */
	public String reqId;
	
	/**
	 * 用于存放插件自定义数据的 Map 对象
	 */
	public Map<String, Object> sticker;
	
	/**
     * 要求返回语言的种类
     */
    public String Language;
	
	public long reqStartTm;
	public long timingBeginTm;
	public Map<String, Long> timings;
	public Map<String, Object> logParams;
	public Map<String, Object> extResultMap;
	public Map<String, Object> auditParams;
	public String errMsg;
	public String stat;
	
	public Env(String moduleName) {
        
        this.moduleName = moduleName;
        
        this.reqStartTm = System.currentTimeMillis();
        this.reqId = makeRequestId();
        this.timingBeginTm = this.reqStartTm;
        this.timings = new HashMap<String, Long>();
        this.logParams = new HashMap<String, Object>();
        this.extResultMap = new HashMap<String, Object>();
        this.sticker = new HashMap<String, Object>();
        this.auditParams = new HashMap<String, Object>();
    }
	
	public Env(HttpServletRequest request, HttpServletResponse response, String moduleName) {
	    
		this.moduleName = moduleName;
		this.request = request;
		this.response = response;
		
		String _Language = request.getHeader("X-Language");
        if (_Language == null) {
            _Language = LanguageUtil.defaultLanguage;
        }
        this.Language = _Language;
		
		String _device = request.getHeader("X-Device");
        if (_device == null) {
            _device = "Web";
        }
        String _mac = request.getHeader("X-Mac");
        if(null != _mac && !"".equals(_mac.trim()))
            this.mac = _mac;
        this.device = _device;
		this.ip = request.getRemoteAddr();
		// for nginx proxy
		if (request.getHeader("X-Forwarded-For") != null) {
		    this.ip = request.getHeader("X-Forwarded-For");
		}
		this.reqStartTm = System.currentTimeMillis();
		this.reqId = makeRequestId();
		this.timingBeginTm = this.reqStartTm;
		this.timings = new HashMap<String, Long>();
		this.logParams = new HashMap<String, Object>();
		this.sticker = new HashMap<String, Object>();
		this.extResultMap = new HashMap<String, Object>();
		this.auditParams = new HashMap<String, Object>();
		response.addHeader("X-RequestId", this.reqId);
	}
	
	public void startTimming() {
		this.timingBeginTm = System.currentTimeMillis();
	}
	
	public void logTiming(String operation) {
		long cost = System.currentTimeMillis() - this.timingBeginTm;
		this.timings.put(operation, cost);
	}
	
	public void logParam(String name, Object value) {
		this.logParams.put(name, value);
	}
	
	public void addAudit(String name, Object value) {
		this.auditParams.put(name, value);
	}
	
	private Logger logger;
	public Logger getLogger() {
		try {
		    if(logger == null) {
		        logger = Logger.getInst(String.format("%s-ReqId:%s", this.moduleName, this.reqId));
		    }
		    return logger;
		} catch (UnInitilized e) {
			e.printStackTrace();
			return Logger.getInst();
		}
	}
	
	public static String makeRequestId() {
		long uuid = UUID.randomUUID().getMostSignificantBits();
		byte[] uuidBytes = ByteBuffer.allocate(8).putLong(uuid).array();
		return Base64.encodeBase64URLSafeString(uuidBytes);		
	}

	public boolean isMobileDevice() {
        
	    switch (device) {
	    case "ios" : 
	    case "android" : return true;
	    default : return false;
	    }
        
    }
}