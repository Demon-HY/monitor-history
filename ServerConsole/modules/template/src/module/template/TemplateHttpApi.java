package module.template;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import module.SDK.http.AuthedJsonProtocol;
import module.SDK.http.AuthedJsonReq;
import module.SDK.info.TemplateInfo;
import monitor.exception.ParamException;
import monitor.exception.UnInitilized;
import monitor.service.http.ApiGateway;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.utils.DBUtil;
import monitor.utils.Time;

public class TemplateHttpApi {

	private TemplateApi templateApi;
	private static TemplateHttpApi templateHttpApi;
	private TemplateHttpApi(TemplateApi templateApi) {
		this.templateApi = templateApi;
	}
	
	public static void init(TemplateApi templateApi) {
		templateHttpApi = new TemplateHttpApi(templateApi);
	}
	
	public static TemplateHttpApi getInst() {
		if (null == templateHttpApi) {
			new UnInitilized();
		}
		return templateHttpApi;
	}
	
	/********************************************     对外接口               ********************************************/
	/**
	 * 获取模板列表
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
     * 		template_id
     * 		<blockquote>
     * 		类型：整数<br/>
     * 		描述：模板 ID
     * 		</blockquote>
     * 		name
     * 		<blockquote>
     * 		类型：字符型<br/>
     * 		描述：模板名称
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
	public JsonResp listTemplate(AuthedJsonReq req) throws Exception {
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
		
		Pair<Integer, List<TemplateInfo>> result = this.templateApi.listTemplate(pageIndex, pageSize, order, sort);
		JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("total", result.getValue0());
        resp.resultMap.put("rows", result.getValue1());
        return resp;
	}

	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
	public JsonResp addTemplate(AuthedJsonReq req) throws Exception {
		String name = req.paramGetString("name", true);
		List<Long> serviceIdList = req.paramGetNumList("serviceIdList", false);
		List<Long> triggerIdList = req.paramGetNumList("triggerIdList", false);
		
		Timestamp _time = Time.getTimestamp();
		TemplateInfo templateInfo = new TemplateInfo(name, _time, _time);
		templateInfo = this.templateApi.addTemplate(req.env, templateInfo, serviceIdList, triggerIdList);
		
		JsonResp resp = new JsonResp(RetStat.OK);
		resp.resultMap.put("TemplateInfo", templateInfo);
        return resp;
	}
	
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp editTemplate(AuthedJsonReq req) throws Exception {
	    Long template_id = req.paramGetNumber("template_id", true, true);
        String name = req.paramGetString("name", true);
        List<Long> serviceIdList = req.paramGetNumList("serviceIdList", false);
        List<Long> triggerIdList = req.paramGetNumList("triggerIdList", false);
        
        Timestamp _time = Time.getTimestamp();
        TemplateInfo templateInfo = new TemplateInfo(template_id, name, _time, _time);
        templateInfo = this.templateApi.editTemplate(req.env, templateInfo, serviceIdList, triggerIdList);
        
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("TemplateInfo", templateInfo);
        return resp;
    }
	
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp deleteTemplate(AuthedJsonReq req) throws Exception {
        Long template_id = req.paramGetNumber("template_id", true, true);
        
        TemplateInfo templateInfo = this.templateApi.deleteTemplate(req.env, template_id);
        
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("TemplateInfo", templateInfo);
        return resp;
    }
	
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp getTemplateServices(AuthedJsonReq req) throws Exception {
        Long template_id = req.paramGetNumber("template_id", true, true);
        
        Map<Long, List<Long>> templateServices = this.templateApi.getTemplateServices(req.env, template_id);
        
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("template_id", template_id);
        resp.resultMap.put("serviceIdList", templateServices.get(template_id));
        return resp;
    }
	
	@ApiGateway.ApiMethod(protocol = AuthedJsonProtocol.class)
    public JsonResp getTemplateTriggers(AuthedJsonReq req) throws Exception {
        Long template_id = req.paramGetNumber("template_id", true, true);
        
        Map<Long, List<Long>> templateTriggers = this.templateApi.getTemplateTriggers(req.env, template_id);
        
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.put("template_id", template_id);
        resp.resultMap.put("triggerIdList", templateTriggers.get(template_id));
        return resp;
    }
	
}
