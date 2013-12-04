package KTH.joel.ninemenmorris;

import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.Serializable;

public class Marker implements Serializable
{
    private int x, y;
    private int radius, color;
    private int i, j;

    public Marker(int color, Point p, int radius)
    {
        this.color = color;

        x = p.x;
        y = p.y;

        this.radius = radius;
    }

    public int getColor()
    {
        return color;
    }

    public void move(Board box, Point p)
    {
        if (isOnBoard(box, p)) {
            setX(p.x);
            setY(p.y);
        }
    }

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

    public boolean isOnBoard(Board board, Point p)
    {
        Rect a = board.getBounds();
        Rect b = new Rect((p.x-radius), (p.y-radius), (p.x + radius), (p.y + radius));
        return a.contains(b);
    }

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

    /*
    public void moveToCoordinate(Board box, Point to)
    {
        Point startDist = new Point((int)x - to.x, (int)y - to.y);
        Point currentDist = startDist;
        //int dv = (startDist.y / startDist.x);


        while (currentDist.x > 100 && currentDist.y > 100) {
            x += dv;
            y += dv;
            currentDist = new Point((int)x - to.x, (int)y - to.y);
        }
        x = to.x;
        y = to.y;
    }*/
}
