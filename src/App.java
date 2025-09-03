import java.awt.*;
import java.util.Random;

public class App {

    static void start() {
        Random r = new Random();
        for (int i = 0; i < Config.N; i++) {
            int rr = r.nextInt(Config.R_MAX - Config.R_MIN + 1) + Config.R_MIN;
            double x = r.nextInt(Math.max(1, Config.W - 2 * rr)) + rr;
            double y = r.nextInt(Math.max(1, Config.H - 2 * rr)) + rr;
            double ang = r.nextDouble() * Math.PI * 2;
            double sp = Config.V_MIN + r.nextDouble() * (Config.V_MAX - Config.V_MIN) + i * 0.03;
            double dx = Math.cos(ang) * sp;
            double dy = Math.sin(ang) * sp;
            Color c = new Color(r.nextInt(180) + 50, r.nextInt(180) + 50, r.nextInt(180) + 50);
            Asteroid a = new Asteroid(i, x, y, dx, dy, rr, c);
            Galaxy.add(a);
            new Thread(a, "A-" + i).start();
        }
    }
}
