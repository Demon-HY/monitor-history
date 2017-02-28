package module.SDK.info;

/**
 * 服务指标
 * 
 * @author Demon
 */
public class IndexInfo {
	public IndexInfo(){}

	/** 服务指标 ID */
    public Long index_id;
    
    /** 服务指标名  */
    public String name;
    
    /** 服务指标具体指标名  */
    public String key;
    
    /** 指标数据类型  */
    public String type;
    
    /** 备注 */
    public String memo;
    
    public IndexInfo(String name, String key, String type, String memo) {
    	this.name = name;
    	this.key = key;
    	this.type = type;
    	this.memo = memo;
    }
    
    public IndexInfo(Long index_id, String name, String key, String type, String memo) {
    	this.index_id = index_id;
    	this.name = name;
    	this.key = key;
    	this.type = type;
    	this.memo = memo;
    }
}