import java.awt.*;

/**
 * Created by zhi on 7/16/17.
 */
public class Square {

    int xc;
    int yc;
    int wd;
    int hd;

    public Square(int x, int y, int w, int h) {
        this.xc = x;
        this.yc = y;
        this.wd = w;
        this.hd = h;
    }

    public void draw(Graphics2D brush) {
        brush.drawRect(this.xc, this.yc, this.wd, this.hd);
    }

}
