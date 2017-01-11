package module.SDK.inner;

import java.sql.SQLException;
import java.util.List;

import org.javatuples.Pair;

import module.SDK.info.HostInfo;
import monitor.exception.LogicalException;
import monitor.service.http.Env;


public interface IHostApi {
	public static final String name = "IHostApi";
	
	/**
	 * 添加主机
	 * 
	 * @param env
	 * <blockquote>
     * 		类型：对象<br/>
     * 		描述：上下文对象<br/>
     * </blockquote>
	 * @param hostInfo 
	 * <blockquote>
     * 		类型：对象<br/>
     * 		描述：主机信息<br/>
     * </blockquote>
     * @param groupIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：群组 ID 集合<br/>
     * </blockquote>
     * @param templateIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：模板 ID 集合<br/>
     * </blockquote>
     * 
     * @return HostInfo
     * 
	 * @throws SQLException
	 * @throws LogicalException 
	 */
	public HostInfo addHost(Env env, HostInfo hostInfo, List<Long> groupIdList, List<Long> templateIdList) 
			throws SQLException, LogicalException;
	
	/**
     * 编辑主机
     * 
     * @param env
     * <blockquote>
     *      类型：对象<br/>
     *      描述：上下文对象<br/>
     * </blockquote>
     * @param hostInfo 
     * <blockquote>
     *      类型：对象<br/>
     *      描述：主机信息<br/>
     * </blockquote>
     * @param groupIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：群组 ID 集合<br/>
     * </blockquote>
     * @param templateIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：模板 ID 集合<br/>
     * </blockquote>
     * 
     * @return HostInfo
     * 
     * @throws SQLException
     * @throws LogicalException 
     */
    public HostInfo editHost(Env env, HostInfo hostInfo, List<Long> groupIdList, List<Long> templateIdList) 
            throws SQLException, LogicalException;
    
    /**
     * 删除主机
     * 
     * @param env
     * <blockquote>
     *      类型：对象<br/>
     *      描述：上下文对象
     * </blockquote>
     * @param host_id
     * <blockquote>
     *      类型：整数<br/>
     *      描述：主机 ID
     * </blockquote>
     * 
     * @return
     * @throws LogicalException 
     * @throws SQLException 
     */
	public boolean deleteHost(Env env, Long host_id) throws LogicalException, SQLException;
	
	/**
	 * 获取主机列表
	 * @param pageIndex 分页页码
	 * @param pageSize 分页大小
	 * @param order 排序参数（desc、asc）
	 * @param sort 排序字段
	 * @return
	 * @throws SQLException 
	 */
	public Pair<Integer, List<HostInfo>> listHost(Integer pageIndex, Integer pageSize, String order, String sort) 
			throws SQLException;
}
