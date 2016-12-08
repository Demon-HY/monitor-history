package module.imageCode;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public final class Watermark {
    
    public static final int DEFAULT_FONT_SIZE = 60;
    public static final float DEFAULT_ALPHA = 0.3f;
    public static final String DEFAULT_FONT_NAME = "宋体";
    
    /**
     * 添加文字水印
     * 
     * @param image 目标图片
     * @param pressText 水印文字， 如：我是水印
     * @param fontName 字体名称， 如：宋体
     * @param fontStyle 字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
     * @param fontSize 字体大小，单位为像素
     * @param color 字体颜色
     * @param x 水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
     * @param y 水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha 透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     */
    public static BufferedImage pressText(Image image, String pressText, String fontName, int fontStyle, 
    		int fontSize, Color color, Integer x, Integer y, float alpha) {
    	
        int width = image.getWidth(null), height = image.getHeight(null);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.setFont(new Font(fontName, fontStyle, fontSize));
        g.setColor(color);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

        int textWidth = fontSize * getLength(pressText), textHeight = fontSize;
        int widthDiff = width - textWidth, heightDiff = height - textHeight;

        if (null == x || y == null) {
            int xInc = 200, yInc = 300;
            int yOffset = 0, row = 0;
            do {
                int xOffset = 0 - row * (xInc / 2);
                do {
                    if (xOffset + textWidth >= 0) {
                        g.drawString(pressText, xOffset, yOffset + textHeight);
                    }
                    xOffset += textWidth + xInc;
                } while (xOffset <= width);
                yOffset += yInc;
                row++;
            } while (yOffset <= height);
        } else {
            x = x < 0 || x > widthDiff ? 0 : x;
            y = y < 0 || y > heightDiff ? 0 : y;
            g.drawString(pressText, x, y + textHeight);
        }
        g.dispose();
        return bufferedImage;
    }

    /**
     * 获取字符长度,一个汉字作为 1 个字符,一个英文字母作为 0.5个字符
     * 
     * @param text
     * @return 字符长度,如:text="中国",返回 2;text="test",返回 2;text="中国ABC",返回 4.
     */
    public static int getLength(String text) {
        int textLength = text.length();
        int length = textLength;
        for (int i = 0; i < textLength; i++) {
            if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
                length++;
            }
        }
        return (length % 2 == 0) ? length / 2 : length / 2 + 1;
    }

//    public static void main(String[] args) throws IOException {
//        File file = new File("E://Jellyfish.jpg");
//        Image image = ImageIO.read(file);
//        BufferedImage bufferedImage = pressText(image, "我是水印", "宋体", Font.BOLD | Font.ITALIC, 60, Color.BLACK, 300, 500, 0.3f);
//        ImageIO.write(bufferedImage, "jpg", file);
//    }
}