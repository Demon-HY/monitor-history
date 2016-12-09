package monitor.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONObject;

import monitor.exception.LogicalException;
import monitor.service.http.protocol.RetStat;

public class XCodeUtil {

    public synchronized static byte[] loadBytes(String name) throws IOException {
        FileInputStream in = null;
        in = new FileInputStream(name);
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int ch;
            while ((ch = in.read()) != -1) {
                buffer.write(ch);
            }
            return buffer.toByteArray();
        } finally {
            in.close();
        }
    }

    public synchronized static String getMD5(String source) {
        return DigestUtils.md5Hex(source);
    }

    public synchronized static String getMD5(File file) throws IOException {

        FileInputStream is = new FileInputStream(file);
        return getMD5(is);
    }

    public synchronized static String getMD5(InputStream is) throws IOException {
        try {
            return DigestUtils.md5Hex(is);
        } finally {
            is.close();
        }
    }
    
    public static String getSHA256(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        return getSHA256(is);
    }

    public static String getSHA256(InputStream is) throws IOException {
        try {
            return DigestUtils.sha256Hex(is);
        } finally {
            is.close();
        }
    }

    public static String getSHA256(String data) throws Exception {
        return DigestUtils.sha256Hex(data);
    }
    
    public static String getSHA256(byte[] data) throws Exception {
        return DigestUtils.sha256Hex(data);
    }
    
    public static String getSHA1(String data) {
    	return DigestUtils.sha1Hex(data);
    }
    
    public static String getSHA1(InputStream is) throws IOException {
    	try {
            return DigestUtils.sha1Hex(is);
        } finally {
            is.close();
        }
    }
    
    public static String getSHA1(byte[] data) {
    	return DigestUtils.sha1Hex(data);
    }

    public static String base16ToBase64(String str) {
        int length = str.length() / 2;
        byte[] data = new byte[length];

        for (int i = 0; i < length; i++) {
            String tmp = str.substring(i * 2, (i + 1) * 2);
            int c = Integer.parseInt(tmp, 16);
            data[i] = (byte) c;
        }

        byte[] code = Base64.encodeBase64(data);

        return new String(code);
    }

    public static String base64ToBase16(String str) {
        
        byte[] data = Base64.decodeBase64(str);
        int length = data.length;
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < length; i++) {
            
            byte d = data[i];
            int dd = 0x000000ff & d;
            String s = Integer.toHexString(dd);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s);
        }

        return sb.toString();
    }
    
    public static String sha1UrlSafeB64(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(bytes);
            return Base64.encodeBase64URLSafeString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String hmacSha1(String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();           
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            return Base64.encodeBase64URLSafeString(rawHmac);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String xEncode(Map<String, Object> params) {
        String info = JSONObject.toJSONString(params);

        byte[] data = info.getBytes();
        xorCode(data, XOR_KEY);
        byte[] crc = crcUnsigned(data, CRC_KEY.getBytes());
        byte[] tmp = new byte[data.length + crc.length];
        for (int i = 0; i < data.length; i++) {
            tmp[i] = data[i];
        }
        for (int i = 0; i < crc.length; i++) {
            tmp[i + data.length] = crc[i];
        }
        data = tmp;

        String xcode = createPrefix(PREFIX_LENGTH)
                + Base64.encodeBase64URLSafeString(data);
        return xcode;
    }
    
    /**
     * 1、移除xcode字符串前端的干扰串
     * 2、将剩下有效字符串进行base64解码
     * 3、解码后得到的字节数组包含数据段和CRC校验字段，CRC校验字段在字节数组的尾部，占8字节
     * 4、进行CRC校验
     * 5、对数据段进行异或解码，获取最终的可读数据：JSON字符串
     * 
     * @param xCode
     * @return
     * @throws LogicalException 
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> xDecode(String xCode) throws LogicalException {
        // 1
        String real = xCode.substring(PREFIX_LENGTH);
        byte[] rst = Base64.decodeBase64(real);

        byte[] data = Arrays.copyOf(rst, rst.length - (Long.SIZE / Byte.SIZE));
        byte[] crc = Arrays.copyOfRange(rst, data.length, rst.length);
        // 4
        long value = byteArrayToLong(crc);
        byte[] realCrc = crcUnsigned(data, CRC_KEY.getBytes());
        long realValue = byteArrayToLong(realCrc);
        if (!(value == realValue)) {
            System.out.println("license verify failed.");
            throw new LogicalException(RetStat.ERR_BAD_PARAMS, null);
        }

        xorCode(data, XOR_KEY);

        String info = new String(data);

        return JSONObject.parseObject(info, Map.class);
    }
    
    private static final int PREFIX_LENGTH = 3;
    private static final String XOR_KEY = "Pp0cH6sBQZA";
    private static final String CRC_KEY = "MQP0TGVX0KFBX5F6";
    private static final String digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXWZ";
    public static String createPrefix(int prefixLength) {
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

    public static void xorCode(byte[] data, String key) {
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

    public static byte[] crcUnsigned(byte[] str, byte[] sign) {
        CRC32 crc = new CRC32();
        crc.update(str);
        crc.update(sign);

        long value = crc.getValue();
        if (value < 0) {
            value = 0xFFFFFFFF & value;
        }
        return longToByteArray(value);
    }

    public static byte[] longToByteArray(long value) {
        ByteBuffer bb = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(value);
        return bb.array();
    }

    // TODO 这种轮子尽量找已经造好的？
    // https://www.google.com/search?q=byte+array+to+long+java
    public static long byteArrayToLong(byte[] data) {
        long value = 0;
        for (int i = 0; i < data.length; i++) {
            long tmp = 0x000000ff & data[i];

            value += tmp << (8 * i);
        }
        return value;
    }
}