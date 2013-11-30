package KTH.joel.ninemenmorris;

import android.graphics.*;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-11-27
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class Board
{
    private final int rows = 7;
    private final int cols = 7;
    private Paint paint;
    private Rect bounds;
    private Paint[][] boardMatrix = new Paint[rows][cols];
    private final int placemarkerRadius = 10;
    private Rect block;

    public Board(int color, int x, int y, int width, int height)
    {
        paint = new Paint();
        paint.setColor(color);
        bounds = new Rect(x, y, x+width, y+height);
        block = new Rect(0, 0, bounds.width() / cols, bounds.height() / rows);

        int i, j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                if ((i == j || rows-i-1 == j || cols-j-1==i ||
                        (i >= 2 && i < 5 && j >= 2 && j < 5) || i == 3 || j == 3
                ) && !(i == 3 && j == 3)) {
                    Paint p = new Paint();
                    p.setColor(Color.WHITE);
                    boardMatrix[i][j] = p;
                }
                //p.setStrokeWidth(3);

            }
        }
    }

    public Rect getBounds()
    {
        return bounds;
    }

    public Point blockIntersecting(Point p, int radius)
    {
        // a = r1 + r2
        // Using same radius for marker as placemarker, for nice drop to place
        final double a = 2*radius;
        double dx, dy;
        int i, j;
        Point p2;

        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                if (boardMatrix[i][j] != null) {
                    p2 = getCoordinate(i, j);
                    dx = p.x - p2.x;
                    dy = p.y - p2.y;

                    if (a > (dx * dx + dy * dy)) {
                        return p2;
                    }
                }
            }
        }

        return new Point(-1, -1);
    }

    private Point getCoordinate(int row, int col)
    {
        int cx = col*block.width() + placemarkerRadius, cy=row*block.height() + placemarkerRadius;
        return new Point(cx, cy);
    }

    public void drawPlaceMarker(int x, int y, Canvas canvas)
    {
        Point p = getCoordinate(x, y);
        canvas.drawCircle(p.x, p.y, placemarkerRadius, boardMatrix[x][y]);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawRect(bounds, paint);
        int i, j;
        for (i = 0; i < rows; i++) {
                for (j = 0; j < cols; j++) {
                    if (boardMatrix[i][j] != null) {
                        drawPlaceMarker(i, j, canvas);
                    }
                }
        }
    }
}
