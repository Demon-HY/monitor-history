package monitor.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

public class Random {
    
    public static String uuidStr() {
        long uuid = UUID.randomUUID().getMostSignificantBits();
        byte[] uuidBytes = ByteBuffer.allocate(8).putLong(uuid).array();
        return Base64.encodeBase64URLSafeString(uuidBytes);     
    }
    
}
