package KTH.joel.ninemenmorris;

import android.graphics.*;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-11-27
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class Board implements Serializable
{
    private final int rows = 7;
    private final int cols = 7;
    private Paint paint;
    private PlaceHolder[][] boardMatrix = new PlaceHolder[rows][cols];
    private Rect bounds;

    public Board(int color, int x, int y, int width, int height)
    {
        paint = new Paint();
        paint.setColor(color);
        bounds = new Rect(x, y, x+width, y+height);
        //block = new Rect(0, 0, bounds.width() / cols, bounds.height() / rows);

        int i, j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                boolean fill = false;

                if ((i == j || rows-i-1 == j || cols-j-1==i ||
                        (i >= 2 && i < 5 && j >= 2 && j < 5) || i == 3 || j == 3
                ) && !(i == 3 && j == 3)) {
                    fill = true;
                }

                boardMatrix[i][j] = new PlaceHolder(getBlockBounds(i, j), fill);
            }
        }
    }

    public Rect getBlockBounds(int i, int j)
    {
        int width = (this.bounds.width() / cols), height = this.bounds.height() / rows;
        int x = i*width, y = j*height;
        return new Rect(x, y, x+width, y+height);
    }

    public PlaceHolder getPlaceHolder(int i, int j)
    {
        if (i < cols && j < rows) {
            return boardMatrix[i][j];
        }
        return null;
    }

    public Rect getBounds()
    {
        return bounds;
    }

    public Point circleIntersecting(Point p, int radius)
    {
        int i,j;

        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                if (boardMatrix[i][j].circleIntersecting(p, radius) && !boardMatrix[i][j].isEmpty()) {
                    return new Point(i ,j);
                }
            }
        }

        return new Point(-1, -1);
    }

    public boolean removeMarker(Point from)
    {
        PlaceHolder ph = boardMatrix[from.x][from.y];
        ph.setMarker(null);
        return true;
    }

    public boolean doThreeInARow(Point p, int color)
    {
         int player = (color == Color.RED) ? Color.BLUE : Color.RED;

         // Horisontal three markers of same color in a row
         if (p.x > 0 && p.x < 6 && boardMatrix[p.x + 1][p.y].isColor(color) && boardMatrix[p.x - 1][p.y].isColor(color)) {
             return true;
         }
        if (p.x < 5 && boardMatrix[p.x + 1][p.y].isColor(color) && boardMatrix[p.x + 2][p.y].isColor(color)) {
            return true;
        }
        if (p.x > 1 && boardMatrix[p.x - 2][p.y].isColor(color) && boardMatrix[p.x - 1][p.y].isColor(color)) {
            return true;
        }

         // Vertical three markers of same color in a row
         if (p.y > 0 && p.y < 6 && boardMatrix[p.x][p.y + 1].isColor(color) && boardMatrix[p.x][p.y - 1].isColor(color)) {
             return true;
         }
        if (p.y < 5 && boardMatrix[p.x][p.y + 1].isColor(color) && boardMatrix[p.x][p.y + 2].isColor(color)) {
            return true;
        }
        if (p.y > 1 && boardMatrix[p.x][p.y - 1].isColor(color) && boardMatrix[p.x][p.y - 2].isColor(color)) {
            return true;
        }

        return false;
    }

    public void moveTo(Marker marker, Point to)
    {
        Point from = marker.getPosition();
        PlaceHolder toHolder = boardMatrix[to.x][to.y];

        if (from == null) {
            if (toHolder.getMarker() == null) {
                marker.setPosition(toHolder.getCenterPoint(), to);
                toHolder.setMarker(marker);
            }
        } else {
            PlaceHolder fromHolder = boardMatrix[from.x][from.y];

            if (toHolder.getMarker() == null) {
                marker.setPosition(toHolder.getCenterPoint(), to);
                toHolder.setMarker(marker);
            }
            fromHolder.setMarker(null);
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawRect(bounds, paint);
        int i, j;
        for (i = 0; i < rows; i++) {
                for (j = 0; j < cols; j++) {
                    boardMatrix[i][j].draw(canvas);
                        //drawPlaceMarker(i, j, canvas);
                }
        }
    }
}
