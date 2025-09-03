import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ExplosionSprite {
    private static final BufferedImage spritesheet;
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 400;
    private static final int FRAMES_PER_ROW = 6;
    private static final int TOTAL_FRAMES = 30;
    
    static {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("./res/explosion/fireballs_explosion.png"));
        } catch (IOException e) {
            Debug.log("Error loading explosion sprite: " + e.getMessage());
        }
        spritesheet = img;
    }

    public static void drawFrame(Graphics g, int frame, int x, int y, int size) {
        frame = frame % TOTAL_FRAMES;
        int row = frame / FRAMES_PER_ROW;
        int col = frame % FRAMES_PER_ROW;
        int srcX = col * FRAME_WIDTH + col;
        int srcY = row * FRAME_HEIGHT + row;
        
        g.drawImage(spritesheet,
            x - size, y - size, x + size, y + size,
            srcX, srcY, srcX + FRAME_WIDTH, srcY + FRAME_HEIGHT,
            null
        );
    }
}
