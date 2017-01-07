package module.SDK.info;

import java.sql.Timestamp;

/**
 * 主机信息
 * 
 * @author Demon
 */
public class HostInfo {
	public HostInfo(){}
	
	/** 主机 ID */
	public Long host_id;
	
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
	 * 主机状态
	 * Online(在线),Down(关闭),Unreachable(拒绝访问),Offline(离线),Problem
	 */
	public String status;
	
	/** 主机存活状态检测间隔(second)  */
	public Integer interval;
	
	/** 备注 */
	public String memo;
	
	/** 创建时间 */
	public Timestamp ctime;
	
	/** 修改时间 */
	public Timestamp mtime;

	public HostInfo(Long host_id, String name, String ip, String monitored, String status, Integer interval, 
			String memo, Timestamp ctime, Timestamp mtime) {
	    this.host_id = host_id;
		this.name = name;
		this.ip = ip;
		this.monitored = monitored;
		this.status = status;
		this.interval = interval;
		this.memo = memo;
		this.ctime = ctime;
		this.mtime = mtime;
	}
}
