import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpriteSheet {
    private static final BufferedImage[] spriteSheets = new BufferedImage[Config.meteorSpriteSheetMax];
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;

    private static final int TOTAL_FRAMES = 3;
    
    static {
        for (int i = 0; i < Config.meteorSpriteSheetMax; i++) {
            try {
                spriteSheets[i] = ImageIO.read(new File("./res/meteor/" + (i+1) + "-sprite-sheet.png"));
            } catch (IOException e) {
                Debug.log("Error loading sprite sheet " + (i+1) + ": " + e.getMessage());
            }
        }
    }

    public static void drawFrame(Graphics g, int spriteType, int frame, int x, int y, int size) {
        frame = frame % TOTAL_FRAMES;
        int srcX = frame * (FRAME_WIDTH + 1);  
        int srcY = 0;
        
        g.drawImage(spriteSheets[spriteType],
            x - size, y - size, x + size, y + size,
            srcX, srcY, srcX + FRAME_WIDTH, srcY + FRAME_HEIGHT,
            null
        );
    }
}