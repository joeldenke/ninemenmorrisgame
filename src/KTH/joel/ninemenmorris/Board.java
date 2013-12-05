package KTH.joel.ninemenmorris;

import android.graphics.*;

import java.io.Serializable;

/**
 * @description Board with placeholders
 * @author Joel Denke
 *
 */
public class Board implements Serializable
{
    private final int rows = 7;
    private final int cols = 7;
    private PlaceHolder[][] boardMatrix = new PlaceHolder[rows][cols];
    private Rect bounds;

    public Board(int x, int y, int width, int height)
    {
        bounds = new Rect(x, y, x+width, y+height);

        int i, j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                boolean fill = false;

                // Formula to draw the board star layout
                if ((i == j || rows-i-1 == j || cols-j-1==i ||
                        (i >= 2 && i < 5 && j >= 2 && j < 5) || i == 3 || j == 3
                ) && !(i == 3 && j == 3)) {
                    fill = true;
                }

                boardMatrix[i][j] = new PlaceHolder(getBlockBounds(i, j), fill);
            }
        }
    }

    /**
     * @description Get the block size and coordinate as a Android Rect
     * @author Joel Denke
     *
     */
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

    /**
     * @description Return block position in matrix, if pointer intersects
     * @author Joel Denke
     *
     */
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

    /**
     * @description Remove marker from placeholder
     * @author Joel Denke
     *
     */
    public boolean removeMarker(Point from)
    {
        PlaceHolder ph = boardMatrix[from.x][from.y];
        ph.setMarker(null);
        return true;
    }

    /**
     * @description Has player formed a mill? Checks if one color is three in a row both horinstally and vertically
     * @author Joel Denke
     *
     */
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

    /**
     * @description Move a marker from its current placeholder to another
     * @author Joel Denke
     *
     */
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

    public void draw(Canvas canvas, Paint paint)
    {
        canvas.drawRect(bounds, paint);
        int i, j;
        for (i = 0; i < rows; i++) {
                for (j = 0; j < cols; j++) {
                    Paint p = new Paint();
                    p.setColor(Color.WHITE);
                    boardMatrix[i][j].draw(canvas, p);
                }
        }
    }
}
