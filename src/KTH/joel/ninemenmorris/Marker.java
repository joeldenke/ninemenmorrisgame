package KTH.joel.ninemenmorris;

import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;

public class Marker implements Runnable
{

    private SurfaceHolder sh;
    private GameBoard board;

    private Canvas canvas;

    private float x, y;
    private Paint paint = new Paint();
    private float radius = 30;
    private Thread thread = null;
    private int color;
    private boolean locked = false;

    public Marker(SurfaceHolder holder, GameBoard board, int color, Point p)
    {
        paint.setColor(color);
        paint.setStrokeWidth(3);

        x = p.x;
        y = p.y;

        sh = holder;
        this.board = board;
        this.color = color;

        update();
    }

    public void setLock(boolean lock)
    {
        this.locked = lock;
    }

    public int getColor()
    {
        return paint.getColor();
    }

    public void move(Board box, Point p)
    {
        if (isOnBoard(box, p) && !locked) {
            setX(p.x);
            setY(p.y);
        }
    }

    public float getRadius()
    {
       return radius;
    }

    public boolean isOnBoard(Board board, Point p)
    {
        Rect a = board.getBounds();
        Rect b = new Rect((int) (p.x-radius), (int) (p.y-radius), (int) (p.x + radius), (int) (p.y + radius));

        //Log.d("Rect:", String.format("Rectangle A: [%d, %d, %d, %d]", a.left, a.top, a.right, a.bottom));
        //Log.d("Rect:", String.format("Rectangle B: [%d, %d, %d, %d]", b.left, b.top, b.right, b.bottom));

        return b.intersect(a);
    }

    public boolean isRunning()
    {
        return thread != null;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawCircle(x, y, radius, paint);
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }

    public void update()
    {
        canvas = null;

        try {
            canvas = sh.lockCanvas(null);

            synchronized(sh) {
                board.onDraw(canvas);
            }
        } finally {

            if(canvas != null) {

                sh.unlockCanvasAndPost(canvas);
            }

        }
    }

    public void moveTo(Board box, Point to)
    {
        Point startDist = new Point((int)x - to.x, (int)y - to.y);
        Point currentDist = startDist;
        int dv = (startDist.y / startDist.x);

        startThread();
        while (currentDist.x > 100 && currentDist.y > 100) {
            x += dv;
            y += dv;
            currentDist = new Point((int)x - to.x, (int)y - to.y);
        }
        stopThread();
    }

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
