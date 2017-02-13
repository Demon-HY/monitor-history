package module.group;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.GroupInfo;
import monitor.exception.ParamException;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.utils.DBUtil;
import monitor.utils.Time;

public class GroupHttpApi {

	private GroupApi groupApi;
	private static GroupHttpApi groupHttpApi;
	private GroupHttpApi(GroupApi groupApi) {
		this.groupApi = groupApi;
	}
	
	public static void init(GroupApi groupApi) {
		groupHttpApi = new GroupHttpApi(groupApi);
	}
	
	public static GroupHttpApi getInst() {
		if (null == groupHttpApi) {
			new UnInitilized();
		}
		return groupHttpApi;
	}
	
	/********************************************     对外接口               ********************************************/
	/**
	 * 获取群组列表
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
     * 		描述：按什么字段排序(默认 mtime)<br/>
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
     * 		描述：群组总数<br/>
     * </blockquote>
     * rows
	 * <blockquote>
     * 		类型：JSON 数组<br/>
     * 		描述：群组信息集<br/>
     * 		group_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：群组 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：群组名称
     * 		</blockquote>
     * 		memo
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：备注
     * 		</blockquote>
     * 		ctime
     * 		<blockquote>
     * 		类型：时间<br/>
     * 		描述：创建时间
     * 		</blockquote>
     * 		mtime
     * 		<blockquote>
     * 		类型：时间<br/>
     * 		描述：修改时间
     * 		</blockquote>
     * </blockquote>
     * 
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp listGroup(AuthedJsonReq req) throws Exception {
		Integer pageIndex = req.paramGetInteger("pageIndex", false);
		Integer pageSize = req.paramGetInteger("pageSize", false);
		String order = req.paramGetString("order", false);
		String sort = req.paramGetString("sort", false);
		if (null == order) {
			order = "desc";
		}
		if (null == sort) {
			sort = "mtime";
		}
		// 检查 SQL 注入
		if (!DBUtil.checkSqlInjection(sort) || !DBUtil.checkSqlInjection(order)) {
			throw new ParamException(
					String.format("sort(%s) or order(%s) check have sql injection.", sort, order));
		}
		
		Pair<Integer, List<GroupInfo>> result = this.groupApi.listGroup(pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
	
	/**
     * 添加群组
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
     *      描述：群组名字<br/>
     *      必需：YES
     * </blockquote>
     * @param memo 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：备注<br/>
     *      必需：YES
     * </blockquote>
     * @param templateIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：模板ID集合<br/>
     *      必需：NO
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * GroupInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：群组信息<br/>
     *      group_id
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：群组 ID
     *      </blockquote>
     *      name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：群组名称
     *      </blockquote>
     *      memo
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：备注
     *      </blockquote>
     *      ctime
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：创建时间
     *      </blockquote>
     *      mtime
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：修改时间
     *      </blockquote>
     * </blockquote>
     * 
     * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp addGroup(AuthedJsonReq req) throws Exception {
	    String name = req.paramGetString("name", true);
	    String memo = req.paramGetString("memo", true);
	    List<Long> templateIdList = req.paramGetNumList("templateIdList", true);
	    
	    GroupInfo groupInfo = new GroupInfo(name, memo, Time.getTimestamp(), Time.getTimestamp());
	    groupInfo = this.groupApi.addGroup(req.env, groupInfo, templateIdList);
	    
	    JsonResp resp = new JsonResp(RetStat.OK);
	    resp.resultMap.put("GroupInfo", groupInfo);
        return resp;
	}
	
	/**
     * 添加群组
     *
     * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param group_id
     * <blockquote>
     *      类型：整数<br/>
     *      描述：群组 ID<br/>
     *      必需：YES
     * </blockquote>
     * @param name 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：群组名字<br/>
     *      必需：YES
     * </blockquote>
     * @param memo 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：备注<br/>
     *      必需：YES
     * </blockquote>
     * @param templateIdList 
     * <blockquote>
     *      类型：数组<br/>
     *      描述：模板ID集合<br/>
     *      必需：NO
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * GroupInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：群组信息<br/>
     *      group_id
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：群组 ID
     *      </blockquote>
     *      name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：群组名称
     *      </blockquote>
     *      memo
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：备注
     *      </blockquote>
     *      ctime
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：创建时间
     *      </blockquote>
     *      mtime
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：修改时间
     *      </blockquote>
     * </blockquote>
     * 
     * @right 该接口需要管理员权限
     */
    @ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp editGroup(AuthedJsonReq req) throws Exception {
        Long group_id = req.paramGetNumber("group_id", true, true);
        String name = req.paramGetString("name", true);
        String memo = req.paramGetString("memo", true);
        List<Long> templateIdList = req.paramGetNumList("templateIdList", true);
        
        GroupInfo groupInfo = new GroupInfo(group_id, name, memo, Time.getTimestamp(), Time.getTimestamp());
        groupInfo = this.groupApi.editGroup(req.env, groupInfo, templateIdList);
        
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("GroupInfo", groupInfo);
        return resp;
    }
    
    /**
     * 删除群组
     *
     * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param group_id 
     * <blockquote>
     *      类型：整形<br/>
     *      描述：群组 ID<br/>
     *      必需：YES
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * GroupInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：群组信息<br/>
     *      group_id
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：群组 ID
     *      </blockquote>
     *      name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：群组名称
     *      </blockquote>
     *      memo
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：备注
     *      </blockquote>
     *      ctime
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：创建时间
     *      </blockquote>
     *      mtime
     *      <blockquote>
     *      类型：时间<br/>
     *      描述：修改时间
     *      </blockquote>
     * </blockquote>
     * 
     * @right 该接口需要管理员权限
     */
    @ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp deleteGroup(AuthedJsonReq req) throws Exception {
        Long group_id = req.paramGetNumber("group_id", true, true);
        
        GroupInfo groupInfo = this.groupApi.deleteGroup(req.env, group_id);
        
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("GroupInfo", groupInfo);
        return resp;
    }
    
    /**
     * 获取群组关联的模板
     *
     * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param group_id 
     * <blockquote>
     *      类型：整形<br/>
     *      描述：群组 ID<br/>
     *      必需：YES
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值
     * </blockquote>
     * group_id 
     * <blockquote>
     *      类型：整形<br/>
     *      描述：群组 ID
     * </blockquote>
     * templateIdList
     * <blockquote>
     *      类型：数组<br/>
     *      描述：模板 ID 集合<br/>
     * </blockquote>
     * 
     * @right 该接口需要管理员权限
     */
    @ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp getGroupTemplates(AuthedJsonReq req) throws Exception {
        Long group_id = req.paramGetNumber("group_id", true, true);
        
        Map<Long, List<Long>> groupTemplates = this.groupApi.getGroupTemplates(req.env, group_id);
        
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("group_id", group_id);
        resp.resultMap.put("templateIdList", groupTemplates.get(group_id));
        return resp;
    }
}
