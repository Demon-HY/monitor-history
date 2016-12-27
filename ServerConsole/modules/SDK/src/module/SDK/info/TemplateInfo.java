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
}
