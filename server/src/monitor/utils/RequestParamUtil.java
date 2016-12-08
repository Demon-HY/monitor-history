package monitor.utils;

import java.util.List;
import java.util.Map;

public class RequestParamUtil {

    public static synchronized String getString(Map<String, Object>map, String key){
        if (null == map || key == null){
            throw new IllegalArgumentException();
        }
        
        String value = (String)map.get(key);
        if(null == value || value.length() == 0) {
            throw new IllegalArgumentException(key + " is empty");
        }
        
        return value;
    }
    
    @SuppressWarnings("unused")
	public static synchronized Double getDouble(Map<String, Object>map, String key){
        if (null == map || key == null){
            throw new IllegalArgumentException();
        }
        
        Double value = Double.parseDouble(map.get(key).toString());
        if(null == value) {
            throw new IllegalArgumentException(key + " is empty");
        }

        return value;
    }
    
    @SuppressWarnings("unused")
	public static synchronized Long getLong(Map<String, Object>map, String key){
        if (null == map || key == null){
            throw new IllegalArgumentException();
        }
        
        Long value = Long.parseLong(map.get(key).toString());
        if(null == value) {
            throw new IllegalArgumentException(key + " is empty");
        }

        return value;
    }
    
    public static synchronized Integer getInteger(Map<String, Object>map, String key){
        if (null == map || key == null){
            throw new IllegalArgumentException();
        }
        
        Integer value = (Integer)map.get(key);
        if(null == value) {
            throw new IllegalArgumentException(key + " is empty");
        }

        return value;
    }

    public static synchronized Map<String, String> getStringMap(Map<String, Object>map, String key){
        if (null == map || key == null){
            throw new IllegalArgumentException();
        }
        
        @SuppressWarnings("unchecked")
        Map<String, String> value = (Map<String, String>)map.get(key);
        if(null == value || value.size() == 0) {
            throw new IllegalArgumentException(key + " is empty");
        }

        return value;
    }
    
    public static synchronized List<String> getStringList(Map<String, Object>map, String key){
        if (null == map || key == null){
            throw new IllegalArgumentException();
        }
        
        @SuppressWarnings("unchecked")
        List<String> value = (List<String>)map.get(key);
        if(null == value || value.size() == 0) {
            throw new IllegalArgumentException(key + " is empty");
        }

        return value;
    }
    
    public static synchronized List<Long> getLongList(Map<String, Object>map, String key){
        if (null == map || key == null){
            throw new IllegalArgumentException();
        }
        
        @SuppressWarnings("unchecked")
        List<Long> value = (List<Long>)map.get(key);
        if(null == value || value.size() == 0) {
            throw new IllegalArgumentException(key + " is empty");
        }

        return value;
    }
    
}
