package module.SDK.stat;

import monitor.service.http.protocol.RetStat;

public class HostRetStat extends RetStat {
	
	/** IP 地址不合法 */
    public static final String ERR_IP_NOT_VAILDITY = "ERR_IP_NOT_VAILDITY";
    
    /** 主机 ID 不存在 */
    public static final String ERR_HOST_ID_NOT_FOUND = "ERR_HOST_ID_NOT_FOUND";
    
    /** 要添加的 IP 已存在 */
    public static final String ERR_IP_EXISTED = "ERR_IP_EXISTED";
    
    /** 主机名已存在 */
    public static final String ERR_HOST_NAME_EXISTED = "ERR_HOST_NAME_EXISTED";

}
