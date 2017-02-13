package module.maintain;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.MaintainInfo;
import monitor.exception.ParamException;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.utils.DBUtil;
import monitor.utils.Time;

public class MaintainHttpApi {

	private MaintainApi maintainApi;
	private static MaintainHttpApi maintainHttpApi;
	private MaintainHttpApi(MaintainApi maintainApi) {
		this.maintainApi = maintainApi;
	}
	
	public static void init(MaintainApi maintainApi) {
		maintainHttpApi = new MaintainHttpApi(maintainApi);
	}
	
	public static MaintainHttpApi getInst() {
		if (null == maintainHttpApi) {
			new UnInitilized();
		}
		return maintainHttpApi;
	}
	
	/********************************************     对外接口               ********************************************/
	/**
	 * 获取维护列表
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param pageIndex 
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：分页页码<br/>
     * 		必需：NO
     * </blockquote>
     * @param pageSize 
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：分页大小<br/>
     * 		必需：NO
     * </blockquote>
     * @param order 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：排序规则(desc/asc默认desc)<br/>
     * 		必需：NO
     * </blockquote>
     * @param sort 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：按什么字段排序(默认 maintain_id)<br/>
     * 		必需：NO
     * </blockquote>
     * 
	 * @return
	 * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
	 * total
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：维护任务总数<br/>
     * </blockquote>
     * rows
	 * <blockquote>
     * 		类型：JSON 数组<br/>
     * 		描述：维护信息集<br/>
     * 		maintain_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：维护 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：维护名称
     * 		</blockquote>
     * 		content
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：维护内容
     * 		</blockquote>
     * 		start_time
     * 		<blockquote>
     * 		类型：时间<br/>
     * 		描述：维护开始时间
     * 		</blockquote>
     * 		end_time
     * 		<blockquote>
     * 		类型：时间<br/>
     * 		描述：维护结束时间
     * 		</blockquote>
     * </blockquote>
     * 
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp listMaintain(AuthedJsonReq req) throws Exception {
		Integer pageIndex = req.paramGetInteger("pageIndex", false);
		Integer pageSize = req.paramGetInteger("pageSize", false);
		String order = req.paramGetString("order", false);
		String sort = req.paramGetString("sort", false);
		if (null == order) {
			order = "desc";
		}
		if (null == sort) {
			sort = "maintain_id";
		}
		// 检查 SQL 注入
		if (!DBUtil.checkSqlInjection(sort) || !DBUtil.checkSqlInjection(order)) {
			throw new ParamException(
					String.format("sort(%s) or order(%s) check have sql injection.", sort, order));
		}
		
		Pair<Integer, List<MaintainInfo>> result = this.maintainApi.listMaintain(pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
	
	/**
	 * 获取维护
	 * 
	 * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param name
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：维护名称<br/>
     *      必需：YES
     * </blockquote>
     * @param content
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：维护内容<br/>
     *      必需：YES
     * </blockquote>
     * @param start_time
     * <blockquote>
     *      类型：时间<br/>
     *      描述：维护开始时间<br/>
     *      必需：YES
     * </blockquote>
     * @param end_time
     * <blockquote>
     *      类型：时间<br/>
     *      描述：维护结束时间<br/>
     *      必需：YES
     * </blockquote>
     * @param hostIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：主机ID集合<br/>
     *      必需：NO
     * </blockquote>
     * @param groupIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：群组ID集合<br/>
     *      必需：NO
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * MaintainInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：维护信息<br/>
     *      maintain_id
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID
     *      </blockquote>
     *      name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：维护名称
     *      </blockquote>
     *      content
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：维护内容
     *      </blockquote>
     *      start_time
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：维护开始时间
     *      </blockquote>
     *      end_time
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：维护结束时间
     *      </blockquote>
     * </blockquote>
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp addMaintain(AuthedJsonReq req) throws Exception {
	    String name = req.paramGetString("name", true);
	    String content = req.paramGetString("content", true);
	    Timestamp start_time = Time.getTimestamp(req.paramGetString("start_time", true));
	    Timestamp end_time = Time.getTimestamp(req.paramGetString("end_time", true));
	    List<Long> hostIdList = req.paramGetNumList("hostIdList", true);
	    List<Long> groupIdList = req.paramGetNumList("groupIdList", true);
	    
	    MaintainInfo maintainInfo = new MaintainInfo(name, content, start_time, end_time);
	    maintainInfo = this.maintainApi.addMaintain(req.env, maintainInfo, hostIdList, groupIdList);
	    
	    JsonResp resp = new JsonResp(RetStat.OK);
	    resp.resultMap.put("MaintainInfo", maintainInfo);
        return resp;
	}
	
	/** 编辑维护
     * 
     * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param maintain_id
     * <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID<br/>
     *      必需：YES
     * </blockquote>
     * @param name
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：维护名称<br/>
     *      必需：YES
     * </blockquote>
     * @param content
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：维护内容<br/>
     *      必需：YES
     * </blockquote>
     * @param start_time
     * <blockquote>
     *      类型：时间<br/>
     *      描述：维护开始时间<br/>
     *      必需：YES
     * </blockquote>
     * @param end_time
     * <blockquote>
     *      类型：时间<br/>
     *      描述：维护结束时间<br/>
     *      必需：YES
     * </blockquote>
     * @param hostIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：主机ID集合<br/>
     *      必需：NO
     * </blockquote>
     * @param groupIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：群组ID集合<br/>
     *      必需：NO
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * MaintainInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：维护信息<br/>
     *      maintain_id
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID
     *      </blockquote>
     *      name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：维护名称
     *      </blockquote>
     *      content
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：维护内容
     *      </blockquote>
     *      start_time
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：维护开始时间
     *      </blockquote>
     *      end_time
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：维护结束时间
     *      </blockquote>
     * </blockquote>
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp editMaintain(AuthedJsonReq req) throws Exception {
		Long maintain_id = req.paramGetNumber("maintain_id", true, true);
		String name = req.paramGetString("name", true);
	    String content = req.paramGetString("content", true);
	    Timestamp start_time = Time.getTimestamp(req.paramGetString("start_time", true));
	    Timestamp end_time = Time.getTimestamp(req.paramGetString("end_time", true));
	    List<Long> hostIdList = req.paramGetNumList("hostIdList", true);
	    List<Long> groupIdList = req.paramGetNumList("groupIdList", true);
	    
	    MaintainInfo maintainInfo = new MaintainInfo(maintain_id, name, content, start_time, end_time);
	    maintainInfo = this.maintainApi.editMaintain(req.env, maintainInfo, hostIdList, groupIdList);
	    
	    JsonResp resp = new JsonResp(RetStat.OK);
	    resp.resultMap.put("MaintainInfo", maintainInfo);
        return resp;
	}
	
	/**
	 * 删除维护
	 * 
	 * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param maintain_id
     * <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID<br/>
     *      必需：YES
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * MaintainInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：维护信息<br/>
     *      maintain_id
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID
     *      </blockquote>
     *      name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：维护名称
     *      </blockquote>
     *      content
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：维护内容
     *      </blockquote>
     *      start_time
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：维护开始时间
     *      </blockquote>
     *      end_time
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：维护结束时间
     *      </blockquote>
     * </blockquote>
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp deleteMaintain(AuthedJsonReq req) throws Exception {
		Long maintain_id = req.paramGetNumber("maintain_id", true, true);
		
		MaintainInfo maintainInfo = this.maintainApi.deleteMaintain(req.env, maintain_id);
		
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("MaintainInfo", maintainInfo);
        return resp;
    }
	
	/**
	 * 获取维护关联的主机
	 * 
	 * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param maintain_id
     * <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID<br/>
     *      必需：YES
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * maintain_id
     * <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID
     * </blockquote>
     * hostIdList
     * <blockquote>
     *      类型：数组<br/>
     *      描述：主机 ID 集合<br/>
     * </blockquote>
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp getMaintainHosts(AuthedJsonReq req) throws Exception {
		Long maintain_id = req.paramGetNumber("maintain_id", true, true);
		
		Map<Long, List<Long>> maintainHosts = this.maintainApi.getMaintainHosts(req.env, maintain_id);
		
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("maintain_id", maintain_id);
        resp.resultMap.put("hostIdList", maintainHosts.get(maintain_id));
        return resp;
    }
	
	/**
	 * 获取维护关联的群组
	 * 
	 * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param maintain_id
     * <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID<br/>
     *      必需：YES
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * maintain_id
     * <blockquote>
     *      类型：整数<br/>
     *      描述：维护 ID
     * </blockquote>
     * groupIdList
     * <blockquote>
     *      类型：数组<br/>
     *      描述：群组 ID 集合<br/>
     * </blockquote>
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp getMaintainGroups(AuthedJsonReq req) throws Exception {
		Long maintain_id = req.paramGetNumber("maintain_id", true, true);
		
		Map<Long, List<Long>> maintainGroups = this.maintainApi.getMaintainGroups(req.env, maintain_id);
		
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("maintain_id", maintain_id);
        resp.resultMap.put("groupIdList", maintainGroups.get(maintain_id));
        return resp;
    }
}
