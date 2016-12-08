package module.imageCode;

import javax.servlet.http.HttpServletResponse;

import monitor.service.http.protocol.JsonProtocol;
import monitor.service.http.protocol.JsonReq;
import monitor.service.http.protocol.JsonResp;
import monitor.service.http.protocol.RetStat;
import monitor.service.http.ApiGateway;

public class ImageCodeHttpApi {

    protected ImageCodeApi codeApi;
    
    public ImageCodeHttpApi(ImageCodeApi codeApi) {
        this.codeApi = codeApi;
    }    
    
    /**
     * 根据指定内容，生成二维码
     *
     * @param content
     * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：二维码内容<br/>
     * 		必需：YES
     * </blockquote>
     * @param width
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：缩略图宽度<br/>
     * 		必需：NO
     * </blockquote>
     * @param height
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：缩略图高度<br/>
     * 		必需：NO
     * </blockquote>
     * @return 二维码图片字节数据流
     * @throws Exception
     */
    @ApiGateway.ApiMethod(protocol = JsonProtocol.class, option = JsonProtocol.BIN_OPTION)
    public JsonResp getQRCodeImage(JsonReq req) throws Exception {
        String content = req.paramGetString("content", true, true);
        Integer w = req.paramGetInteger("width", false);
        Integer h = req.paramGetInteger("height", false);

        w = w == null ? 200 : w;
        h = h == null ? 200 : h;
        
        returnImage(req.env.response);
        QRCode.buildQRCode(content, w, h, req.env.response.getOutputStream());
        
        req.env.stat = RetStat.OK;
        return null;
    }

    /**
     * 获取验证码图片的地址，验证码有效时间为：1分钟
     *
     * @param imageId
     * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：验证码图片ID<br/>
     * 		必需：YES
     * </blockquote>
     * @param width
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：验证码宽度<br/>
     * 		必需：NO
     * </blockquote>
     * @param height
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：验证码高度<br/>
     * 		必需：NO
     * </blockquote>
     * @return 图片字节数据流
     * @throws Exception
     */
    @ApiGateway.ApiMethod(protocol=JsonProtocol.class, option=JsonProtocol.BIN_OPTION)
    public JsonResp getValidateCodeImage(JsonReq req) throws Exception {
        String imageId = req.paramGetString("imageId", true);
        Integer w = req.paramGetInteger("width", false);
        Integer h = req.paramGetInteger("height", false);
        String code = ImageCodeApi.parseValidateCode(imageId);
        
        w = w == null ? ImageCodeConfig.validateCodeImgWidth : w;
        h = h == null ? ImageCodeConfig.validateCodeImgHeight : h;
        
        returnImage(req.env.response);
        ValidateCode vlc = new ValidateCode(w, h, ImageCodeConfig.validateCodeCount, ImageCodeConfig.validateCodeImgRandomLineCount, code);
        vlc.write(req.env.response.getOutputStream());
        
        req.env.stat = RetStat.OK;
        return null;
    }
    
    /**
     * 获取验证码信息
     * 
     * @return imageId 和 url
     * @throws Exception
     */
    @ApiGateway.ApiMethod(protocol = JsonProtocol.class)
    public JsonResp getValidateCodeInfo(JsonReq req) throws Exception {
        JsonResp resp = new JsonResp(RetStat.OK);
        resp.resultMap.putAll(codeApi.createValidateCode(req.env));
        return resp;
    }
    
    /**
     * 给图片添加水印
     * 
     * @param url
     * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：验证码图片地址<br/>
     * 		必需：YES
     * </blockquote>
     * @param text
     * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：水印文字<br/>
     * 		必需：YES
     * </blockquote>
     * @param fontName
     * <blockquote>
     * 		类型：字符串<br/>
     * 		描述：字体名称,如：宋体<br/>
     * 		必需：NO
     * </blockquote>
     * @param fontSize
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：字体大小，单位为像素<br/>
     * 		必需：NO
     * </blockquote>
     * @param x
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：水印文字距离目标图片左侧的偏移量, 如果x<0,则在正中间<br/>
     * 		必需：NO
     * </blockquote>
     * @param y
     * <blockquote>
     * 		类型：整数<br/>
     * 		描述：水印文字距离目标图片上侧的偏移量, 如果y<0,则在正中间<br/>
     * 		必需：NO
     * </blockquote>
     * @param alpha
     * <blockquote>
     * 		类型：Double<br/>
     * 		描述：透明度(0.0 -- 1.0, 0.0为完全透明, 1.0为完全不透明)<br/>
     * 		必需：YES
     * </blockquote>
     * @return 添加水印后的图片流
     */
    @ApiGateway.ApiMethod(protocol=JsonProtocol.class, option=JsonProtocol.BIN_OPTION)
    public JsonResp pressWatermark(JsonReq req) throws Exception {
        String url = req.paramGetString("url", true);
        String text = req.paramGetString("text", true);
        String fontName = req.paramGetString("fontName", false);
        Integer fontSize = req.paramGetInteger("fontSize", false);
        Integer x = req.paramGetInteger("x", false);
        Integer y = req.paramGetInteger("y", false);
        Double alpha = req.paramGetDouble("alpha", false, true);
        
        returnImage(req.env.response);
        codeApi.pressWatermark(req.env, url, text, fontName, fontSize, x, y, alpha);
        
        req.env.stat = RetStat.OK;
        return null;
    }
    
    private static void returnImage(HttpServletResponse response) {
        long adddaysM = 315360000;
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "max-age="+315360000);
        response.addDateHeader("Expires", System.currentTimeMillis() + adddaysM * 1000);
    }
}
