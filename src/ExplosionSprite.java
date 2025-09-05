import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ExplosionSprite {
    private static final BufferedImage sheet;
    private static final int W = 400;
    private static final int H = 400;
    private static final int PER_ROW = 6;
    public static final int TOTAL_FRAMES = 30;
    
    static {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("./res/explosion/fireballs_explosion.png"));
        } catch (IOException e) {
            Debug.log("Error loading explosion sprite: " + e.getMessage());
        }
        sheet = img;
    }

    public static void drawFrame(Graphics g, int frame, int x, int y, int size) {
        if (frame >= TOTAL_FRAMES) return;
        int row = frame / PER_ROW;
        int col = frame % PER_ROW;
        int srcX = col * W + col;
        int srcY = row * H + row;
        
        g.drawImage(sheet,
            x - size, y - size, x + size, y + size,
            srcX, srcY, srcX + W, srcY + H,
            null
        );
    }
}
