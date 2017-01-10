package monitor.service.http.protocol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import monitor.service.http.Env;

public class ErrTextFormatter {

    private static Map<String, Map<String, ErrTextFormat>> errTextFormats = new HashMap<String, Map<String, ErrTextFormat>>();
    public static ErrTextFormat commonErrTextFormat;
    public static void registerErrTextFormat(String module, ErrTextFormat format) throws IllegalArgumentException, IllegalAccessException {
        if (format == null) {
            return;
        }
        if (module == null) {
            commonErrTextFormat = format;
            return;
        }
        List<String> stats = getStat(format);
        for (String stat : stats) {
            Map<String, ErrTextFormat> formats = errTextFormats.get(stat);
            if (formats == null) {
                formats = new HashMap<String, ErrTextFormat>();
            }
            formats.put(module, format);
            errTextFormats.put(stat, formats);
        }
    }
    
    /**
     * 
     * 查找返回码的解析文本</br>
     * 
     * 1、当返回码为OK时，没有错误码解析文本</br>
     * 2、当没有指定归属module时优先从通用返回码解析器中查找</br>
     * 3、当指定了归属module时，或者在通用返回码解析器查找不到时</br>
     * 4、从指定模块的解析器中查找，若还是找不到，再一次到通用返回码解析器中查找</br>
     * 
     * @param env
     * @param module
     * @param stat
     * @return
     */
    public static String getErrText(Env env, String module, String stat) {
        if (RetStat.OK.equalsIgnoreCase(stat)) {
            return null;
        }
        
        String text = null;
        if (null == module && commonErrTextFormat != null) {
            text = commonErrTextFormat.getErrText(env, stat);
        }
        
        if (null == text) {
            Map<String, ErrTextFormat> formats = errTextFormats.get(stat);

            ErrTextFormat format = formats != null && null != module ? formats.get(module) : null;
            if (null == format && formats != null && formats.size() > 0) {
                Set<String> keys = formats.keySet();
                Iterator<String> it = keys.iterator();
                format = formats.get(it.next());
            }
            
            text = format != null ? format.getErrText(env, stat) : null;
            if (null == text && commonErrTextFormat != null) {
                text = commonErrTextFormat.getErrText(env, stat);
            }
        }
        
        return text;
    }
    
    /**
     * 
     * 收集注册的返回码解析器中的返回码，收集到的返回码，会被认为是该解析器能够解析的返回码</br>
     * 解析器中的返回码必须是字符串类型，并且以 ERR_ 开头
     * 
     * @param format
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static List<String> getStat(ErrTextFormat format) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = format.getClass().getDeclaredFields();
        if (null == fields) {
            return null;
        }
        
        List<String> stats = new LinkedList<String>();
        
        for (Field field : fields) {
            String name = field.getName();
            if (name.matches("ERR_.+")) {
                if (field.getType() == String.class) {
                    Object value = field.get((Object)format);
                    if (null != value) {
                        stats.add((String)value);
                    }
                }
            }
        }
        
        return stats;
    }
}
