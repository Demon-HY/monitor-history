package module.SDK.info;

import java.util.Map;

public class HostInfo {
	
	/** 主机名 */
	public String name;

	/** IP 地址 */
	public String ip;
	
	/** 
	 * 监控方式
	 * Agent,SNMP,WGET 
	 */
	public String monitored;
	
	/** 
	 * 状态
	 * Online,Down,Unreachable,Offline,Problem
	 */
	public String status;
	
	/** 主机存活状态检测间隔  */
	public int host_alive_check_interval;
	
	/** 备注 */
	public String memo;
	
	/** 扩展属性集合 */
	public Map<String, Object> exattr;
}
