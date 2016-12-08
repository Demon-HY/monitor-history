package module.SDK.info;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import monitor.utils.Time;

public class TokenInfo {
    
    /** 用户登录凭据 */
    public String token;
    /** 用户ID */
	public long uid;
	/** token的有效期限 */
	public Timestamp expires;
	/** 创建时间 */
	public Timestamp ctime;
	/** 客户端IP */
	public String ip;
	/** 客户端设备 */
	public String device;
	
	public TokenInfo(String token, long uid, Timestamp expires, Timestamp ctime, String ip) {
		this.token = token;
		this.uid = uid;
		this.expires = expires;
		this.ctime = ctime;
		this.ip = ip;
	}
	
	public TokenInfo(String token, long uid, Timestamp expires, Timestamp ctime, String ip, String device) {
        this.token = token;
        this.uid = uid;
        this.expires = expires;
        this.ctime = ctime;
        this.ip = ip;
        this.device = device;
    }
	
	/**
	 * 新建token信息
	 * @param uid 用户id
	 * @param age token寿命
	 * @param ip 用户的ip地址
	 * @return TokenInfo
	 */
	public static TokenInfo newToken(long uid, long age, String ip) {
		long now = Time.currentTimeMillis();
		return new TokenInfo(makeToken(), uid, new Timestamp(now + age), new Timestamp(now), ip);
	}
	/**
	 * 新建token信息
	 * @param uid 用户id
	 * @param age token寿命
	 * @param ip 用户的ip地址
	 * @param device 用户登录设备
	 * @param userAgent 用户访问使用的代理
	 * @return TokenInfo
	 */
	public static TokenInfo newToken(long uid, long age, String ip, String device) {
	    long now = Time.currentTimeMillis();
        return new TokenInfo(makeToken() + "@" + uid, uid, new Timestamp(now + age), new Timestamp(now), ip, device);
    }
	
	/**
	 * 生成token字符串
	 * @return token
	 */
	private static String makeToken() {
		long uuid = UUID.randomUUID().getMostSignificantBits();
		byte[] uuidBytes = ByteBuffer.allocate(8).putLong(uuid).array();
		return Base64.encodeBase64URLSafeString(uuidBytes);		
	}
}