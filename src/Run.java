import javax.swing.*;
import java.awt.Dimension;

/**
 * Created by zhi on 7/16/17.
 */

public class Run extends JPanel {

    public static void main(String[] args) {
        // make a new JFrame
        JFrame frame = new JFrame("platformer");

        // TODO the frame is not actually 800x600, consider using JPanel...
        frame.setSize(800, 600);

        // define some variables for JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // add animation thread
        frame.add(new Canvas());
        frame.setVisible(true);

        // add keylistener
        frame.addKeyListener(new Mover());
    }

}
