import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import java.awt.RenderingHints;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// TODO look for TODO sections for potential bugs and important notes!

/**
 * Created by zhi on 7/16/17.
 */
class Canvas extends JComponent {

    // initialize user information
    public static int userXC = 350;
    public static int userYC = 350;
    public static int userWidth = 50;
    public static int userHeight = 50;

    // initialize ground information
    public static int groundXA = 0;
    public static int groundXB = 800;
    public static int groundYA = 400;
    public static int groundYB = 400;

    // initialize gravity information
    public static double accelerationDueToGravity = 0.1;
    public static boolean gravityOn = true;

    // initialize delta timing information
    public static double beatPeriod = 9;
    public static double lastTime = 0;

    // velocity arrays; X is not actually used
    public static double YVelocity = 0;
    public static double XVelocity = 0;

    // jumping information
    public static boolean currentlyJumping = false;
    public static int jumpRate = 5;

    // intruder information
    // used in collision prevention
    public static boolean intruding = false;
    public static Square intruder;

    // is the user standing on an obstacle?
    public static boolean amOnObstacle = false;

    // scrolling information
    public static boolean horizontalScrollTrue = false;
    public static boolean verticalScrollTrue = false;

    // debugging tools
    public static int frameCounter = 0;
    public static int[] XBuffer = new int[3];
    public static boolean horizontalBoolean = false;

    // arraylist of randomly generated squares
    ArrayList<Square> squareList = new ArrayList<Square>();

    // call this from Run.java to create a new canvas and initiate the animation thread
    public Canvas() {
        // randomly generate obstacles
        for (int i = 0; i < 200; i++) { // make 100 random obstacles
            int randomX = ThreadLocalRandom.current().nextInt(-1000, 1800);
            int randomY = ThreadLocalRandom.current().nextInt(-1000, 400);
            int randomW = ThreadLocalRandom.current().nextInt(25, 100 + 1);
            int randomH = ThreadLocalRandom.current().nextInt(25, 100 + 1);
            squareList.add(new Square(randomX, randomY, randomW, randomH));
        }

        // since the user spawns at 350,350, remove all the obstacles that may interfere with
        // the spawning position
        for (int i = 0; i < squareList.size(); i++) {
            if ((squareList.get(i).xc < 500) && (squareList.get(i).xc > 300) && (squareList.get(i).yc > 100) && (squareList.get(i).yc < 250)) {
                squareList.remove(i);
            }
        }

        // this currently commented out, but this would be how you
        // generate squares manually
        /*
        squareList.add(new Square(150, 230, 100, 50));
        squareList.add(new Square(510, 230, 50, 100));
        squareList.add(new Square(300, 150, 50, 100));
        squareList.add(new Square(510, 70, 50, 50));
        squareList.add(new Square(400, 230, 50, 50));
        squareList.add(new Square(350, 190, 50, 50));
        */

        // start animation thread
        Thread animationThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    // delta timing is more accurate than Thread.sleep
                    // i like setting the beatPeriod to 9, which equals roughly 100 frames per second
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

        // included some text for debugging
        brush.drawString("Y-Velocity: " + YVelocity, 10, 20);
        brush.drawString("Squares: " + squareList.size(), 10, 35);
        brush.drawString("Jumping: " + currentlyJumping, 10, 50);
        brush.drawString("Y-Coordinate: " + userYC, 10, 65);
        brush.drawString("X-Coordinate: " + userXC, 10, 80);
        brush.drawString("Intruding: " + intruding, 10, 95);
        brush.drawString("On Obstacle? " + amOnObstacle, 10, 110);
        brush.drawString("Gravity? " + gravityOn, 10, 125);
        brush.drawString("Frames: " + frameCounter, 10, 140);
        brush.drawString("JumpRate: " + jumpRate, 10, 155);
        brush.drawString("" + horizontalBoolean, 10, 170);
        //brush.drawString("" + ThreadLocalRandom.current().nextInt(0, 9 + 1), 10, 155);

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

        // if the user is in the air, then set gravity to on
        // if user is on the ground, set gravity to off
        isGravityOn(brush);

        // draw the user
        brush.drawRect(userXC, userYC, userWidth, userHeight);

        // the draw function for all the obstacles
        for (int i = 0; i < squareList.size(); i++) {
            if ((squareList.get(i).xc < Run.frameWidth * 0.98) && (squareList.get(i).xc > (Run.frameWidth * 0.02)) && (squareList.get(i).yc < (Run.frameHeight * 0.95)) && (squareList.get(i).yc > (Run.frameHeight * 0.05))) {
                squareList.get(i).draw(brush);
            }
        }

        // movement
        for (int i = 0; i < Mover.keyList.size(); i++) {
            switch (Mover.keyList.get(i)) {
                case KeyEvent.VK_W:
                    /* TODO disabled W functionality
                    userYC -= 2;
                    checkIntruding();
                    if (intruding) {
                        userYC += 2;
                    }
                    intruding = false;
                    checkScrollVertical();
                    if (verticalScrollTrue) {
                        userYC += 2;
                        shiftVertical(-2);
                        groundYA += 2;
                        groundYB += 2;
                    }
                    verticalScrollTrue = false;
                    */
                    break;
                case KeyEvent.VK_A:
                    userXC -= 2;
                    checkIntruding();
                    if (intruding) {
                        userXC += 2;
                        userXC = intruder.xc + intruder.wd;
                        horizontalBoolean = true;
                    }
                    intruding = false;
                    checkScrollHorizontal();
                    if (horizontalScrollTrue) {
                        userXC += 2;
                        shiftHorizontal(-2);
                    }
                    horizontalScrollTrue = false;
                    break;
                case KeyEvent.VK_S: // TODO disabled S functionality
                    /*
                    userYC += 2;
                    checkIntruding();
                    if (intruding) {
                        userYC -= 2;
                    }
                    intruding = false;
                    checkScrollVertical();
                    if (verticalScrollTrue) {
                        userYC -= 2;
                        shiftVertical(+2);
                        groundYA -= 2;
                        groundYB -= 2;
                    }
                    verticalScrollTrue = false;
                    */
                    break;
                case KeyEvent.VK_D:
                    userXC += 2;
                    checkIntruding();
                    if (intruding) {
                        userXC -= 2;
                        userXC = intruder.xc - 50;
                        horizontalBoolean = true;
                    }
                    intruding = false;
                    checkScrollHorizontal();
                    if (horizontalScrollTrue) {
                        userXC -= 2;
                        shiftHorizontal(+2);
                    }
                    horizontalScrollTrue = false;
                    break;
                case KeyEvent.VK_SPACE:
                    jump();
                    break;
            }
        }
        // if jumping intrudes an obstacle, turn jumpRate to 0
        // jumprate will be turned back on once it hits the ground
        XBuffer[0] = XBuffer[1];
        XBuffer[1] = XBuffer[2];
        XBuffer[2] = userXC;
        jumping();
    }

