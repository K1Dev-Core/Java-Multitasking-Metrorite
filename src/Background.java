import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Background {
    private static final BufferedImage background;
    private static double offsetX = 0;
    private static double offsetY = 0;
    private static final double SCROLL_SPEED = 0.2;
    private static BufferedImage blendedBackground;
    
    static {
        try {
            BufferedImage originalBg = ImageIO.read(new File("./res/background/spr_background_space.png"));
            

            int w = originalBg.getWidth();
            int h = originalBg.getHeight();
            blendedBackground = new BufferedImage(w * 2, h * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = blendedBackground.createGraphics();
            
            // วาดภาพ 2x2 แบบซ้อนทับกัน
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 2; x++) {
                    g2d.drawImage(originalBg, x * w, y * h, null);
                }
            }
            

            int blendSize = 60;
            float[] scales = new float[blendSize];
            for (int i = 0; i < blendSize; i++) {
                scales[i] = (float)Math.sin((i / (float)blendSize) * Math.PI/2);
            }
            

            for (int y = 0; y < h * 2; y++) {
                for (int i = 0; i < blendSize; i++) {
                    float alpha = scales[i];
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.drawImage(originalBg, 
                        w - blendSize + i, y, w - blendSize + i + 1, y + 1,
                        0, y % h, 1, (y % h) + 1, null);
                }
            }
            

            for (int x = 0; x < w * 2; x++) {
                for (int i = 0; i < blendSize; i++) {
                    float alpha = scales[i];
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.drawImage(originalBg,
                        x, h - blendSize + i, x + 1, h - blendSize + i + 1,
                        x % w, 0, (x % w) + 1, 1, null);
                }
            }
            
            g2d.dispose();
            background = blendedBackground;
            Debug.log("Loaded and blended background successfully");
        } catch (IOException e) {
            Debug.log("Error loading background: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void update() {
        offsetX += SCROLL_SPEED;
        offsetY += SCROLL_SPEED;
        
        if (offsetX >= background.getWidth() / 2) {
            offsetX -= background.getWidth() / 2;
        }
        if (offsetY >= background.getHeight() / 2) {
            offsetY -= background.getHeight() / 2;
        }
    }

    public static void draw(Graphics g, int width, int height) {
        int bgWidth = background.getWidth() / 2;
        int bgHeight = background.getHeight() / 2;
        

        int startX = (int)offsetX - bgWidth;
        int startY = (int)offsetY - bgHeight;

        g.drawImage(background, 
            startX, startY, startX + bgWidth * 2, startY + bgHeight * 2,
            0, 0, background.getWidth(), background.getHeight(),
            null);
    }
}