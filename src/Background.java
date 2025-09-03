import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Background {
    private static final BufferedImage background;
    
    static {
        try {
            background = ImageIO.read(new File("./res/background/spr_background_space.png"));
            Debug.log("Loaded background image successfully");
        } catch (IOException e) {
            Debug.log("Error loading background: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void draw(Graphics g, int width, int height) {
        g.drawImage(background, 0, 0, width, height, null);
    }
}
