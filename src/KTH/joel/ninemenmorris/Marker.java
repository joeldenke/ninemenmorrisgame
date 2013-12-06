package KTH.joel.ninemenmorris;

import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.Serializable;

/**
 * @description Marker on the game board
 * @author Joel Denke
 *
 */
public class Marker implements Serializable
{
    private int x, y;
    private int radius, color;
    private int i, j;

    public Marker(int color, Point p, int radius, int i, int j)
    {
        this.color = color;

        this.i = i;
        this.j = j;

        x = p.x;
        y = p.y;

        this.radius = radius;
    }

    public int getColor()
    {
        return color;
    }

    /**
     * @description Move marker to coordinate (x,y) if is inside board bounds
     * @author Joel Denke
     *
     */
    public void move(Board box, Point p)
    {
        if (isOnBoard(box, p)) {
            setX(p.x);
            setY(p.y);
        }
    }

    /**
     * @description Move marker to placeholders position
     * @author Joel Denke
     *
     */
    public void setPosition(Point center, Point position)
    {
        setX(center.x);
        setY(center.y);
        i = position.x;
        j = position.y;
    }

    public Point getPosition()
    {
        return new Point(i, j);
    }

    public int getRadius()
    {
       return radius;
    }

    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    /**
     * @description Is marker inside board bounds?
     * @author Joel Denke
     *
     */
    public boolean isOnBoard(Board board, Point p)
    {
        Rect a = board.getBounds();
        Rect b = new Rect((p.x-radius), (p.y-radius), (p.x + radius), (p.y + radius));
        return a.contains(b);
    }

    /**
     * @description Draws the marker on canvas
     * @author Joel Denke
     *
     */
    public void draw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
}
