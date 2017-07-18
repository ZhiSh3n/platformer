import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import java.awt.RenderingHints;
import java.util.*;

// TODO now replace 50 with user width to make it fully scalable

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

    public static boolean intruding = false;
    public static Square intruder;

    public static int jumpRate = 5;

    public static boolean amOnObstacle = false;


    // arraylist of squares
    ArrayList<Square> squareList = new ArrayList<Square>();

    public Canvas() {
        // TODO generate obstacles here
        squareList.add(new Square(400, 370, 50, 50));
        squareList.add(new Square(350, 330, 50, 50));
        squareList.add(new Square(150, 330, 100, 50));
        squareList.add(new Square(510, 330, 50, 100));

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
        // Y Velocity fluctuates when you are on an obstacle because gravity is set to off when you are intruding
        // however you are then placed right on top of the obstacle which is NOT intruding. while you are not
        // intruding, your y velocity will increase and make you intrude. this is why the y velocity will fluctuate
        // however there is no real impact in-game
        brush.drawString("Squares: " + squareList.size(), 10, 35);
        brush.drawString("Jumping: " + currentlyJumping, 10, 50);
        brush.drawString("Y-Coordinate: " + userYC, 10, 65);
        brush.drawString("X-Coordinate: " + userXC, 10, 80);
        brush.drawString("Intruding: " + intruding, 10, 95);
        brush.drawString("On Obstacle? " + amOnObstacle, 10, 110);
        brush.drawString("Gravity? " + gravityOn, 10, 125);

        // set brush color
        brush.setColor(Color.BLACK);

        // draw the ground
        brush.drawLine(groundXA, groundYA, groundXB, groundYB);

        // add YVelocity to userYC
        // if intrude, put user atop obstacle + turn gravity off + turn jumpRate on
        actVelocity();

        // if user is in the ground, put it atop ground + turn currentlyJumping off
        noContact();

        // if gravity is on, add acceleration to user's YVelocity component
        // if gravity is off,  turn jumping off + turn on the jump rate + set user's YVelocity to 0
        gravity();

        // check if on obstacle
        //checkOnObstacle();

        // if the user is in the air, then set gravity to on
        // if user is on the ground, set gravity to off
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
        // if jumping intrudes an obstacle, turn jumpRate to 0
        // jumprate will be turned back on once it hits the ground
        jumping();
    }

    public void checkIntruding() {
        // if the user is on top of any object, gravity is also off
        for (int i = 0; i < squareList.size(); i++) {
            if ((userYC > (squareList.get(i).yc - 50)) && (userYC < (squareList.get(i).yc + squareList.get(i).hd)) && (userXC > (squareList.get(i).xc - 50)) && (userXC < (squareList.get(i).xc + squareList.get(i).wd))) {
                intruding = true;
                intruder = squareList.get(i);
            }
        }
    }

    public void actVelocity() { // TODO
        userYC += (int) YVelocity; // add the gravity velocity
        checkIntruding(); // are we intruding?
        if (intruding == true) { // if we are...
            userYC -= YVelocity; // rescind the change in y coordinate
            userYC = intruder.yc - 50; // put the user on top of the obstacle
            gravityOn = false; // gravity is OFF
            jumpRate = 5;
        }
        amOnObstacle = false;
        for (int i = 0; i < squareList.size(); i++) {
            if ((userYC == (squareList.get(i).yc - 50)) && ((userXC > (squareList.get(i).xc - 50)) && (userXC < (squareList.get(i).xc + squareList.get(i).wd)))) { // if we are on the same Y and we are within an X range...
                amOnObstacle = true;
            }
        }
        intruding = false;
    }

    public void gravity() {
        // if user hit the ground
        if (gravityOn == true) {
            YVelocity += accelerationDueToGravity;
        }
        if ((gravityOn == false) ) {
            YVelocity = 0;
            currentlyJumping = false;
            jumpRate = 5;
        }
    }

    public void isGravityOn(Graphics2D brush) { // TODO brush is only for debugging, remove when done
        // if the user hit the ground, gravity is OFF
        if (userYC >= (groundYA - 50)) {
            gravityOn = false;
            //currentlyJumping = false;
        }
        if ((userYC < (groundYA - 50)) && !amOnObstacle) { // if the user is not yet at the ground...
            gravityOn = true;
        }
    }






    public void jump() {
        if (currentlyJumping == false) {
            currentlyJumping = true;
        }
    }

    public void jumping() {
        if (currentlyJumping == true) {
            userYC -= jumpRate;
            checkIntruding();
            if (intruding) {
                userYC += jumpRate;
                jumpRate = 0;
                //currentlyJumping = false; we don't do this because it allows us to jump infinitely after hitting the bottom of an obstacle
            }
            intruding = false;
        }
    }
    // don't allow the user to cross the ground line
    public void noContact() {
        if ((userYC > (groundYA - 50)) || (userYC > (groundYB - 50))) {
            userYC = (groundYA - 50);
            currentlyJumping = false;
        }
    }
}
