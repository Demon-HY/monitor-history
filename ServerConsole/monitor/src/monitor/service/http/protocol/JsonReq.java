package monitor.service.http.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

import monitor.exception.ParamException;
import monitor.service.http.Env;

public class JsonReq {
    public Env env;
    public Map<String, Object> params;

    public JsonReq(Env env) {
        this.env = env;
        this.params = new HashMap<String, Object>();
    }
    /**
     * 获取客户端传递过来的Long类型参数
     * 默认写到日志里
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param strConv 是否从字符串中转换
     * @return
     * @throws ParamException
     */
    public Long paramGetNumber(String name, boolean notEmpty, boolean strConv) throws ParamException {
        return paramGetNumber(name, notEmpty, strConv, true);
    }
    /**
     * 获取客户端传递过来的Long类型参数
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param logParam 是否写到日志里
     * @param strConv 是否从字符串中转换
     * @return
     * @throws ParamException
     */
    public Long paramGetNumber(String name, boolean notEmpty, boolean strConv, boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (notEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }
        
        if ((o instanceof Long) || (o instanceof Integer) || (o instanceof Short)) {
            return Long.parseLong(String.valueOf(o));
        }
        
        if (strConv && (o instanceof String)) {
            try {
                return Long.parseLong((String)o);
            } catch (Exception e) {
                throw new ParamException("Param '" + name + "' should be number.");
            }
        }
        
        throw new ParamException("Param '" + name + "' should be number.");
    }
    
    /**
     * 获取客户端传递过来的整型参数
     * 默认写到日志里
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @return
     * @throws ParamException
     */
    public Integer paramGetInteger(String name, boolean notEmpty) throws ParamException {
        return paramGetInteger(name, notEmpty, true, Integer.class);
    }
    /**
     * 获取客户端传递过来的整型参数
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param logParam 是否写到日志里
     * @param type 指定参数类型
     * @return
     * @throws ParamException
     */
    public Integer paramGetInteger(String name, boolean notEmpty, boolean logParam, Class<?> type) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (notEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }
        
        if ((o instanceof Integer) || (o instanceof Short)) {
            return Integer.parseInt(o.toString());
        } else if (o instanceof String) {
            try {
                return Integer.parseInt(o.toString());
            } catch (Exception e) {
                throw new ParamException("Param '" + name + "' should be number.");
            }
        }
        
