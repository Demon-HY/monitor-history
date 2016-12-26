package module.SDK.info;

import java.sql.Timestamp;

/**
 * 维护信息</br>
 * 注意：维护的时候可以让它不报警
 * 
 * @author Demon
 */
public class MaintainInfo {
	public MaintainInfo(){}
	
	/** 维护 ID */
    public Long maintain_id;
    
    /** 维护名 */
    public String name;
    
    /** 维护内容 */
    public String content;
    
    /** 维护开始时间 */
    public Timestamp start_time;
    
    /** 维护结束时间 */
    public Timestamp end_time;
}
