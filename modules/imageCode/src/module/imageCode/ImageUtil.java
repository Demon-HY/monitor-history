package module.imageCode;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtil {

    public static void cutImage(InputStream in, OutputStream out, int x, int y, int w, int h) throws IOException {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = iterator.next();
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        int width = reader.getWidth(0);
        w = w + x > width ? width - x : w;
        int height = reader.getHeight(0);
        h = h + y > height ? height - y : h;
        Rectangle rect = new Rectangle(x, y, w, h);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, "jpg", out);
    }
}
