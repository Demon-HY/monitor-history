package monitor.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.javatuples.Pair;

import monitor.exception.ReadPostException;

public class ServletUtil {

    /**
     * 解析url中的参数
     * @param queryString
     * @return
     */
    public static Map<String, String> decodeQueryString(String queryString) {
        if (queryString == null || queryString.length() == 0) {
            return null;
        }

        MultiMap<String> map = new MultiMap<>();
        byte[] queryStringBytes = queryString.getBytes();
        UrlEncoded.decodeUtf8To(queryStringBytes, 0, queryStringBytes.length, map);

        if (map.size() == 0) {
            return null;
        }

        Map<String, String> result = new HashMap<String, String>();
        Enumeration<String> em = Collections.enumeration(map.keySet());
        while (em.hasMoreElements()) {
            String name = em.nextElement();
            result.put(name, map.getValue(name, 0));
        }
        return result;
    }

    /**
     * 获取post body里的数据
     * @param request
     * @param max
     * @return
     * @throws IOException
     * @throws ReadPostException
     */
    public static byte[] readPostData(HttpServletRequest request, long max) throws IOException, ReadPostException {
        int bodyLen = request.getContentLength();
        if (bodyLen > max) {
            throw new ReadPostException(String.format("Entity Too Large, max:%d cur:%d", max, bodyLen));
        }
        if (bodyLen == -1) {
            bodyLen = 0;
        }

        byte[] body = new byte[bodyLen];
        ServletInputStream is = request.getInputStream();
        int pos = 0;

        while (pos < bodyLen) {
            int received = is.read(body, pos, bodyLen - pos);
            if (received == -1) {
                break;
            }
            pos += received;
        }

        if (pos != bodyLen) {
            throw new ReadPostException(String.format("Client Sent Less Data Than Expected, expected:%s cur:%s",
                    bodyLen, pos));
        }

        return body;
    }
    
    /**
     * 设置请求返回头信息
     * @param response
     * @param httpCode
     * @param respText
     * @throws IOException
     */
    public static void sendHttpResponse(HttpServletResponse response, int httpCode, String respText) throws IOException {
        response.setStatus(httpCode);
        response.setContentType("text/plain;charset=UTF-8");
        byte[] respBin = respText.getBytes();
        response.setContentLength(respBin.length);

        ServletOutputStream os = response.getOutputStream();
        os.write(respBin);
        os.flush();
        os.close();
    }
    	

    /**
     * 跟据 UserAgent 获取 Content-Disposition 响应头。 浏览器下载文件时，需要设置
     * Content-Disposition 响应头，才能正确显示文件名，但是， 不同的浏览器对 Content-Disposition
     * 的解释、编码不同，所以需要对 UserAgent 进行适配。
     * 
     * @param userAgent
     *            浏览器 UserAgent
     * @param fileName
     *            UTF-8 编码的文件名
     * @return
     */
    public static String makeContDisp(String userAgent, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "attachment";
        }