    public void checkScrollHorizontal() {
        // the in-frame box for X
        // if the user moves past this threshold, move the screen instead of the user
        if (userXC > (Run.frameWidth * 0.65)) {
            horizontalScrollTrue = true;
        }
        if (userXC < (Run.frameWidth * 0.35)) {
            horizontalScrollTrue = true;
        }
    }

    public void checkScrollVertical() {
        // the in-frame box for Y
        // if the user moves past this threshold, move the screen instead of the user
        if (userYC > (Run.frameHeight * 0.7)) {
            verticalScrollTrue = true;
        }
        if (userYC < (Run.frameHeight * 0.3)) {
            verticalScrollTrue = true;
        }
    }

    // method to shift everything BUT the user by an amount
    public void shiftHorizontal(int shiftBy) {
        for (int i = 0; i < squareList.size(); i++) {
            squareList.get(i).xc -= shiftBy;
        }
    }

    // method to shift everything BUT the user by an amount
    public void shiftVertical(double shiftBy) {
        for (int i = 0; i < squareList.size(); i++) {
            squareList.get(i).yc -= shiftBy;
        }
    }

    // check if the user is colliding into any obstacles
    public void checkIntruding() {
        // if the user is on top of any object, gravity is also off
        for (int i = 0; i < squareList.size(); i++) {
            if ((userYC > (squareList.get(i).yc - 50)) && (userYC < (squareList.get(i).yc + squareList.get(i).hd)) && (userXC > (squareList.get(i).xc - 50)) && (userXC < (squareList.get(i).xc + squareList.get(i).wd))) {
                intruding = true;
                intruder = squareList.get(i);
            }
        }
    }

    // add the velocity created by gravity
    public void actVelocity() { //
        userYC += YVelocity; // add the gravity velocity
        checkIntruding(); // are we intruding?
        /*
        TODO identified a problem where when two obstacles are overlapping, acting on velocity might make the intruder
        TODO the wrong obstacle, which causes the user to clip to another place
         */
        if (intruding == true) { // if we are intruding...
            userYC -= YVelocity; // rescind the change in y coordinate
            userYC = intruder.yc - 50; // put the user on top of the obstacle
            gravityOn = false; // gravity is OFF
            jumpRate = 5;
        }

        // see if we need to scroll the screen vertically
        checkScrollVertical();
        if (verticalScrollTrue) {
            userYC -= YVelocity;
            shiftVertical(YVelocity);
            groundYA -= YVelocity;
            groundYB -= YVelocity;
        }
        verticalScrollTrue = false;

        // are we on an obstacle?
        amOnObstacle = false;
        for (int i = 0; i < squareList.size(); i++) {
            if ((userYC == (squareList.get(i).yc - 50)) && ((userXC > (squareList.get(i).xc - 50)) && (userXC < (squareList.get(i).xc + squareList.get(i).wd)))) { // if we are on the same Y and we are within an X range...
                amOnObstacle = true;
                //jumpRate = 5;
            }
        }
        intruding = false;
    }

    // physics of gravity
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

    // is gravity on?
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

    // jump initializer
    public void jump() {
        if (currentlyJumping == false) {
            currentlyJumping = true;
        }
    }

    /*
    TODO we've solved the problem of the changing jump velocity when sliding off the side of an object, but...
    TODO if you are sliding against (pressing A or D) the side of an obstacle, and are jumping, and you hit
    TODO the bottom of an obstacle, jumprate does not decrease to 1
    TODO this only applies when you are both jumping and actively pressing A and D
    if we remove the horizontalBoolean capability, we don't get this problem, but then we get the sliding problem
    again.
    how do I tell the difference between HITTING the bottom of an obstacles and simply SLIDING along its side and
    fall below it?
    then again, this doesn't affect gameplay that much, it just bugs me.
     */

    // actual jumping method
    public void jumping() {
        if (currentlyJumping == true) {
            userYC -= jumpRate;
            checkIntruding();
            if (intruding) {
                intruding = false;
                frameCounter -= 1;
                userYC += jumpRate;
                userYC = intruder.yc + intruder.hd; // TODO potential problem here where user screen moves
                // TODO higher than user...
                if (horizontalBoolean == false) {
                    jumpRate = 0;
                    YVelocity = 0;
                }
                horizontalBoolean = false;
            }
            checkScrollVertical();
            if (verticalScrollTrue) {
                userYC += jumpRate;
                shiftVertical(-(jumpRate));
                groundYA += (jumpRate);
                groundYB += (jumpRate);
            }
            verticalScrollTrue = false;
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