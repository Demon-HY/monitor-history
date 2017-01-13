package module.trigger;

import java.util.List;

import org.javatuples.Pair;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.ExpressionInfo;
import module.SDK.info.TriggerInfo;
import monitor.exception.ParamException;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.utils.DBUtil;

public class TriggerHttpApi {

	private TriggerApi triggerApi;
	private static TriggerHttpApi triggerHttpApi;
	private TriggerHttpApi(TriggerApi triggerApi) {
		this.triggerApi = triggerApi;
	}
	
	public static void init(TriggerApi triggerApi) {
		triggerHttpApi = new TriggerHttpApi(triggerApi);
	}
	
	public static TriggerHttpApi getInst() {
		if (null == triggerHttpApi) {
			new UnInitilized();
		}
		return triggerHttpApi;
	}
	
	/********************************************     对外接口               ********************************************/
	/**
	 * 获取触发器列表
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
     * 		描述：按什么字段排序(默认 trigger_id)<br/>
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
     * 		trigger_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：触发器 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：触发器名称
     * 		</blockquote>
     * 		severity
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：告警级别:Information(1),Warning(2),Average(3),High(4),Diaster(5)
     * 		</blockquote>
     * 		enabled
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：是否启动触发器
     * 		</blockquote>
     * 		memo
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：备注
     * 		</blockquote>
     * </blockquote>
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp listTrigger(AuthedJsonReq req) throws Exception {
		Integer pageIndex = req.paramGetInteger("pageIndex", false);
		Integer pageSize = req.paramGetInteger("pageSize", false);
		String order = req.paramGetString("order", false);
		String sort = req.paramGetString("sort", false);
		if (null == order) {
			order = "desc";
		}
		if (null == sort) {
			sort = "trigger_id";
		}
		// 检查 SQL 注入
		if (!DBUtil.checkSqlInjection(sort) || !DBUtil.checkSqlInjection(order)) {
			throw new ParamException(
					String.format("sort(%s) or order(%s) check have sql injection.", sort, order));
		}
		
		Pair<Integer, List<TriggerInfo>> result = this.triggerApi.listTrigger(pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
	
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
     * 		描述：按什么字段排序(默认 expression_id)<br/>
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
     * 		expression_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：触发条件 ID
     * 		</blockquote>
     * 		trigger_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：触发器 ID
     * 		</blockquote>
     * 		service_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：服务 ID
     * 		</blockquote>
     * 		index_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：服务指标  ID
     * 		</blockquote>
     * 		key
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：触发器名称
     * 		</blockquote>
     * 		operator_type
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：运算符
     * 		</blockquote>
     * 		func
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：数据处理方式：Average,Max,Hit,Last
     * 		</blockquote>
     * 		params
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：参数
     * 		</blockquote>
     * 		threshold
     * 		<blockquote>
     * 		类型：<br/>
     * 		描述：阈值 
     * 		</blockquote>
     * 		logic_type
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：与上一条条件的逻辑关系
     * 		</blockquote>
     * </blockquote>
     * 
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp listExpression(AuthedJsonReq req) throws Exception {
		Integer pageIndex = req.paramGetInteger("pageIndex", false);
		Integer pageSize = req.paramGetInteger("pageSize", false);
		String order = req.paramGetString("order", false);
		String sort = req.paramGetString("sort", false);
		if (null == order) {
			order = "desc";
		}
		if (null == sort) {
			sort = "expression_id";
		}
		// 检查 SQL 注入
		if (!DBUtil.checkSqlInjection(sort) || !DBUtil.checkSqlInjection(order)) {
			throw new ParamException(
					String.format("sort(%s) or order(%s) check have sql injection.", sort, order));
		}
		
		Pair<Integer, List<ExpressionInfo>> result = this.triggerApi.listExpression(pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
}
