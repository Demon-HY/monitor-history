package module.SDK.info;

import java.sql.Timestamp;

/**
 * 模板信息
 * 
 * @author Demon
 */
public class TemplateInfo {
	public TemplateInfo(){}
	
	/** 模板 ID */
    public Long template_id;
    
    /** 模板名称 */
    public String name;
    
    /** 创建时间 */
	public Timestamp ctime;
	
	/** 修改时间 */
	public Timestamp mtime;
	
	public TemplateInfo(String name, Timestamp ctime, Timestamp mtime) {
		this.name = name;
		this.ctime = ctime;
		this.mtime = mtime;
	}
	
	public TemplateInfo(Long template_id, String name, Timestamp ctime, Timestamp mtime) {
		this.template_id = template_id;
		this.name = name;
		this.ctime = ctime;
		this.mtime = mtime;
	}
}
