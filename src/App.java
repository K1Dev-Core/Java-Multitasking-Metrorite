import javax.swing.*;

public class App {
    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog("Metrorite Amount:");
        int amount = Config.rockAmount;
        try { amount = Integer.parseInt(input); } catch (Exception ignored) {}
        Config.rockAmount = amount;

        World w = new World();
        w.init();
        JFrame f = new JFrame("Meteor Game");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        View view = new View(w);
    
        f.add(view);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