        try {
            userAgent = userAgent.toLowerCase();
            if (userAgent.indexOf("msie") != -1 || userAgent.indexOf("trident") != -1) {
                // IE 6.7.8.9.10.11 Tested
                return String.format("attachment; filename=%s;", urlEncode(fileName));
            }
            if (userAgent.indexOf("chrome") != -1 || userAgent.indexOf("firefox") != -1) {
                // Chrome/Firefox Tested
                return String.format("attachment; filename*=UTF-8''%s", urlEncode(fileName));
            } else {
                // Safari/Android Tested
                return String.format("attachment; filename=\"%s\"", fileName);
            }
        } catch (Exception e) {
            return "attachment";
        }
    }

    /**
     * RFC 3986 URL Encode. 注意，这个不是 x-www-form-urlencoded，空格将编码成 %20，而不是 + 号。
     * "abc def" -> "abc%20def"
     * 
     * @param s
     *            包含非ASCII字符、原始未编码的字符串
     * @return 编码后的字符串
     */
    public static String urlEncode(String s) {

        StringBuilder buf = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') 
                    || (ch >= '0' && ch <= '9') || ".-*_+".indexOf(ch) > -1) {
                buf.append(ch);
            } else {
                byte[] bytes = new String(new char[] {ch}).getBytes();
                for (int j = 0; j < bytes.length; j++) {
                    buf.append('%');
                    buf.append(digits.charAt((bytes[j] & 0xf0) >> 4));
                    buf.append(digits.charAt(bytes[j] & 0xf));
                }
            }
        }
        return buf.toString();
    }
    static final String digits = "0123456789ABCDEF";

    /**
     * 从 HTTP 的 Range 请求头中，获取该 Request 请求的文件开始位置(begin position)和结束位置(end position)
     * 协议标准请查看 https://www.google.com/search?q=http+range
     * @param rangeStr Range 请求头的字符串，比如："bytes=500-999"
     * @param size 该 Request 所请求的文件大小，如果 Range 中没有指定结束位置时（比如 "bytes=123-"），则 size 为其结束位置
     * @return (开始位置，结束位置)
     */
    public static Pair<Long, Long> getHttpRange(String rangeStr, long size) {
        if (rangeStr == null)
            return new Pair<Long, Long>((long) 0, size - 1);

        rangeStr = rangeStr.trim();
        if (!rangeStr.startsWith("bytes=")) {
            return null;
        }

        rangeStr = rangeStr.substring(6);
        rangeStr = rangeStr.trim();
        String[] fields = rangeStr.split("-");

        String beginStr = null;
        String endStr = null;
        if (fields.length == 1 && rangeStr.endsWith("-")) {
            beginStr = fields[0];
            endStr = "";
        } else if (fields.length == 2) {
            beginStr = fields[0];
            endStr = fields[1];
        } else {
            return null;
        }

        if (beginStr.isEmpty()) {
            beginStr = "0";
        }
        if (endStr.isEmpty()) {
            endStr = String.format("%d", size - 1);
        }

        try {
            long begin = Long.parseLong(beginStr);
            long end = Long.parseLong(endStr);
            return new Pair<Long, Long>(begin, end);
        } catch (Exception e) {
            return null;
        }
    }

    public static void testGetHttpRange() {
        String[] samples = {
        		"bytes=500-999 ",
                "bytes=500-999 ",
                " bytes=500-999", 
                "bytes=500-999",
                "bytes= 500-",
                "bytes=-999",
                "bytes="};
        for (String sample : samples) {
            System.out.println(getHttpRange(sample, 1024));
        }
    }

    /**
     * 不会将 "+" 号解码成空格 " " 的 URLDecode
     * @param 以 URLEncode 编码的字符串
     * @return Decode 后的字符串
     * @throws UnsupportedEncodingException
     */
    public static String decode(String s) throws UnsupportedEncodingException {
        return decode(s, "UTF-8");
    }

    public static String decode(String s, String enc) throws UnsupportedEncodingException {

        boolean needToChange = false;
        int numChars = s.length();
        StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
        int i = 0;

        if (enc.length() == 0) {
            throw new UnsupportedEncodingException("URLDecoder: empty string enc parameter");
        }

        char c;
        byte[] bytes = null;
        while (i < numChars) {
            c = s.charAt(i);
            switch (c) {
            case '%':
                try {
                    if (bytes == null)
                        bytes = new byte[(numChars - i) / 3];
                    int pos = 0;

                    while (((i + 2) < numChars) && (c == '%')) {
                        int v = Integer.parseInt(s.substring(i + 1, i + 3), 16);
                        if (v < 0)
                            throw new IllegalArgumentException(
                                    "URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
                        bytes[pos++] = (byte) v;
                        i += 3;
                        if (i < numChars)
                            c = s.charAt(i);
                    }

                    if ((i < numChars) && (c == '%'))
                        throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");

                    sb.append(new String(bytes, 0, pos, enc));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - "
                            + e.getMessage());
                }
                needToChange = true;
                break;
            default:
                sb.append(c);
                i++;
                break;
            }
        }

        return (needToChange ? sb.toString() : s);
    }

}
