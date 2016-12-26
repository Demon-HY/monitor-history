package module.SDK.info;

import java.sql.Timestamp;

/**
 * 群组信息
 * 
 * @author Demon
 */
public class GroupInfo {
	public GroupInfo(){}
	
	/** 群组 ID */
	public Long group_id;
	
	/** 群组名 */
	public String name;
	
	/** 备注 */
	public String memo;
	
	/** 创建时间 */
	public Timestamp ctime;
	
	/** 修改时间 */
	public Timestamp mtime;
}
