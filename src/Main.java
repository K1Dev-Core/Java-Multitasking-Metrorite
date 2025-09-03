import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        JFrame f = new JFrame("Meteor Threads");
        GalaxyPanel p = new GalaxyPanel();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(p);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        App.start();
    }

}