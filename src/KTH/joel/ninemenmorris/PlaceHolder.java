package KTH.joel.ninemenmorris;

import android.graphics.*;

import java.io.Serializable;

/**
 * @description PlaceHolder who mark possible moving point and holds marker
 * @author Joel Denke
 *
 */
public class PlaceHolder implements Serializable
{
    private transient Rect bounds;
    private Marker marker;
    private int radius;
    private boolean empty = true;
    private int borderColor = Color.WHITE;

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

    public void setBorderColor(int borderColor)
    {
         this.borderColor = borderColor;
    }


    /**
     * @description Is a marker intersecting with a placeHolder?
     * @author Joel Denke
     *
     */
    public boolean circleIntersecting(Point p, int radius)
    {
        final int a = radius + this.radius;
        int dx, dy;

        dx = p.x - bounds.centerX();
        dy = p.y - bounds.centerY();

        // Distance between two coordinates is less than the cricles radius
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

    /**
     * @description Retrieve the center point of placeholder
     * @author Joel Denke
     *
     */
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

    public boolean hasMarker()
    {
        return (marker != null);
    }

    public void draw(Canvas canvas, Paint p)
    {
        if (!empty) {
            Paint p2 = new Paint();
            p2.setColor(borderColor);

            canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, p2);
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius - 5, p);
        }
    }
}
