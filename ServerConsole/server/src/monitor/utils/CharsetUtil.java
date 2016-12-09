package monitor.utils;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

public class CharsetUtil {

    public static String getCharset(byte[] data) {
        return new CharsetDetector().geestFileEncoding(data, new nsDetector());
    }
}

class CharsetDetector {
    private boolean found = false;
    private String encoding = null;

    public String geestFileEncoding(byte[] data, nsDetector det) {
        det.Init(new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                found = true;
                encoding = charset;
            }
        });

        byte[] buf = data;
        int len = data.length;

        boolean isAscii = det.isAscii(buf, len);

        if (!isAscii) {
            det.DoIt(buf, len, false);
        }
        det.DataEnd();

        if (isAscii) {
            encoding = "ASCII";
            found = true;
        }

        if (!found) {
            String prob[] = det.getProbableCharsets();
            if (prob.length > 0) {
                encoding = prob[0];
            } else {
                return null;
            }
        }
        return encoding;
    }
}
