package monitor.utils.unit;

public class TimeUnit extends NumberUtil {
    
    public static final String timeRegex = "(\\d)+((\\.)(\\d)+)?(\\s)*(ms|s|m|h|d|w)*";
    
    public static final long VALUE_WEEK = 1000l * 60 * 60 * 24 * 7;
    public static final long VALUE_DAY = 1000l * 60 * 60 * 24;
    public static final long VALUE_HOUR = 1000l * 60 * 60;
    public static final long VALUE_MINUTE = 1000l * 60;
    public static final long VALUE_SECOND = 1000l;
    public static final long VALUE_MILLISECOND = 1;
    public static final String UNIT_MILLISECOND = "ms";
    public static final String UNIT_SECOND = "s";
    public static final String UNIT_MINUTE = "min";
    public static final String UNIT_HOUR = "h";
    public static final String UNIT_DAY = "d";
    public static final String UNIT_WEEK = "w";
    
    public Double value;
    public String unit;
    
    public TimeUnit(String str) {
        super(timeRegex, str);
        this.value = Double.parseDouble(super.value);
        this.unit = toStanderUnit(super.unit);
        toMSValue();
    }
    
    public static String toStanderUnit(String str) {
        if (null == str || str.trim().length() == 0) {
            return UNIT_MILLISECOND;
        }
        str = str.toLowerCase();
        
        switch(str) {
        case "ms" :
            return UNIT_MILLISECOND;
        case "s" :
            return UNIT_SECOND;
        case "min" :
            return UNIT_MINUTE;
        case "h" :
            return UNIT_HOUR;
        case "d" :
            return UNIT_DAY;
        case "w" :
            return UNIT_WEEK;
        default:
            throw new IllegalArgumentException();
        }
        
    }
    
    private void toMSValue() {
        switch(unit) {
        case UNIT_MILLISECOND :
            value = value * VALUE_MILLISECOND;
            break;
        case UNIT_SECOND :
            value = value * VALUE_SECOND;
            break;
        case UNIT_MINUTE :
            value = value * VALUE_MINUTE;
            break;
        case UNIT_HOUR :
            value = value * VALUE_HOUR;
            break;
        case UNIT_DAY :
            value = value * VALUE_DAY;
            break;
        case UNIT_WEEK :
            value = value * VALUE_WEEK;
            break;
        }
    }
    
    public static String getSuitUnit(double value) {
        String unit = UNIT_MILLISECOND;
        if (value >= VALUE_WEEK) {
            unit = UNIT_WEEK;
        } else if (value >= VALUE_DAY) {
            unit = UNIT_DAY;
        } else if (value >= VALUE_HOUR) {
            unit = UNIT_HOUR;
        } else if (value >= VALUE_MINUTE) {
            unit = UNIT_MINUTE;
        } else if (value >= VALUE_SECOND) {
            unit = UNIT_SECOND;
        }
        
        return unit;
    }
    
    public String toStrWithUnit() {
        String unit = getSuitUnit(value);
        return toStrWithUnit(unit);
    }

    public String toStrWithUnit(String unit) {
        if (null == unit) {
            throw new IllegalArgumentException();
        }
        
        if (value == 0) {
            return "0 " + unit;
        }
        
        double result;
        switch(unit) {
        
        case UNIT_SECOND :
            result = ((double)value) / VALUE_SECOND;
            break;
        case UNIT_MINUTE :
            result = ((double)value) / VALUE_MINUTE;
            break;
        case UNIT_HOUR :
            result = ((double)value) / VALUE_HOUR;
            break;
        case UNIT_DAY :
            result = ((double)value) / VALUE_DAY;
            break;
        case UNIT_WEEK :
            result = ((double)value) / VALUE_WEEK;
            break;
        case UNIT_MILLISECOND :
            result = value;
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        String tmp = String.format("%.2f", result);
        if (tmp.equals("0.00")) {
            tmp = "0.01";
        }
        
        return tmp + " " + unit;
    }
    
    public static void main(String[] args) {
        TimeUnit bu = new TimeUnit("86400000");
        System.out.println(bu.value.longValue());
        System.out.println(bu.toStrWithUnit());
    }
}
