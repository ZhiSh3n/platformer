import javax.swing.*;

/**
 * Created by zhi on 7/16/17.
 */

public class Run extends JPanel {

    // create static variables for frame dimensions because they need to be used in Canvas
    public static int frameWidth = 800;
    public static int frameHeight = 600;

    public static void main(String[] args) {
        // make a new JFrame
        JFrame frame = new JFrame("platformer");
        frame.setSize(frameWidth, frameHeight);

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
