import javax.swing.*;

public class App {
    public static void main(String[] args) {
        String mode = !Config.debug ? " " : "DevMode";
        String input = JOptionPane.showInputDialog("Metrorite Amount: ( "+mode+" )");
        int count = Config.rockCount;
        try { count = Integer.parseInt(input); } catch (Exception ignored) {}
        Config.rockCount = count;

        World w = new World();
        w.init();
        JFrame f = new JFrame("Meteor Game ( "+mode+" )");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        View view = new View(w);
        f.setResizable(false);
        f.add(view);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                Sound.shutdown();
                System.exit(0);
            }
        });
    }
}