        throw new ParamException("Param '" + name + "' should be integer.");
    }
    /**
     * 获取客户端传递过来的Long类型参数
     * 
     * @param name 参数名 
     * @param min 允许的最大值
     * @param max 允许的最小值
     * @param logParam 是否写到日志里
     * @param strConv 是否从字符串中转换
     * @return
     * @throws ParamException
     */
    public Long paramGetNumber(String name, long min, long max, boolean strConv, boolean logParam) throws ParamException {
        Long number = paramGetNumber(name, true, strConv, logParam);
        if (number < min || number > max) {
            throw new ParamException("Param '" + name + "' out of range");
        }
        return number;
    }
    /**
     * 获取double型参数
     * 默认写日志
     * 
     * @param name 参数名 
     * @param notEmpty 是否允许为空
     * @param strConv 是否从字符串中转换
     * @return
     * @throws ParamException
     */
    public Double paramGetDouble(String name, boolean notEmpty, boolean strConv) throws ParamException {
        return paramGetDouble(name, notEmpty, strConv, true);
    }
    /**
     * 获取double型参数
     * 
     * @param name 参数名 
     * @param notEmpty 是否允许为空
     * @param strConv 是否从字符串中转换
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public Double paramGetDouble(String name, boolean notEmpty, boolean strConv, boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (notEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }
        
        if ((o instanceof Double) || (o instanceof Float)) {
            return (Double)o;
        }
        
        if (strConv && (o instanceof String)) {
            try {
                return Double.parseDouble((String)o);
            } catch (Exception e) {
                throw new ParamException("Param '" + name + "' should be double.");
            }
        }
        
        throw new ParamException("Param '" + name + "' should be double.");
    }
    /**
     * 获取字符串型参数
     * 默认写日志
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @return
     * @throws ParamException
     */
    public String paramGetString(String name, boolean notEmpty) throws ParamException {
        return paramGetString(name, notEmpty, true);
    }
    /**
     * 获取字符串型参数
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public String paramGetString(String name, boolean notEmpty, boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (notEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }
        
        if ((o instanceof String)) {
            String s = (String)o;
            if (notEmpty && s.length() == 0) {
                throw new ParamException("Param '" + name + "' should not be empty.");
            }
            return s;
        }
        
        throw new ParamException("Param '" + name + "' should be string.");
    }
    /**
     * 获取布尔类型参数
     * 默认写日志
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param strConv 是否从字符串转换
     * @return
     * @throws ParamException
     */
    public Boolean paramGetBoolean(String name, boolean notEmpty, boolean strConv) throws ParamException {
        return paramGetBoolean(name, notEmpty, strConv, true);
    }
    /**
     * 获取布尔类型参数
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param strConv 是否从字符串转换
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public Boolean paramGetBoolean(String name, boolean notEmpty, boolean strConv, boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (notEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } 
            
            return null;
        }
        
        if (o instanceof Boolean) {
            return (Boolean)o;
        }
        
        if (strConv && (o instanceof String)) {
            try {
                return Boolean.parseBoolean((String)o);
            } catch (Exception e) {
                throw new ParamException("Param '" + name + "' should be boolean.");
            }
        }
        
        throw new ParamException("Param '" + name + "' should be boolean.");
    }
    
    /**
     * 获取列表类型参数
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param type 参数类型
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public <T> List<T> paramGetList(String name, boolean notEmpty, Class<?> type, boolean logParam) throws ParamException {
        return paramGetList(name, notEmpty, notEmpty, type, logParam);
    }
    public <T> List<T> paramGetList(String name, boolean notNull, boolean notEmpty, Class<?> type, boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (notNull) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return new ArrayList<T>();
            }
        }
        
        if ((o instanceof List)) {
            String str = o.toString();
            
            try {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) JSONArray.parseArray(str, type);
                if (notEmpty && list.size() == 0) {
                    throw new ParamException("Param '" + name + "', list should not be empty.");
                }
                
                for (@SuppressWarnings("unused") T item : list);
                
                return list;
            } catch (Exception e) {
                throw new ParamException("Param '" + name + "', list should contains " + type.getName() + " only.");
            }
            
        }
        
        throw new ParamException("Param '" + name + "' should be a list.");
    }
    /**
     * 获取列表类型参数，其元素为Long类型
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @return
     * @throws ParamException
     */
    public List<Long> paramGetNumList(String name, boolean notEmpty) throws ParamException {
        return paramGetList(name, notEmpty, Long.class, true);
    }
    /**
     * 获取列表类型参数，其元素为Long类型
     * 默认写日志
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public List<Long> paramGetNumList(String name, boolean notNull, boolean notEmpty, boolean logParam) throws ParamException {
        return paramGetList(name, notNull, notEmpty, Long.class, logParam);
    }
    /**
     * 获取列表类型参数，其元素为String类型
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @return
     * @throws ParamException
     */
    public List<String> paramGetStrList(String name, boolean notEmpty) throws ParamException {
        return paramGetList(name, notEmpty, String.class, true);
    }
    /**
     * 获取列表类型参数，其元素为String类型
     * 默认写日志
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public List<String> paramGetStrList(String name, boolean notNull, boolean notEmpty, boolean logParam) throws ParamException {
        return paramGetList(name, notNull, notEmpty, String.class, logParam);
    }
    /**
     * 获取列表类型参数，其元素为Double类型
     * 
     * @param name 参数名
     * @param notEmpty 是否不为空
     * @return
     * @throws ParamException
     */
    public List<Double> paramGetDblList(String name, boolean notEmpty) throws ParamException {
        return paramGetList(name, notEmpty, Double.class, true);
    }
    /**
     * 获取列表类型参数，其元素为Double类型
     * 默认写日志
     * 
     * @param name 参数名
     * @param notEmpty参数名
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public List<Double> paramGetDblList(String name, boolean notNull, boolean notEmpty, boolean logParam) throws ParamException {
        return paramGetList(name, notNull, notEmpty, Double.class, logParam);
    }

    /**
     * 获取Map类型参数
     * 
     * @param name 参数名
     * @param notEmpty 参数名
     * @param kt Map中键的类型
     * @param vt Map中值的类型
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public <K, V> Map<K, V> paramGetMap(String name, boolean notEmpty, Class<K> kt, Class<V> vt, boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (notEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return new HashMap<K, V>();
            }
        }
        
        if ((o instanceof Map)) {
            @SuppressWarnings("unchecked")
            Map<K, V> map = (Map<K, V>)o;
            if (notEmpty && map.size() == 0) {
                throw new ParamException("Param '" + name + "', map should not be empty.");
            }
            
            return map;
        }
        
        throw new ParamException("Param '" + name + "' should be a map.");
    }
    
    /**
     * 获取Map类型参数，其中键值对为String类型
     * 
     * @param name 参数名
     * @param notEmpty 参数名
     * @return
     * @throws ParamException
     */
    public Map<String, Object> paramGetStrMap(String name, boolean notEmpty) throws ParamException {
        return paramGetMap(name, notEmpty, String.class, Object.class, true);
    }
    /**
     * 获取Map类型参数，其中键值对为String类型
     * 
     * @param name 参数名
     * @param notEmpty 参数名
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public Map<String, Object> paramGetStrMap(String name, boolean notEmpty, boolean logParam) throws ParamException {
        return paramGetMap(name, notEmpty, String.class, Object.class, logParam);
    }
    /**
     * 获取参数
     * 
     * @param name 参数名
     * @param notEmpty 是否允许为空
     * @param type 参数的类型
     * @param logParam 是否写日志
     * @return
     * @throws ParamException
     */
    public <T> T paramGetObject(String name, boolean notEmpty, Class<?> type, boolean logParam) throws ParamException {
        Object o = this.params.get(name);
        if (logParam) {
            this.env.logParam(name, o);
        }
        if (o == null) {
            if (notEmpty) {
                throw new ParamException("Param '" + name + "' needed.");
            } else {
                return null;
            }
        }
        
        @SuppressWarnings("unchecked")
        T obj = (T)JSONArray.parseObject(o.toString(), type);
        
        return obj;
        
    }
}
