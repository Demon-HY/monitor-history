package monitor.utils.unit;

public abstract class NumberUtil {
    
    public String value;
    public String unit;
    
    public NumberUtil(String regex, String str) {
        if (null == str) {
            throw new IllegalArgumentException();
        }
        str = str.trim().toLowerCase();
        
        if (str.matches(regex)) {
            this.unit = str.replaceAll("(\\d)+((\\.)(\\d)+)?", "").replaceAll("[\\s]", "");
            this.value = str.replace(unit, "").replaceAll("[\\s]", "");
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public abstract String toStrWithUnit();
    public abstract String toStrWithUnit(String unit);
    
    /**
     * 空间使用百分比信息 转换成 带百分号的字符串
     * 
     * @param persent
     * @return
     */
    public static String getPersentStr(double persent) {
        
        String tmp = "";
        if (persent == 0) {
            tmp = "0";
        } else {
            tmp = String.format("%.2f", (persent * 100));
        }
        
        return tmp + "%";
    }
}
