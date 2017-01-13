package module.service;

import java.util.List;

import org.javatuples.Pair;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.IndexInfo;
import module.SDK.info.ServiceInfo;
import monitor.exception.ParamException;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.utils.DBUtil;

public class ServiceHttpApi {

	private ServiceApi serviceApi;
	private static ServiceHttpApi serviceHttpApi;
	private ServiceHttpApi(ServiceApi serviceApi) {
		this.serviceApi = serviceApi;
	}
	
	public static void init(ServiceApi serviceApi) {
		serviceHttpApi = new ServiceHttpApi(serviceApi);
	}
	
	public static ServiceHttpApi getInst() {
		if (null == serviceHttpApi) {
			new UnInitilized();
		}
		return serviceHttpApi;
	}
	
	/********************************************     对外接口               ********************************************/
	/**
     * 获取服务列表
     *
     * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param pageIndex 
     * <blockquote>
     *      类型：整数<br/>
     *      描述：分页页码<br/>
     *      必需：NO
     * </blockquote>
     * @param pageSize 
     * <blockquote>
     *      类型：整数<br/>
     *      描述：分页大小<br/>
     *      必需：NO
     * </blockquote>
     * @param order 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：排序规则(desc/asc默认desc)<br/>
     *      必需：NO
     * </blockquote>
     * @param sort 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：按什么字段排序(默认 service_id)<br/>
     *      必需：NO
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     * </blockquote>
     * total
     * <blockquote>
     *      类型：整数<br/>
     *      描述：群组总数<br/>
     * </blockquote>
     * rows
     * <blockquote>
     *      类型：JSON 数组<br/>
     *      描述：服务信息集<br/>
     *      service_id
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：服务 ID
     *      </blockquote>
     *      name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：群组名称
     *      </blockquote>
     *      interval
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：监控间隔(s)
     *      </blockquote>
     *      plugin_name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：插件名
     *      </blockquote>
     *      has_sub_service
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：是否有子服务(例如网卡有eth0,lo等)
     *      </blockquote>
     *      memo
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：备注
     *      </blockquote>
     * </blockquote>
     * 
     * @right 该接口需要管理员权限
     */
    @ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp listService(AuthedJsonReq req) throws Exception {
        Integer pageIndex = req.paramGetInteger("pageIndex", false);
        Integer pageSize = req.paramGetInteger("pageSize", false);
        String order = req.paramGetString("order", false);
        String sort = req.paramGetString("sort", false);
        if (null == order) {
            order = "desc";
        }
        if (null == sort) {
            sort = "service_id";
        }
        // 检查 SQL 注入
        if (!DBUtil.checkSqlInjection(sort) || !DBUtil.checkSqlInjection(order)) {
            throw new ParamException(
                    String.format("sort(%s) or order(%s) check have sql injection.", sort, order));
        }
        
        Pair<Integer, List<ServiceInfo>> result = this.serviceApi.listService(pageIndex, pageSize, order, sort);
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
    }
    
    /**
     * 获取服务的各项指标列表
     *
     * @param token 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：token 用户登录令牌<br/>
     *      必需：YES
     * </blockquote>
     * @param pageIndex 
     * <blockquote>
     *      类型：整数<br/>
     *      描述：分页页码<br/>
     *      必需：NO
     * </blockquote>
     * @param pageSize 
     * <blockquote>
     *      类型：整数<br/>
     *      描述：分页大小<br/>
     *      必需：NO
     * </blockquote>
     * @param order 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：排序规则(desc/asc默认desc)<br/>
     *      必需：NO
     * </blockquote>
     * @param sort 
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：按什么字段排序(默认 name)<br/>
     *      必需：NO
     * </blockquote>
     * 
     * @return
     * stat
     * <blockquote>
     *      类型：字符型<br/>
     *      描述：状态值<br/>
     *      必需：YES
     * </blockquote>
     * total
     * <blockquote>
     *      类型：整数<br/>
     *      描述：群组总数<br/>
     *      必需：YES
     * </blockquote>
     * rows
     * <blockquote>
     *      类型：JSON 数组<br/>
     *      描述：群组信息集<br/>
     *      index_id
     *      <blockquote>
     *      类型：整数<br/>
     *      描述：服务指标 ID
     *      </blockquote>
     *      name
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：服务指标名
     *      </blockquote>
     *      key
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：服务指标具体指标名
     *      </blockquote>
     *      type
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：指标数据类型
     *      </blockquote>
     *      memo
     *      <blockquote>
     *      类型：字符型<br/>
     *      描述：备注
     *      </blockquote>
     * </blockquote>
     * @right 该接口需要管理员权限
     */
    @ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp listIndex(AuthedJsonReq req) throws Exception {
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
        
        Pair<Integer, List<IndexInfo>> result = this.serviceApi.listIndex(pageIndex, pageSize, order, sort);
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
    }
}
