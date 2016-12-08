package monitor.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

import com.alibaba.fastjson.JSONException;

public class URLConnectionUtil {

	public static String sendPost(String url, byte[] param) {
		return sendPost(url, param, null);
	}
	
	public static String sendPost(String url, byte[] param, Map<String, String> headers) {
        String result = "";
        try {
            URL httpurl = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) httpurl
                    .openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(6000);
            if (null != headers) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    String value = headers.get(key);
                    httpConn.addRequestProperty(key, value);
                }
            }
            
            if (null != param) {
                OutputStream writer = httpConn.getOutputStream();
                writer.write(param);
                writer.flush();
                writer.close();
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public static Pair<byte[], Map<String, String>> send(String url, byte[] param, Map<String, String> headers) {
        byte[] result = null;
        Map<String, String> map = new HashMap<String, String>();
        try {
            URL httpurl = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) httpurl
                    .openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(6000);
            if (null != headers) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    String value = headers.get(key);
                    httpConn.addRequestProperty(key, value);
                }
            }
            
            if (null != param) {
                OutputStream writer = httpConn.getOutputStream();
                writer.write(param);
                writer.flush();
                writer.close();
            }
            
            Map<String, List<String>> rspHs = httpConn.getRequestProperties();
            Set<String> names = rspHs.keySet();
            for (String name : names) {
                List<String> hs = rspHs.get(name);
                if (null != hs) {
                    String value = Arrays.toString(hs.toArray());
                    value = value.substring(1, value.length()-1);
                    map.put(name, value);
                }
            }
            
            InputStream is = httpConn.getInputStream();
            result = new byte[is.available()];
            
            is.read(result);
            is.close();
            
            httpConn.disconnect();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<byte[], Map<String, String>>(result, map);
    }
	
	public static void main(String[] args) throws JSONException, ParseException, MalformedURLException {
        
//	    JSONObject jo = new JSONObject();
//	    
//	    jo.put("pdid", 0);
//	    jo.put("name", "火狼xxx");
//	    jo.put("token", "0WY87COsSrE");
//	    
//	    String str = jo.toString();
//	    
//	    String url = "http://192.168.1.197:9999/dept/api/adminCreateDept";
//	    
//	    String s =sendPost(url, str.getBytes());
//	    System.out.println(s);
	    
//	    URL u = new URL("http://192.168.1.197:99");
//	    System.out.println(u.getHost());
//	    System.out.println(u.getPort());
	    
//	    Map<String, Object> map = new HashMap<String, Object>();
//        map.put("uid", 5);
//        map.put("fid", 34360086867l);
//        map.put("gid", 0);
//        Long expires = Time.currentTimeMillis() + 24l * 3600 * 1000;
//        map.put("expires", expires);
//        String token = JSONObject.toJSONString(map);
//        
//        token = Base64.encodeBase64URLSafeString(token.getBytes());
//        
//        String src = String.format("%s/webdoc/wopi/files/X-X", "http://10.72.90.39", "80");
//	    
//	    String url = String.format("%s%s%s&WOPISrc=%s&type=png&o15=1&access_token=%s&PdfMode=1", "http://preview.yunpan.hnagroup.com", "/wv/docdatahandler.ashx?", "ui=zh-CN", src, token);
//        
//        String result = URLConnectionUtil.sendPost(url, null);
//        System.out.println(result);
//        
//        String regex = "<pageset[^>]+count=\"[\\d]+\"[^>]*>";
//        int count = 0;
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(result);
//        while (matcher.find()) {
//            String str = matcher.group();
//            String format = "{0}count=\"{1, number}\"{2}";
//            MessageFormat mf = new MessageFormat(format);
//            Object[] objs = mf.parse(str);
//            Long c = (Long) objs[1];
//            if (null != c) {
//                count += c;
//            }
//        }
//        System.out.println(count);
	    
	    List<String> l = new ArrayList<String>();
	    l.add("asda,sd");
	    l.add("gff");
	    String value = Arrays.toString(l.toArray());
        value = value.substring(1, value.length()-1);
	    System.out.println(value);
	    
    }
}
