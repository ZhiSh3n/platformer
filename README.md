Java Platformer
============================
Created by: Zachary Yong

This is a small (and quite useless) platformer game I built in Java after figuring out the best way to allow KeyListener
to process multiple keypresses simultaneously. The user can move either left or right, and use the spacebar to jump on top of
obstacles.

This is very likely not the best way to code a platformer game. I tried my best to remove redundant code and fix
existing bugs but there are still some errors you may encounter while testing this program that I personally think
are due to the lack of foresight when I was implementing features (like jumping). The drawing and rendering is also done
in a very CPU-intensive manner because I didn't use anything else apart from Graphics2D.

In the future I will likely use this project as a starting point, but also view some parts of this project as examples of what not to do.

## Files
* [Run.java](src/Run.java)
    * Run the program from this class.
    * Create a new JFrame; add a Canvas (Graphics2D) and a KeyListener to it.
* [Square.java](src/Square.java)
    * A simple Square object used in the process of randomly generating obstacles for the user to navigate through.
    * Includes all the variables Graphics2D requires to draw a rectangle and also a method that draws the squares.
* [Mover.java](src/Mover.java)
    * Contains methods for adding key codes when keys are pressed and removing key codes when keys are released.
* [Canvas.java](src/Canvas.java)
    * Where most of the magic and physics happens.
    * Contains the graphics component and the animation thread.
    * All the code intended for displaying in the JFrame is found here.
    * Processes the list of currently-pressed-keys for respective actions.
    * Physics related methods, like gravity, are found here.

## How it works
To move left or right, use A or D respectively. To jump, use the spacebar. Since we are able to process simultaneous keypresses,
you can move left or right while jumping.

* Randomly generating obstacles
    * In a fairly large area around where the user spawns, create random rectangles to act as obstacles.
* Drawing objects
    * Every object (including the ground) has its own static coordinates.
    * Objects are only drawn if their coordinates are within the 800 by 600 JFrame. Otherwise they are not drawn.
* Prevention collisions between user and obstacles
    * If a certain coordinate change -- due to user input, gravity, or jumping -- causes the user to "intrude" within an obstacle's area, then rescind those coordinate changes.
* Other physics
    * Whenever gravity is applicable (not on the ground or atop an obstacle), add a certain velocity to the user's y-coordinate.
    * I created the jumping mechanic by applying a constant upward velocity to the user whenever spacebar was pressed. This is probably not the best method, as it created a problems that I will outline in the bugs section below.

## Bugs

* Clipping when falling at fast speeds onto close obstacles
    * The method to detect user-intrusion iterates through all the obstacles and finds one "intruder". The problem is that when two obstacles are interscting, one coordinate change may actually cause the user to intrude into more than one obstacle.
    * Since the user-intrusion method only detects one "intruder", the obstacle that is selected is the one that has the latter index in the ArrayList.
    * As a result, the method tries to move the user into a "safe location", but this safe location is occupied by another obstacle. This makes the user clip into another location.
    * This bug is hard to replicate but it is definitely there.
* Jumping into a corner made by two obstacles
    * One other problem with creating the jump function in the way I did is that when you are sliding against the side of an obstacle, and drop off, the method reads as if you have hit the bottom of the obstacle.
    * Adding a boolean value to deal with this causes a new bug, which is that jump rate is not affected when you hit the bottom of one obstacle while sliding up against the side of another obstacle.
    * Gameplay is not substantially affected, but I can't seem to find a solution to this new bug (except for rescinding my fix with the former bug, but this will only cause the former bug to appear again).
* Clipping upwards off-screen
    * Has been observed, but not sure why.
    * Might have to do with the fact that the screen pans upwards at a slightly different rate than the user's y-coordinate changes (in some events, that is).