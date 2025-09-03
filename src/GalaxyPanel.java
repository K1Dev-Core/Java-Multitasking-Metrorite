import java.awt.*;
import javax.swing.*;

public class GalaxyPanel extends JPanel {
    public GalaxyPanel() {
        setPreferredSize(new Dimension(Config.W, Config.H));
        new Timer(Config.FPS_MS, e -> repaint()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        for (Asteroid a : Galaxy.list) {
            if (!a.alive) continue;
            g.setColor(a.color);
            g.fillOval((int)(a.x - a.r), (int)(a.y - a.r), a.r * 2, a.r * 2);
        }
        g.setColor(Color.white);
        g.drawString("Alive: " + Galaxy.list.size(), 10, 20);
    }
}
