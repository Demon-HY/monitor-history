package module.action;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.ActionInfo;
import module.SDK.info.OperationInfo;
import monitor.exception.ParamException;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.utils.DBUtil;

public class ActionHttpApi {

	private ActionApi actionApi;
	private static ActionHttpApi actionHttpApi;
	private ActionHttpApi(ActionApi actionApi) {
		this.actionApi = actionApi;
	}
	
	public static void init(ActionApi actionApi) {
		actionHttpApi = new ActionHttpApi(actionApi);
	}
	
	public static ActionHttpApi getInst() {
		if (null == actionHttpApi) {
			new UnInitilized();
		}
		return actionHttpApi;
	}
	
	/********************************************     对外接口               ********************************************/
	/**
	 * 获取报警装置列表
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
     * 		描述：按什么字段排序(默认 name)<br/>
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
     * 		action_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警名称
     * 		</blockquote>
     * 		interval
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：告警间隔(s)
     * 		</blockquote>
     * 		notice
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：是否在故障恢复后发送通知消息
     * 		</blockquote>
     * 		subject
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：标题
     * 		</blockquote>
     * 		message
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：正文
     * 		</blockquote>
     * 		enabled
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：启用报警
     * 		</blockquote>
     * </blockquote>
     * 
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp listAction(AuthedJsonReq req) throws Exception {
		Integer pageIndex = req.paramGetInteger("pageIndex", false);
		Integer pageSize = req.paramGetInteger("pageSize", false);
		String order = req.paramGetString("order", false);
		String sort = req.paramGetString("sort", false);
		if (null == order) {
			order = "desc";
		}
		if (null == sort) {
			sort = "name";
		}
		// 检查 SQL 注入
		if (!DBUtil.checkSqlInjection(sort) || !DBUtil.checkSqlInjection(order)) {
			throw new ParamException(
					String.format("sort(%s) or order(%s) check have sql injection.", sort, order));
		}
		
		Pair<Integer, List<ActionInfo>> result = this.actionApi.listAction(req.env, pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
	
	/**
	 * 获取报警设置列表
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
     * 		描述：按什么字段排序(默认 name)<br/>
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
     * 		action_operation_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警设置 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警设置名称
     * 		</blockquote>
     * 		step
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：第 N 次告警
     * 		</blockquote>
     * 		action_type
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：动作类型：email，sms
     * 		</blockquote>
     * 		msg_format
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：消息格式<br/>
     * 		默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg}
     * 		</blockquote>
     * </blockquote>
     * 
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp listOperation(AuthedJsonReq req) throws Exception {
		Integer pageIndex = req.paramGetInteger("pageIndex", false);
		Integer pageSize = req.paramGetInteger("pageSize", false);
		String order = req.paramGetString("order", false);
		String sort = req.paramGetString("sort", false);
		if (null == order) {
			order = "desc";
		}
		if (null == sort) {
			sort = "name";
		}
		// 检查 SQL 注入
		if (!DBUtil.checkSqlInjection(sort) || !DBUtil.checkSqlInjection(order)) {
			throw new ParamException(
					String.format("sort(%s) or order(%s) check have sql injection.", sort, order));
		}
		
		Pair<Integer, List<OperationInfo>> result = this.actionApi.listOperation(req.env, pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
	
	/**
	 * 添加报警装置
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
	 * @param name
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警名称<br/>
     * 		必需：YES
     * </blockquote>
     * @param interval
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：告警间隔(s)<br/>
     * 		必需：YES
     * </blockquote>
     * @param notice
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：是否在故障恢复后发送通知消息<br/>
     * 		必需：YES
     * </blockquote>
     * @param subject
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：标题<br/>
     * 		必需：NO
     * </blockquote>
     * @param message
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：正文<br/>
     * 		必需：NO
     * </blockquote>
     * @param enabled
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：启用报警<br/>
     * 		必需：YES
     * </blockquote>
     * @param groupIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：群组ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * @param hostIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：主机ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * @param operationIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：报警设置ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * @param triggerIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：触发器ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * 
	 * @return
	 * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * ActionInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：报警信息<br/>
     *      action_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警名称
     * 		</blockquote>
     * 		interval
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：告警间隔(s)
     * 		</blockquote>
     * 		notice
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：是否在故障恢复后发送通知消息
     * 		</blockquote>
     * 		subject
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：标题
     * 		</blockquote>
     * 		message
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：正文
     * 		</blockquote>
     * 		enabled
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：启用报警
     * 		</blockquote>
     * </blockquote>
     * 
	 * @throws Exception
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp addAction(AuthedJsonReq req) throws Exception {
		String name = req.paramGetString("name", true);
		Long interval = req.paramGetNumber("interval", true, true);
		Integer notice = req.paramGetInteger("notice", true);
		String subject = req.paramGetString("subject", false);
		String message = req.paramGetString("message", false);
		Integer enabled = req.paramGetInteger("enabled", true);
		List<Long> groupIdList = req.paramGetNumList("groupIdList", false);
		List<Long> hostIdList = req.paramGetNumList("hostIdList", false);
		List<Long> operationIdList = req.paramGetNumList("operationIdList", false);
		List<Long> triggerIdList = req.paramGetNumList("triggerIdList", false);
		
		ActionInfo actionInfo = new ActionInfo(name, interval, notice, subject, message, enabled);
		actionInfo = this.actionApi.addAction(req.env, actionInfo, operationIdList, groupIdList, hostIdList, triggerIdList);
		
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("ActionInfo", actionInfo);
		return resp;
	}
	
	/**
	 * 添加报警设置
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param name
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警设置名称<br/>
     * 		必需：YES
     * </blockquote>
     * @param step
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：第 N 次告警<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_type
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：动作类型：email，sms<br/>
     * 		必需：YES
     * </blockquote>
     * @param msg_format
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：消息格式<br/>
     * 		默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg}<br/>
     * 		必需：YES
     * </blockquote>
     * @param userIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：主机ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * OperationInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：报警设置信息<br/>
     * 		action_operation_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警设置 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警设置名称
     * 		</blockquote>
     * 		step
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：第 N 次告警
     * 		</blockquote>
     * 		action_type
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：动作类型：email，sms
     * 		</blockquote>
     * 		msg_format
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：消息格式<br/>
     * 		默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg}
     * 		</blockquote>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp addOperation(AuthedJsonReq req) throws Exception {
		String name = req.paramGetString("name", true);
		Integer step = req.paramGetInteger("step", true);
		String action_type = req.paramGetString("action_type", true);
		String msg_format = req.paramGetString("msg_format", true);
		List<Long> userIdList = req.paramGetNumList("userIdList", false);
		
		OperationInfo operationInfo = new OperationInfo(name, step, action_type, msg_format);
		operationInfo = this.actionApi.addOperation(req.env, operationInfo, userIdList);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("OperationInfo", operationInfo);
        return resp;
	}
	
	/**
	 * 删除报警
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_id 
	 * <blockquote>
     * 		类型：整形<br/>
     * 		描述：报警 ID<br/>
     * 		必需：YES
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * ActionInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：报警信息<br/>
     *      action_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警名称
     * 		</blockquote>
     * 		interval
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：告警间隔(s)
     * 		</blockquote>
     * 		notice
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：是否在故障恢复后发送通知消息
     * 		</blockquote>
     * 		subject
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：标题
     * 		</blockquote>
     * 		message
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：正文
     * 		</blockquote>
     * 		enabled
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：启用报警
     * 		</blockquote>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp deleteAction(AuthedJsonReq req) throws Exception {
		Long action_id = req.paramGetNumber("action_id", true, true);
		
		ActionInfo actionInfo = this.actionApi.deleteAction(req.env, action_id);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("ActionInfo", actionInfo);
        return resp;
	}
	
	/**
	 * 删除报警设置
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param operation_id 
	 * <blockquote>
     * 		类型：整形<br/>
     * 		描述：报警设置 ID<br/>
     * 		必需：YES
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * OperationInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：报警设置信息<br/>
     * 		action_operation_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警设置 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警设置名称
     * 		</blockquote>
     * 		step
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：第 N 次告警
     * 		</blockquote>
     * 		action_type
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：动作类型：email，sms
     * 		</blockquote>
     * 		msg_format
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：消息格式<br/>
     * 		默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg}
     * 		</blockquote>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp deleteOperation(AuthedJsonReq req) throws Exception {
		Long operation_id = req.paramGetNumber("operation_id", true, true);
		
		OperationInfo operationInfo = this.actionApi.deleteOperation(req.env, operation_id);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("OperationInfo", operationInfo);
        return resp;
	}
	
	/**
	 * 修改报警
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_id
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * 		必需：YES
     * </blockquote>
     * @param name
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警名称<br/>
     * 		必需：YES
     * </blockquote>
     * @param interval
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：告警间隔(s)<br/>
     * 		必需：YES
     * </blockquote>
     * @param notice
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：是否在故障恢复后发送通知消息<br/>
     * 		必需：YES
     * </blockquote>
     * @param subject
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：标题<br/>
     * 		必需：NO
     * </blockquote>
     * @param message
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：正文<br/>
     * 		必需：NO
     * </blockquote>
     * @param enabled
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：启用报警<br/>
     * 		必需：YES
     * </blockquote>
     * @param groupIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：群组ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * @param hostIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：主机ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * @param operationIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：报警设置ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * @param triggerIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：触发器ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * 
	 * @return
	 * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * ActionInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：报警信息<br/>
     *      action_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警名称
     * 		</blockquote>
     * 		interval
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：告警间隔(s)
     * 		</blockquote>
     * 		notice
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：是否在故障恢复后发送通知消息
     * 		</blockquote>
     * 		subject
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：标题
     * 		</blockquote>
     * 		message
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：正文
     * 		</blockquote>
     * 		enabled
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：启用报警
     * 		</blockquote>
     * </blockquote>
     * 
	 * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp editAction(AuthedJsonReq req) throws Exception {
		Long action_id = req.paramGetNumber("action_id", true, true);
		String name = req.paramGetString("name", true);
		Long interval = req.paramGetNumber("interval", true, true);
		Integer notice = req.paramGetInteger("notice", true);
		String subject = req.paramGetString("subject", false);
		String message = req.paramGetString("message", false);
		Integer enabled = req.paramGetInteger("enabled", true);
		List<Long> groupIdList = req.paramGetNumList("groupIdList", false);
		List<Long> hostIdList = req.paramGetNumList("hostIdList", false);
		List<Long> operationIdList = req.paramGetNumList("operationIdList", false);
		List<Long> triggerIdList = req.paramGetNumList("triggerIdList", false);
		
		ActionInfo actionInfo = new ActionInfo(action_id, name, interval, notice, subject, message, enabled);
		actionInfo = this.actionApi.editAction(req.env, actionInfo, operationIdList, groupIdList, hostIdList, triggerIdList);
		
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("ActionInfo", actionInfo);
		return resp;
	}
	
	/**
	 * 修改报警设置
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_operation_id
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警设置 ID<br/>
     * 		必需：YES
     * </blockquote>
     * @param name
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警设置名称<br/>
     * 		必需：YES
     * </blockquote>
     * @param step
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：第 N 次告警<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_type
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：动作类型：email，sms<br/>
     * 		必需：YES
     * </blockquote>
     * @param msg_format
     * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：消息格式<br/>
     * 		默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg}<br/>
     * 		必需：YES
     * </blockquote>
     * @param userIdList 
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：主机ID集合<br/>
     * 		必需：NO
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * OperationInfo
     * <blockquote>
     *      类型：JSON 对象<br/>
     *      描述：报警设置信息<br/>
     * 		action_operation_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警设置 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：报警设置名称
     * 		</blockquote>
     * 		step
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：第 N 次告警
     * 		</blockquote>
     * 		action_type
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：动作类型：email，sms
     * 		</blockquote>
     * 		msg_format
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：消息格式<br/>
     * 		默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg}
     * 		</blockquote>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp editOperation(AuthedJsonReq req) throws Exception {
		Long operation_id = req.paramGetNumber("operation_id", true, true);
		String name = req.paramGetString("name", true);
		Integer step = req.paramGetInteger("step", true);
		String action_type = req.paramGetString("action_type", true);
		String msg_format = req.paramGetString("msg_format", true);
		List<Long> userIdList = req.paramGetNumList("userIdList", false);
		
		OperationInfo operationInfo = new OperationInfo(operation_id, name, step, action_type, msg_format);
		operationInfo = this.actionApi.editOperation(req.env, operationInfo, userIdList);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("OperationInfo", operationInfo);
        return resp;
	}
	
	/**
	 * 获取报警关联的群组
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_id
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * 		必需：YES
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * action_id
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * </blockquote>
     * groupIdList
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：群组 ID 集合<br/>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp getActionGroups(AuthedJsonReq req) throws Exception {
		Long action_id = req.paramGetNumber("action_id", true, true);
		
		Map<Long, List<Long>> actionGroups = this.actionApi.getActionGroups(req.env, action_id);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("action_id", action_id);
		resp.resultMap.put("groupIdList", actionGroups.get(action_id));
        return resp;
	}
	
	/**
	 * 获取报警关联的主机
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_id
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * 		必需：YES
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * action_id
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * </blockquote>
     * hostIdList
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：主机 ID 集合<br/>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp getActionHosts(AuthedJsonReq req) throws Exception {
		Long action_id = req.paramGetNumber("action_id", true, true);
		
		Map<Long, List<Long>> actionHosts = this.actionApi.getActionHosts(req.env, action_id);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("action_id", action_id);
		resp.resultMap.put("hostIdList", actionHosts.get(action_id));
        return resp;
	}
	
	/**
	 * 获取报警关联的报警设置
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_id
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * 		必需：YES
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * action_id
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * </blockquote>
     * operationIdList
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：报警设置 ID 集合<br/>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp getActionOperations(AuthedJsonReq req) throws Exception {
		Long action_id = req.paramGetNumber("action_id", true, true);
		
		Map<Long, List<Long>> actionOperations = this.actionApi.getActionOperations(req.env, action_id);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("action_id", action_id);
		resp.resultMap.put("operationIdList", actionOperations.get(action_id));
        return resp;
	}
	
	/**
	 * 获取报警关联的触发器
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param action_id
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * 		必需：YES
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * action_id
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * </blockquote>
     * triggerIdList
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：触发器 ID 集合<br/>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp getActionTriggers(AuthedJsonReq req) throws Exception {
		Long action_id = req.paramGetNumber("action_id", true, true);
		
		Map<Long, List<Long>> actionTriggers = this.actionApi.getActionTriggers(req.env, action_id);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("action_id", action_id);
		resp.resultMap.put("triggerIdList", actionTriggers.get(action_id));
        return resp;
	}
	
	/**
	 * 获取报警设置关联的用户
	 *
	 * @param token 
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：token 用户登录令牌<br/>
     * 		必需：YES
     * </blockquote>
     * @param operation_id
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警设置 ID<br/>
     * 		必需：YES
     * </blockquote>
     * 
     * @return
     * stat
	 * <blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态值<br/>
     * </blockquote>
     * action_id
	 * <blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID<br/>
     * </blockquote>
     * userIdList
	 * <blockquote>
     * 		类型：数组<br/>
     * 		描述：用户 ID 集合<br/>
     * </blockquote>
     * 
     * @throws Exception
	 * @right 该接口需要管理员权限
     */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp getOperationUsers(AuthedJsonReq req) throws Exception {
		Long operation_id = req.paramGetNumber("operation_id", true, true);
		
		Map<Long, List<Long>> actionUsers = this.actionApi.getOperationUsers(req.env, operation_id);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("action_id", operation_id);
		resp.resultMap.put("userIdList", actionUsers.get(operation_id));
        return resp;
	}
}
