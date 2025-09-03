import java.awt.*;
import java.util.Random;

public class Asteroid implements Runnable {
    public int id;
    public volatile boolean alive = true;
    public double x, y, dx, dy;
    public int r;
    public Color color;
    private final Random rand = new Random();

    public Asteroid(int id, double x, double y, double dx, double dy, int r, Color c) {
        this.id = id; this.x = x; this.y = y; this.dx = dx; this.dy = dy; this.r = r; this.color = c;
    }

    public double speed() { return Math.hypot(dx, dy); }

    private void clampSpeed() {
        double s = speed();
        if (s > Config.MAX_SPEED) {
            dx = dx * Config.MAX_SPEED / s;
            dy = dy * Config.MAX_SPEED / s;
        }
    }

    private void bounceIfEdge() {
        boolean b = false;
        if (x - r < 0 && dx < 0) { dx = -dx; b = true; }
        if (x + r > Config.W && dx > 0) { dx = -dx; b = true; }
        if (y - r < 0 && dy < 0) { dy = -dy; b = true; }
        if (y + r > Config.H && dy > 0) { dy = -dy; b = true; }
        if (b) {
            dx *= Config.SPEED_UP;
            dy *= Config.SPEED_UP;
            clampSpeed();
        }
    }

    private void checkHit() {
        for (Asteroid o : Galaxy.list) {
            if (o == this || !o.alive || !alive) continue;
            double d = Math.hypot(x - o.x, y - o.y);
            if (d <= r + o.r) {
                synchronized (Galaxy.hitLock) {
                    if (!alive || !o.alive) return;
                    double s1 = this.speed();
                    double s2 = o.speed();
                    if (Math.abs(s1 - s2) < 1e-6) {
                        if (this.id > o.id) this.alive = false; else o.alive = false;
                    } else {
                        if (s1 < s2) this.alive = false; else o.alive = false;
                    }
                }
            }
        }
        if (!alive) Galaxy.remove(this);
    }

    @Override
    public void run() {
        while (alive) {
            x += dx; y += dy;
            bounceIfEdge();
            checkHit();
            try { Thread.sleep(Config.TICK_MS); } catch (InterruptedException e) { break; }
        }
    }
}
