package monitor.utils;

import java.sql.Timestamp;

// @javadoc

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 格式化日期
     * @param date 日期
     * @return
     */
    public static String getDateStr(Date date) {
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date);
    }
    
    /**
     * 格式化日期时间
     * @param date 日期时间
     * @return
     */
    public static String getDateTimeStr(Date date) {
        return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(date);
    }
    
    /**
     * 格式化日期时间
     * @param date 日期时间
     * @param format 指定格式
     * @return
     */
    public static String getDateTimeStr(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
    
    /**
     * 返回 yyyy-MM-dd 格式的当前日期字符串
     * @return
     */
    public static String getDateStr() {
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(new Date(currentTimeMillis()));
    }
    
    /**
     * 格式化当前日期时间
     * @return
     */
    public static String getDateTimeStr() {
        return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(new Date(currentTimeMillis()));
    }
    
    public static long parseTimeStr(String str, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(str).getTime();
    }
    
    public static long getDayBefore(int dayBefore) throws ParseException {
        return parseTimeStr(getDateStr(), DEFAULT_DATE_FORMAT) - dayBefore * 24 * 60 * 60;
    }
    
    /**
     * 系统当前时间
     * @return
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    public static Timestamp getTimestamp() {
    	return new Timestamp(Time.currentTimeMillis());
    }
    
}
