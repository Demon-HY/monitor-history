package module.imageCode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import module.SDK.stat.ImageCodeRetStat;
import monitor.exception.LogicalException;
import monitor.exception.UnInitilized;
import monitor.service.http.Env;
import monitor.utils.Time;
import monitor.utils.XCodeUtil;

public class ImageCodeApi {

    private static ImageCodeApi codeApi;
    public static ImageCodeApi getInst() throws UnInitilized {
        if (codeApi == null) {
            throw new UnInitilized();
        }
        return codeApi;
    }
    public static void init() throws LogicalException {
        codeApi = new ImageCodeApi();
//        SdkCenter.getInst().addInterface(IUtils.name, codeApi);
    }

    /**
     * 创建图片验证码
     * @param env
     * @return Map
     * <blockquote>
     * 		code 验证码<br/>
     * 		expired 到期时间<br/>
     * 		imageId 图片验证码唯一标识<br/>
     * 		url 图片验证码获取路径
     * </blockquote>
     */
    public Map<String, Object> createValidateCode(Env env) {
        String code = ValidateCode.randCode(ImageCodeConfig.validateCodeCount);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", code);
        map.put("expired", Time.currentTimeMillis() + ImageCodeConfig.validateCodeAge);
        String imageId = XCodeUtil.xEncode(map);
        map = new HashMap<String, Object>();
        map.put("imageId", imageId);
        
        String schema = env.request.getScheme().toLowerCase();
        String host = env.request.getServerName();
        int port = env.request.getServerPort();
        
        String url = String.format("%s://%s:%s/%s/api/getValidateCodeImage?imageId=%s", schema, host, port, Init.MODULE_NAME, imageId);
        
        map.put("url", url);
        return map;
    }
    
    /**
     * 校验验证码是否正确
     * @param imageId
     * @param validateCode
     * @return true/false
     * @throws LogicalException
     */
    public boolean validate(String imageId, String validateCode) throws LogicalException {
        if (null == imageId || null == validateCode) {
            throw new IllegalArgumentException(validateCode);
        }
        String realCode = parseValidateCode(imageId);
        if (!validateCode.equalsIgnoreCase(realCode)) {
            throw new LogicalException(ImageCodeRetStat.ERR_INVALID_VALIDATE_CODE, imageId + ":" + realCode + ":" + validateCode);
        }
        return true;
    }
    
    public static String parseValidateCode(String imageId) throws LogicalException {
        Map<String, Object> map = XCodeUtil.xDecode(imageId);
        String code = (String)map.get("code");
        long expired = (Long)map.get("expired");
        if (expired < Time.currentTimeMillis()) {
            throw new LogicalException(ImageCodeRetStat.ERR_VALIDATE_CODE_EXPIRED, imageId);
        }
        return code;
    }
    
    /**
     * 给图片添加水印
     * @param env
     * @param urlStr
     * @param pressText
     * @param fontName
     * @param fontSize
     * @param x
     * @param y
     * @param alpha
     * @throws IOException
     */
    public void pressWatermark(Env env, String urlStr, String pressText, String fontName,
            Integer fontSize, Integer x, Integer y, Double alpha) throws IOException {
        
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setReadTimeout(2000);
        connection.connect();
        Image image = ImageIO.read(connection.getInputStream());
        connection.disconnect();
        
        fontName = fontName == null ? Watermark.DEFAULT_FONT_NAME : fontName;
        fontSize = fontSize == null ? Watermark.DEFAULT_FONT_SIZE : fontSize;
        alpha = alpha == null ? Watermark.DEFAULT_ALPHA : alpha;
        
        BufferedImage bufferedImage = Watermark.pressText(image, pressText, fontName, Font.BOLD | Font.ITALIC, fontSize, Color.BLACK, x, y, alpha.floatValue());
        ImageIO.write(bufferedImage, "jpg", env.response.getOutputStream());
    }
    
    public void cutImage(InputStream in, OutputStream out, int x, int y, int w, int h) throws IOException {
        if (x < 0 || y < 0 || w < 0 || h < 0) {
            throw new IllegalArgumentException();
        }
        ImageUtil.cutImage(in, out, x, y, w, h);
    }
    
    public byte[] cutImage(byte[] data, int x, int y, int w, int h) throws IOException {
        if (x < 0 || y < 0 || w < 0 || h < 0) {
            throw new IllegalArgumentException();
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cutImage(bais, baos, x, y, w, h);
        data = baos.toByteArray();
        
        return data;
    }
    
}
