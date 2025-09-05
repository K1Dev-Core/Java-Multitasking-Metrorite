import java.awt.*;

public class Explosion {
    private final int x, y;
    private int frame;
    private final int size;
    private boolean done;
    
    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 40;
        this.frame = 0;
        this.done = false;
        Sound.playExplosion();
    }
    
    public void update() {
        if (!done) {
            frame++;
            if (frame >= ExplosionSprite.TOTAL_FRAMES) {
                done = true;
            }
        }
    }
    
    public void draw(Graphics g) {
        ExplosionSprite.drawFrame(g, frame, x, y, size*4);
    }
    
    public boolean isFinished() {
        return done;
    }

    public boolean hits(Rock rock) {
        double dx = x - rock.posX;
        double dy = y - rock.posY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist <= size + rock.size + 1.5;
    }
}
