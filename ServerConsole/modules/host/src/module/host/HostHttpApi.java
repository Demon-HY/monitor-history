package module.host;

import java.util.List;

import org.javatuples.Pair;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.HostInfo;
import monitor.exception.ParamException;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.utils.DBUtil;

public class HostHttpApi {

	private HostApi hostApi;
	private static HostHttpApi hostHttpApi;
	private HostHttpApi(HostApi hostApi) {
		this.hostApi = hostApi;
	}
	
	public static void init(HostApi hostApi) {
		hostHttpApi = new HostHttpApi(hostApi);
	}
	
	public static HostHttpApi getInst() {
		if (null == hostHttpApi) {
			new UnInitilized();
		}
		return hostHttpApi;
	}
	
	/********************************************     对外接口               ********************************************/
	/**
	 * 获取主机列表
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
     * 		描述：主机总数<br/>
     * </blockquote>
     * rows
	 * <blockquote>
     * 		类型：JSON 数组<br/>
     * 		描述：主机信息集<br/>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：主机名称<br/>
     * 		</blockquote>
     * 		ip
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：主机 IP<br/>
     * 		</blockquote>
     * 		monitored
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：监控方式(Agent,SNMP,WGET)<br/>
     * 		</blockquote>
     * 		status
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：状态(Online,Down,Unreachable,Offline,Problem)<br/>
     * 		</blockquote>
     * 		interval
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：主机存活状态检测间隔(second)<br/>
     * 		</blockquote>
     * 		memo
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：备注<br/>
     * 		</blockquote>
     * 		ctime
     * 		<blockquote>
     * 		类型：时间<br/>
     * 		描述：创建时间<br/>
     * 		</blockquote>
     * 		mtime
     * 		<blockquote>
     * 		类型：时间<br/>
     * 		描述：修改时间<br/>
     * 		</blockquote>
     * </blockquote>
     * 
	 * @right 该接口需要管理员权限
	 */
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp listHost(AuthedJsonReq req) throws Exception {
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
		
		Pair<Integer, List<HostInfo>> result = this.hostApi.listHost(pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
	
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp addHost(AuthedJsonReq req) throws Exception {
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
		
		Pair<Integer, List<HostInfo>> result = this.hostApi.listHost(pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}
}
