import java.awt.*;

/**
 * Created by zhi on 7/16/17.
 */
public class Square {

    public static int x;
    public static int y;
    public static int w;
    public static int h;

    public Square(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void draw(Graphics2D brush) {
        brush.drawRect(x, y, w, h);
    }

}
