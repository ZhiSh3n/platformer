import java.awt.*;

/**
 * Created by zhi on 7/16/17.
 */
public class Square {

    // all the variables needed to draw a rectangle in Graphics2D
    int xc;
    int yc;
    int wd;
    int hd;

    // constructor
    public Square(int x, int y, int w, int h) {
        this.xc = x;
        this.yc = y;
        this.wd = w;
        this.hd = h;
    }

    // method to draw the rectangle given a Graphics2D brush
    public void draw(Graphics2D brush) {
        brush.drawRect(this.xc, this.yc, this.wd, this.hd);
    }

}
