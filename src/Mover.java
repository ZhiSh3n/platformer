import java.awt.event.*;
import java.util.*;

/**
 * Created by zhi on 7/16/17.
 */
class Mover implements KeyListener {

    public static ArrayList<Integer> keyList = new ArrayList<Integer>();

    @Override
    public void keyPressed(final KeyEvent event) {
        if (!Mover.keyList.contains(KeyEvent.VK_W) && (event.getKeyCode() == KeyEvent.VK_W)) {
            keyList.add(event.getKeyCode());
        }
        if (!Mover.keyList.contains(KeyEvent.VK_A) && (event.getKeyCode() == KeyEvent.VK_A)) {
            keyList.add(event.getKeyCode());
        }
        if (!Mover.keyList.contains(KeyEvent.VK_S) && (event.getKeyCode() == KeyEvent.VK_S)) {
            keyList.add(event.getKeyCode());
        }
        if (!Mover.keyList.contains(KeyEvent.VK_D) && (event.getKeyCode() == KeyEvent.VK_D)) {
            keyList.add(event.getKeyCode());
        }
        if (!Mover.keyList.contains(KeyEvent.VK_SPACE) && (event.getKeyCode() == KeyEvent.VK_SPACE)) {
            keyList.add(event.getKeyCode());
        }
    }

    @Override
    public void keyReleased(final KeyEvent event) {
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i) == event.getKeyCode()) {
                keyList.remove(i);
            }
        }
    }

    @Override
    public void keyTyped(final KeyEvent event) {
        // null
    }

}
