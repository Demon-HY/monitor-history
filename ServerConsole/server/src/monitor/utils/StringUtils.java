package monitor.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import org.apache.commons.codec.binary.Base64;

public class StringUtils {
	private StringUtils() {}

	public static String getString(String str) {
	    //解密  不要挪到在javadoc里了
	    //此方法不允许重构，如需重构需要申请
		return StringUtilsEx.getString(str, 3, "MQP0TGVX0KFBX5F6", "Pp0cH6sBQZA");
	}
	
	public static String setString(String str) {
	    //加密 不要挪到在javadoc里了
	    //此方法不允许重构，如需重构需要申请
		return StringUtilsEx.setString(str, 3, "MQP0TGVX0KFBX5F6", "Pp0cH6sBQZA", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXWZ");
	}

	public static String reverseString(String str) {
	    //解密  不要挪到在javadoc里了
	    //此方法不允许重构，如需重构需要申请
		return StringUtilsEx.getStringEx(str, "MTF0KFBXQVX5P0G6", "s6BQ0cHPpZA");
	}
	
	public static String sortString(String str) {
	    //加密 不要挪到在javadoc里了
	    //此方法不允许重构，如需重构需要申请
		return StringUtilsEx.setStringEx(str, "MTF0KFBXQVX5P0G6", "s6BQ0cHPpZA", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXWZ");
	}
	
	public static void main(String[] args) {
	    String s = StringUtils.sortString("ekpapi");
	    System.out.println(s);
	}
	
	/**
	 * 一段匹配
	 * @param ip 被检查的ip
	 * @param expression 表达式
	 * @return 是否匹配
	 */
	public static boolean checkIp(String ip, String expression) {
//        Pattern r = Pattern.compile(expression);
//        Matcher m = r.matcher(ip);
//		return m.matches();
		if (null != ip && null != expression) {
			return ip.matches(expression);			
		}
		return false;
	}
	/**
	 * 多段匹配
	 * @param ip 被检查的ip
	 * @param expression 表达式
	 * @return
	 */
	public static boolean checkIpMultiSection(String ip, String expression) {
		if (null != ip && null != expression) {
			for (String exp : expression.split(",")) {
				if (true == checkIp(ip, exp))
					return true;
			}
		}
		return false;
	}

	/**
	 * 多段匹配并且检查白名单设置
	 * @param ip
	 * @param expression
	 * @param white
	 * @return
	 */
	public static boolean checkIpMultiSectionWithWhiteFlag(String ip, String expression, boolean white) {
		if (null != ip && null != expression) {
			if (true == checkIpMultiSection(ip, expression)) {
				if (true == white)
					return true;
			} else {
				if (false == white)
					return true;
			}
		}
		return false;
	}
	
	
	
}

class StringUtilsEx {
	private StringUtilsEx() {}

	public static String getString(String str, int prefixLength, String crcKey, String xorKey) {
	    //解密
	    //此方法不允许重构，如需重构需要申请
	    String real = str.substring(prefixLength);
        byte[] rst = Base64.decodeBase64(real);

        byte[] data = Arrays.copyOf(rst, rst.length-(Long.SIZE / Byte.SIZE));
        byte[] crc = Arrays.copyOfRange(rst, data.length, rst.length);

        long value = byteArrayToLong(crc);
        byte[] realCrc = crcUnsigned(data, crcKey);
        long realValue = byteArrayToLong(realCrc);
        if (!(value == realValue)) {
            // System.out.println("CRC verify failed.");
            // System.exit(0);
        	return null;
        }

        xorCode(data, xorKey);

        return new String(data);
	}
	
	public static String setString(String str, int prefixLength, String crcKey, String xorKey, String digits) {
	    //加密
	    //此方法不允许重构，如需重构需要申请
	    byte[] data = str.getBytes();
        xorCode(data, xorKey);
        byte[] crc = crcUnsigned(data, crcKey);
        byte[] tmp = new byte[data.length + crc.length];
        for (int i = 0; i < data.length; i++) {
            tmp[i] = data[i];
        }
        for (int i = 0; i < crc.length; i++) {
            tmp[i+data.length] = crc[i];
        }
        data = tmp;

        return createPrefix(prefixLength, digits) + Base64.encodeBase64URLSafeString(data);
	}
	
	public static String getStringEx(String str, String crcKey, String xorKey) {
	    //解密
	    //此方法不允许重构，如需重构需要申请
        byte[] rst = Base64.decodeBase64(str);

        byte[] data = Arrays.copyOf(rst, rst.length-(Long.SIZE / Byte.SIZE));
        byte[] crc = Arrays.copyOfRange(rst, data.length, rst.length);

        long value = byteArrayToLong(crc);
        byte[] realCrc = crcUnsigned(data, crcKey);
        long realValue = byteArrayToLong(realCrc);
        if (!(value == realValue)) {
            // System.out.println("CRC verify failed.");
            // System.exit(0);
        	return null;
        }

        xorCode(data, xorKey);

        return new String(data);
	}
	
	public static String setStringEx(String str, String crcKey, String xorKey, String digits) {
	    //加密
	    //此方法不允许重构，如需重构需要申请
	    byte[] data = str.getBytes();
        xorCode(data, xorKey);
        byte[] crc = crcUnsigned(data, crcKey);
        byte[] tmp = new byte[data.length + crc.length];
        for (int i = 0; i < data.length; i++) {
            tmp[i] = data[i];
        }
        for (int i = 0; i < crc.length; i++) {
            tmp[i+data.length] = crc[i];
        }
        data = tmp;

        return Base64.encodeBase64URLSafeString(data);
	}

	private static String createPrefix(int prefixLength, String digits) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < prefixLength; i++) {
            int tmp = random.nextInt();
            if (tmp < 0) {
                tmp = -tmp;
            }
            int index = tmp % digits.length();
            sb.append(digits.charAt(index));
        }

        return sb.toString();
    }

	private static byte[] longToByteArray(long value) {
        ByteBuffer bb = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(value);
        return bb.array();
    }

    private static long byteArrayToLong(byte[] data) {
        long value = 0;
        for (int i = 0; i < data.length; i++) {
            long tmp = 0x000000ff & data[i];

            value += tmp << (8 * i);
        }
        return value;
    }

    private static byte[] crcUnsigned(byte[] str, String crcKey) {
        CRC32 crc = new CRC32();
        crc.update(str);
        crc.update(crcKey.getBytes());

        long value = crc.getValue();
        if (value < 0) {
            value = 0xFFFFFFFF & value;
        }
        return longToByteArray(value);
    }
    
    private static void xorCode(byte[] data, String key) {
        for (int i = 0, j = 0; i < data.length; i++) {
            byte d = data[i];
            char k = key.charAt(j);
            byte tmp = (byte) (d ^ k);
            data[i] = tmp;
            j++;
            if (j >= key.length()) {
                j = 0;
            }
        }
    }
}
