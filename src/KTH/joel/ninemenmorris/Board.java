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

    public void clearValid()
    {
        int i,j;

        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                boardMatrix[i][j].setBorderColor(Color.WHITE);
            }
        }
    }

    public int markEmpty()
    {
        int c = 0;
        int i, j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                if (!boardMatrix[i][j].hasMarker()) {
                    boardMatrix[i][j].setBorderColor(Color.GREEN);
                    c += 1;
                }
            }
        }

        return c;
    }

    public int markEmpty(Point p)
    {
        int c = 0, diff;
        int d = 1;

        //if (p.x != 3 && p.y != 3) {
            if ((p.x == 1 || p.x == 5) || (p.y == 1 || p.y == 5)) {
                d = 2;
            }
            if ((p.x == 0 || p.x == 6) || (p.y == 0 || p.y == 6)) {
                d = 3;
            }
        //}

        for (diff = 1; diff <= d; diff++) {
            if (p.x < 6-diff && !boardMatrix[p.x + diff][p.y].hasMarker()) {
                boardMatrix[p.x + diff][p.y].setBorderColor(Color.GREEN);
                c += 1;
            }
            if (p.x > diff-1 && !boardMatrix[p.x - diff][p.y].hasMarker()) {
                boardMatrix[p.x - diff][p.y].setBorderColor(Color.GREEN);
                c += 1;
            }
            if (p.y < 6-diff && !boardMatrix[p.x][p.y + diff].hasMarker()) {
                boardMatrix[p.x][p.y + diff].setBorderColor(Color.GREEN);
                c += 1;
            }
            if (p.y > diff-1 && !boardMatrix[p.x][p.y - diff].hasMarker()) {
                boardMatrix[p.x][p.y - diff].setBorderColor(Color.GREEN);
                c += 1;
            }
        }

        return c;
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
        int diff, d = 1;


        //if (p.x != 3 && p.y != 3) {
            if ((p.x == 1 || p.x == 5) || (p.y == 1 || p.y == 5)) {
                d = 2;
            }
            if ((p.x == 0 || p.x == 6) || (p.y == 0 || p.y == 6)) {
                d = 3;
            }
        //}

        for (diff = 1; diff <= d; diff++) {
            // Horisontal three markers of same color in a row
            if (p.x > diff-1 && p.x < 6-diff && boardMatrix[p.x + diff][p.y].isColor(color) && boardMatrix[p.x - diff][p.y].isColor(color)) {
                return true;
            }
            if (p.x < 7-diff-diff && boardMatrix[p.x + diff][p.y].isColor(color) && boardMatrix[p.x + diff+diff][p.y].isColor(color)) {
                return true;
            }
            if (p.x > diff+diff-1 && boardMatrix[p.x - diff][p.y].isColor(color) && boardMatrix[p.x - diff - diff][p.y].isColor(color)) {
                return true;
            }

            // Vertical three markers of same color in a row
            if (p.y > diff-1 && p.y < 6-diff && boardMatrix[p.x][p.y + diff].isColor(color) && boardMatrix[p.x][p.y - diff].isColor(color)) {
                return true;
            }
            if (p.y < 7-diff-diff && boardMatrix[p.x][p.y + diff].isColor(color) && boardMatrix[p.x][p.y + diff+diff].isColor(color)) {
                return true;
            }
            if (p.y > diff+diff-1 && boardMatrix[p.x][p.y - diff].isColor(color) && boardMatrix[p.x][p.y - diff-diff].isColor(color)) {
                return true;
            }
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
