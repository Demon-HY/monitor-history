package monitor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCheck {

	/**
	 * 校验 IP 的合法性<br/>
	 * 这里在匹配的时候，我没有在正则里面去判断ip段的值是否超出255，而是获取到四个ip段后，
	 * 用字符串的分割法去判断，这样写出来的正则表达式会很简单
	 * @param ip
	 * @return true/false
	 */
	public static boolean checkIPVaildity(String ip) {
		// 正则表达式
		String pattern = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}";
		
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(ip);
		
		if (m.matches()) {
			String[] ips = ip.split("\\.");
			return Integer.valueOf(ips[0]).intValue() <= 255 &&
					Integer.valueOf(ips[0]).intValue() <= 255 &&
					Integer.valueOf(ips[0]).intValue() <= 255 &&
					Integer.valueOf(ips[0]).intValue() <= 255;
		} else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		// 测试 IP
		System.out.println("ip:" + checkIPVaildity("127.0.0.1"));
		System.out.println("ip:" + checkIPVaildity("12.120.20.01"));
		System.out.println("ip:" + checkIPVaildity("0.0.0.0"));
		System.out.println("ip:" + checkIPVaildity("1.02.105.142.121"));
	}
}
