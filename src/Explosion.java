import java.awt.*;

public class Explosion {
    private int x, y;
    private int frame;
    private final int size;
    private boolean isFinished;
    
    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 40; // ขนาดการระเบิด
        this.frame = 0;
        this.isFinished = false;
        Sound.playExplosion();
    }
    
    public void update() {
        if (!isFinished) {
            frame++;
            if (frame >= ExplosionSprite.TOTAL_FRAMES) {
                isFinished = true;
            }
        }
    }
    
    public void draw(Graphics g) {
        ExplosionSprite.drawFrame(g, frame, x, y, size*4);
    }
    
    public boolean isFinished() {
        return isFinished;
    }

    public boolean hits(Rock rock) {
        double dx = x - rock.posX;
        double dy = y - rock.posY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= size + rock.size + 1.5;
    }
}
