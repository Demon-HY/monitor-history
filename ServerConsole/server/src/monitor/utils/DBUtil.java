package monitor.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class DBUtil {

    public static String sqlQuoteMark = "`";
    
    public static void main(String[] args) {

        System.out.println(wrapParams(1, "trew", 4619868971l));
    }

    /**
     * 
     * 拼装往数据库中插入的记录
     * 
     * @param params
     * @return
     */
    public static String wrapParams(Object... params) {
        if (null != params && params.length > 0) {
            List<String> list = new ArrayList<String>();

            for (Object obj : params) {
                String tmp = null;
                if(obj == null){
                    tmp = "null";
                } else if(obj instanceof String){
                    tmp = "'" + StringEscapeUtils.escapeSql(obj.toString()) + "'";
                } else if (isNumber(obj)) {
                    tmp = obj.toString();
                } else {
                    tmp = "'" + obj.toString() + "'";
                }
                list.add(tmp);
            }
            String str = Arrays.toString(list.toArray());
            str = "(" + str.substring(1, str.length() - 1) + ")";
            return str;
        }
        return null;
    }
    
    /**
     * 检查参数是否为数字
     * 
     * @param obj
     * @return
     */
    public static boolean isNumber(Object obj) {
        if (obj instanceof Long || obj instanceof Integer
                || obj instanceof Double || obj instanceof Short
                || obj instanceof BigInteger) {
            return true;
        }
        return false;
    }
    
    /**
     * 检查请求的字段是合法
     * 
     * @param field
     * @param fields
     * @return
     */
    public static boolean isValidField(String field, String[] fields) {
        if (field == null || fields == null) {
            throw new IllegalArgumentException();
        }
        for (String f : fields) {
            if (f.equalsIgnoreCase(field)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查请求参数中排序关键字的合法性
     * 
     * @param order
     * @return
     */
    public static boolean isValidOrder(String order) {
        if (null == order) {
            throw new IllegalArgumentException();
        }
        order = order.toLowerCase();
        switch (order) {
        case "desc" :
        case "asc" : return true;
        default : return false;
        }
    }
    
    /**
     * 检测字符串是否为英文\数字\中文\下划线\横岗\点,防止sql被注入
     * @param str
     * @return 是返回true,反之false.若传入为空则返回正确
     */
    public static  boolean isSimpleString(String str) {
    	if(str != null) {
	    	String regexString = "^[A-Za-z0-9_\\-\u4e00-\u9fa5.%:]+$";
	    	return str.matches(regexString);
    	}
    	return true;
    }
    
    /**
     * 防止 SQL 注入，检测字符串是否为英文\数字
     * @param str
     * @return 是返回true,反之false.若传入为空则返回正确
     */
    public static  boolean checkSqlInjection(String str) {
    	if(str != null) {
    		String regexString = "^[A-Za-z0-9]+$";
            return str.matches(regexString);
    	}
    	return true;
    }
    
}
