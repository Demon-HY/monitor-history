package monitor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCheck {

	/**
	 * 校验 IP 的合法性<br/>
	 * @param ip
	 * @return true/false
	 */
	public static boolean checkIPVaildity(String ip) {
		// 正则表达式
		String pattern = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}";
		
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(ip);
		
		return m.matches();
	}
	
	public static void main(String[] args) {
		// 测试 IP
		System.out.println("ip:" + checkIPVaildity("127.0.0.1"));
		System.out.println("ip:" + checkIPVaildity("0.0.0.0"));
		System.out.println("ip:" + checkIPVaildity("255.255.255.255"));
		System.out.println("ip:" + checkIPVaildity("12.120.20.01"));
		System.out.println("ip:" + checkIPVaildity("1.02.105.142.121"));
		System.out.println("ip:" + checkIPVaildity("12.120.20.256"));
	}
}
