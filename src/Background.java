import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Background {
    private static final BufferedImage bg;
    private static double x = 0;
    private static double y = 0;
    private static final double SPEED = 0.2;
    private static BufferedImage blended;
    
    static {
        try {
            BufferedImage orig = ImageIO.read(new File("./res/background/spr_background_space.png"));
            
            int w = orig.getWidth();
            int h = orig.getHeight();
            blended = new BufferedImage(w * 2, h * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = blended.createGraphics();
            
            for (int yy = 0; yy < 2; yy++) {
                for (int xx = 0; xx < 2; xx++) {
                    g2d.drawImage(orig, xx * w, yy * h, null);
                }
            }
            
            int blendSize = 100;
            float[] scales = new float[blendSize];
            for (int i = 0; i < blendSize; i++) {
                scales[i] = (float)Math.sin((i / (float)blendSize) * Math.PI/2);
            }
            
            for (int yy = 0; yy < h * 2; yy++) {
                for (int i = 0; i < blendSize; i++) {
                    float alpha = scales[i];
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.drawImage(orig, 
                        w - blendSize + i, yy, w - blendSize + i + 1, yy + 1,
                        0, yy % h, 1, (yy % h) + 1, null);
                }
            }
            
            for (int xx = 0; xx < w * 2; xx++) {
                for (int i = 0; i < blendSize; i++) {
                    float alpha = scales[i];
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.drawImage(orig,
                        xx, h - blendSize + i, xx + 1, h - blendSize + i + 1,
                        xx % w, 0, (xx % w) + 1, 1, null);
                }
            }
            
            g2d.dispose();
            bg = blended;
            Debug.log("Loaded and blended background successfully");
        } catch (IOException e) {
            Debug.log("Error loading background: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void update() {
        x += SPEED;
        y += SPEED;
        
        if (x >= bg.getWidth() / 2) {
            x -= bg.getWidth() / 2;
        }
        if (y >= bg.getHeight() / 2) {
            y -= bg.getHeight() / 2;
        }
    }

    public static void draw(Graphics g, int width, int height) {
        int bgW = bg.getWidth() / 2;
        int bgH = bg.getHeight() / 2;
        
        int startX = (int)x - bgW;
        int startY = (int)y - bgH;

        g.drawImage(bg, 
            startX, startY, startX + bgW * 2, startY + bgH * 2,
            0, 0, bg.getWidth(), bg.getHeight(),
            null);
    }
}