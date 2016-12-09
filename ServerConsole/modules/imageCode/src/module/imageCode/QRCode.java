package module.imageCode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
 * 使用ZXing3.0
 */
public final class QRCode {

    private static final String CHARSET = "utf-8";
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 生成矩阵，是一个简单的函数，参数固定，更多的是使用示范。
     * 
     * @param text
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static void buildQRCode(String text, int width, int height, OutputStream stream) throws WriterException,
            IOException {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        // 二维码的图片格式
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = toBufferedImage(bitMatrix);
        ImageIO.write(image, "png", stream);
    }

    /**
     * 根据点矩阵生成黑白图
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        
        int min = width > height ? height : width;
        int offset = (int) (min * 0.1);
        
        BufferedImage image = new BufferedImage(width - offset * 2, height - offset * 2, BufferedImage.TYPE_INT_RGB);
        for (int x = offset; x < width - offset; x++) {
            for (int y = offset; y < height - offset; y++) {
                image.setRGB(x - offset, y - offset, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * 解码
     * 
     * @param is
     * @return
     */  
    public static String decode(InputStream is) throws NotFoundException, IOException {

        BufferedImage image = ImageIO.read(is);

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result;

        // 解码设置编码方式为：utf-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);

        result = new MultiFormatReader().decode(bitmap, hints);

        return result.getText();

    }
}