import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpriteSheet {
    private static final BufferedImage[] sheets = new BufferedImage[Config.spriteCount];
    private static final int W = 32;
    private static final int H = 32;
    private static final int TOTAL_FRAMES = 3;
    
    static {
        for (int i = 0; i < Config.spriteCount; i++) {
            try {
                sheets[i] = ImageIO.read(new File("./res/meteor/" + (i+1) + "-sprite-sheet.png"));
            } catch (IOException e) {
                Debug.log("Error loading sprite sheet " + (i+1) + ": " + e.getMessage());
            }
        }
    }

    public static void drawFrame(Graphics g, int type, int frame, int x, int y, int size) {
        frame = frame % TOTAL_FRAMES;
        int srcX = frame * (W + 1);  
        int srcY = 0;
        
        g.drawImage(sheets[type],
            x - size, y - size, x + size, y + size,
            srcX, srcY, srcX + W, srcY + H,
            null
        );
    }
}