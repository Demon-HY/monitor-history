package module.action;

import java.util.List;

import org.javatuples.Pair;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.ActionInfo;
import module.SDK.info.ActionOperationInfo;
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
		
		Pair<Integer, List<ActionInfo>> result = this.actionApi.listAction(pageIndex, pageSize, order, sort);
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
     * 		action_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：报警 ID
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
     * 		描述：消息格式</br>
     * 		默认: Host({hostname},{ip}) service({service_name}) has issue,msg:{msg}
     * 		</blockquote>
     * </blockquote>
     * 
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp listActionOperation(AuthedJsonReq req) throws Exception {
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
		
		Pair<Integer, List<ActionOperationInfo>> result = this.actionApi.listActionOperation(pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
}
