package KTH.joel.ninemenmorris;

import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.Serializable;

public class Marker implements Runnable, Serializable
{

    private SurfaceHolder sh;
    private GameBoard board;

    private Canvas canvas;

    private int x, y;
    private Paint paint = new Paint();
    private int radius;
    private Thread thread = null;
    private Point position;

    public Marker(SurfaceHolder holder, GameBoard board, int color, Point p, int radius)
    {
        paint.setColor(color);

        x = p.x;
        y = p.y;

        sh = holder;
        this.radius = radius;
        this.board = board;

        update();
    }

    public int getColor()
    {
        return paint.getColor();
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
        this.position = position;
    }

    public Point getPosition()
    {
        return position;
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

    public boolean isRunning()
    {
        return thread != null;
    }

    public void draw(Canvas canvas)
    {
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

    public void update()
    {
        canvas = null;

        try {
            canvas = sh.lockCanvas(null);

            synchronized(sh) {
                if (canvas != null) {
                    board.onDraw(canvas);
                }
            }
        } finally {

            if(canvas != null) {

                sh.unlockCanvasAndPost(canvas);
            }

        }
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

    public void startThread()
    {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stopThread()
    {
        thread = null;
    }

    @Override
    public void run()
    {
        while (thread != null) {
            update();
        }
    }
}
