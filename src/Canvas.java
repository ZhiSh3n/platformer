import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import java.awt.RenderingHints;
import java.util.*;


// TODO now we need to fix when we are jumping from below underneat obstacle

/**
 * Created by zhi on 7/16/17.
 */
class Canvas extends JComponent {

    // initialize user information
    public static int userXC = 300;
    public static int userYC = 200;
    public static int userWidth = 50;
    public static int userHeight = 50;

    // initialize ground information
    public static int groundXA = 0;
    public static int groundXB = 800;
    public static int groundYA = 500;
    public static int groundYB = 500;

    // initialize gravity information
    public static double accelerationDueToGravity = 0.1;
    public static boolean gravityOn = true;

    // initialize delta timing information
    public static double beatPeriod = 9;
    public static double lastTime = 0;

    // velocity arrays
    public static double YVelocity = 0;
    public static double XVelocity = 0;

    // jumping
    public static boolean currentlyJumping = false;


    // arraylist of squares
    ArrayList<Square> squareList = new ArrayList<Square>();

    public Canvas() {
        // generate obstacles
        squareList.add(new Square(400, 370, 50, 50));

        // start animation thread
        Thread animationThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    // delta timing is more accurate than Thread.sleep
                    double now = System.currentTimeMillis();
                    if (now - lastTime > beatPeriod) {
                        lastTime = now;
                        repaint();
                    }
                }
            }
        });
        animationThread.start();
    }

    public void paintComponent(Graphics g) {
        Graphics2D brush = (Graphics2D) g;

        // rendering hints
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP
        );
        brush.setRenderingHints(rh);

        // draw the text
        brush.drawString("Y-Velocity: " + YVelocity, 10, 20);
        brush.drawString("Squares: " + squareList.size(), 10, 35);
        brush.drawString("Jumping: " + currentlyJumping, 10, 50);
        brush.drawString("Y-Coordinate: " + userYC, 10, 65);
        brush.drawString("X-Coordinate: " + userXC, 10, 80);
        brush.drawString("Intruding: " + intruding, 10, 95);
        brush.drawString("Square X, Y : " + squareList.get(0).x + ", " + squareList.get(0).y, 10, 110);

        // set brush color
        brush.setColor(Color.BLACK);

        // draw the ground
        brush.drawLine(groundXA, groundYA, groundXB, groundYB);

        // list of validation methods
        // if the box is below the ground line, set it to the ground line
        actVelocity();
        noContact();
        gravity();

        isGravityOn(brush);

        // draw the user
        brush.drawRect(userXC, userYC, userWidth, userHeight);


        for (int i = 0; i < squareList.size(); i++) {
            squareList.get(i).draw(brush);
        }

        // movement
        for (int i = 0 ; i < Mover.keyList.size(); i++) {
            switch (Mover.keyList.get(i)) {
                case KeyEvent.VK_W:
                    userYC -= 2;
                    checkIntruding();
                    if (intruding) {
                        userYC += 2;
                    }
                    intruding = false;
                    break;
                case KeyEvent.VK_A:
                    userXC -= 2;
                    checkIntruding();
                    if (intruding) {
                        userXC += 2;
                    }
                    intruding = false;
                    break;
                case KeyEvent.VK_S:
                    userYC += 2;
                    checkIntruding();
                    if (intruding) {
                        userYC -= 2;
                    }
                    intruding = false;
                    break;
                case KeyEvent.VK_D:
                    userXC += 2;
                    checkIntruding();
                    if (intruding) {
                        userXC -= 2;
                    }
                    intruding = false;
                    break;
                case KeyEvent.VK_SPACE:
                    jump();
                    break;
            }
        }
        jumping();
    }

    public static boolean intruding = false;
    public static Square intruder;

    public void checkIntruding() {
        // if the user is on top of any object, gravity is also off
        for (int i = 0; i < squareList.size(); i++) {

            if ((userYC > (squareList.get(i).y - 50) && userYC < (squareList.get(i).y + 50) && userXC > (squareList.get(i).x - 50) && userXC < (squareList.get(i).x + 50))) {
                intruding = true;
                intruder = squareList.get(i);
            }
        }
    }



    public void isGravityOn(Graphics2D brush) { // TODO brush is only for debugging, remove when done
        // if the user hit the ground, gravity is OFF
        if ((userYC >= (groundYA - 50)) || (userYC >= (groundYB - 50))) {
            gravityOn = false;
            //currentlyJumping = false;
        } else if ((userYC < (groundYA - 50)) || (userYC < (groundYB - 50))) { // if the user is not yet at the ground...
            gravityOn = true;
        }
    }


    public void gravity() {
        // if user hit the ground
        if (gravityOn == true) {
           YVelocity += accelerationDueToGravity;
        }
        if (gravityOn == false) {
            YVelocity = 0;
            currentlyJumping = false;
            jumpRate = 5;
        }
    }

    public void actVelocity() {
        userYC += (int) YVelocity;
        checkIntruding();
        if (intruding) {
            userYC -= YVelocity;
            userYC = intruder.y - 50;
            gravityOn = false;
        }
        intruding = false;
    }

    // don't allow the user to cross the ground line
    public void noContact() {
        if ((userYC >= (groundYA - 50)) || (userYC >= (groundYB - 50))) {
            userYC = (groundYA - 50);
            currentlyJumping = false;
        }
    }

    public void jump() {
        if (currentlyJumping == false) {
            currentlyJumping = true;
        }
    }

    public static int jumpRate = 5;

    public void jumping() {
        if (currentlyJumping == true) {
            userYC -= jumpRate;
            checkIntruding();
            if (intruding) {
                userYC += jumpRate;
                jumpRate = 0;
                //currentlyJumping = false;
            }
            intruding = false;
        }
    }
}
