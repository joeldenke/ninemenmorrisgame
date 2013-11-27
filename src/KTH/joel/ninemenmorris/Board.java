package KTH.joel.ninemenmorris;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-11-27
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class Board
{
    int xMin, xMax, yMin, yMax;
    private Paint paint;
    private Rect bounds;

    public Board(int color)
    {
        paint = new Paint();
        paint.setColor(color);
        bounds = new Rect();
    }

    public void set(int x, int y, int width, int height)
    {
        xMin = x;
        xMax = x + width - 1;
        yMin = y;
        yMax = y + height - 1;
        bounds.set(xMin, yMin, xMax, yMax);
    }
}
