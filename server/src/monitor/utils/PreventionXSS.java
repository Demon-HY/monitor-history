package monitor.utils;

/**
 * XSS : 跨站脚本攻击缩写
 * 防止 XSS 攻击
 * @author monitor
 *
 */
public class PreventionXSS {
    
    
    public static String cleanXSS(String value) {
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
    }
    
    
//    public static void main(String[] args) {
//        String test = "<script type=\"text/javascript\"> <!--  window.onload=window.onresize=function()"
//                + "{  if(document.getElementById(\"mm2\").clientHeight<document.getElementById(\"mm1\").clientHeight)"
//                + "{  document.getElementById(\"mm2\").style.height=document.getElementById(\"mm1\").offsetHeight+\"px\"; } else"
//                + "{  document.getElementById(\"mm1\").style.height=document.getElementById(\"mm2\").offsetHeight+\"px\"; }";
//        String str = PreventionXSS.cleanXSS(test);
//        System.out.println(str);
//    
//    }
}
