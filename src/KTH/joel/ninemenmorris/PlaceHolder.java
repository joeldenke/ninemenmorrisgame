package KTH.joel.ninemenmorris;

import android.graphics.*;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-12-03
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class PlaceHolder implements Serializable
{
    private transient Rect bounds;
    private Marker marker;
    private int radius;
    private boolean empty = true;

    public PlaceHolder(Rect bounds, boolean fill)
    {
        this.bounds = bounds;
        this.radius = (bounds.height() < bounds.width() ? bounds.height() / 2 : bounds.width() / 2);

        if (fill) {
            empty = false;
        }
    }

    public int getRadius()
    {
        return radius;
    }

    public boolean isEmpty()
    {
        return empty;
    }

    public boolean circleIntersecting(Point p, int radius)
    {
        // a = r1 + r2
        // Using same radius for marker as placemarker, for nice drop to place
        final int a = radius + this.radius;
        int dx, dy;

        dx = p.x - bounds.centerX();
        dy = p.y - bounds.centerY();

        if (a > Math.sqrt(dx * dx + dy * dy)) {
             return true;
        }

        return false;
    }

    public boolean isColor(int color)
    {
        if (marker == null) {
            return false;
        }

        return marker.getColor() == color;
    }

    public Point getCenterPoint()
    {
        return new Point(bounds.centerX(), bounds.centerY());
    }

    public void setMarker(Marker marker)
    {
         this.marker = marker;
    }

    public Marker getMarker()
    {
        return marker;
    }

    public void draw(Canvas canvas, Paint p)
    {
        if (!empty) {
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, p);
        }
    }
}
