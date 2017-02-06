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
	
    public GroupInfo(String name, String memo, Timestamp ctime, Timestamp mtime) {
        this.name = name;
        this.memo = memo;
        this.ctime = ctime;
        this.mtime = mtime;
    }
    
    public GroupInfo(Long group_id, String name, String memo, Timestamp ctime, Timestamp mtime) {
        this.group_id = group_id;
        this.name = name;
        this.memo = memo;
        this.ctime = ctime;
        this.mtime = mtime;
    }
}
