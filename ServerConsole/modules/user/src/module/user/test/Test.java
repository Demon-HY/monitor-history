package module.user.test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import monitor.utils.SSHA;
import monitor.utils.Time;
import monitor.utils.unit.TimeUnit;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		// Timestamp
//		Timestamp nowdate1 = new Timestamp(System.currentTimeMillis());
//		System.out.println("System.currentTimeMillis():"+nowdate1);
//		
//		Date date = new Date();   
//		Timestamp nowdate2 = new Timestamp(date.getTime());
//		System.out.println("new Date():"+nowdate2);
//		
//		long now = Time.currentTimeMillis();
//		long age = new TimeUnit("1w").value.longValue();
//		
//		System.out.println(new Timestamp(now));
//		System.out.println(new Timestamp(now + age));
		
		
//		String d3 = "name=module&password=hey杀an10发22&t\"ype=na呵呵me&exattr=&email=1764496637&qq=1764496637？@#……&￥@&&nick=monitor";
//		byte[] encode = Base64.encodeBase64(d3.getBytes("utf-8"));
//		System.out.println(new String(encode));
//		
//		String s = "bmFtZT1kZW1vbiZwYXNzd29yZD1oZXnmnYBhbjEw5Y+RMjImdCJ5cGU9bmHlkbXlkbVtZSZleGF0dHI9JmVtYWlsPTE3NjQ0OTY2MzcmcXE9MTc2NDQ5NjYzN++8n0Aj4oCm4oCmJu+/pUAmJm5pY2s9RGVtb24=";
//		System.out.println(new String(Base64.decodeBase64(s), "utf-8"));
		
		// password sha1
		String a = "heyan1022";
		String pass1 = SSHA.getSaltedPassword(a);
		System.out.println(pass1);
		String pass2 = SSHA.getSaltedPassword(a);
		System.out.println(pass2);
		
		System.out.println(SSHA.verifySaltedPassword(a.getBytes(), pass1));
		System.out.println(SSHA.verifySaltedPassword(a.getBytes(), pass2));
	}
}
