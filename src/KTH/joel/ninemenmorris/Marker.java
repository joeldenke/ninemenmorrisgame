package KTH.joel.ninemenmorris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class Marker extends Thread
{

    private SurfaceHolder sh;
    private GameBoard board;

    private Canvas canvas;

    private boolean run = false;
    private float x, y;
    private float vx, vy;
    private Paint paint = new Paint();
    private float radius = 30;

    public Marker(SurfaceHolder _holder, GameBoard _ball)
    {
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);

        x = 50.0f;
        y = 50.0f;

        vx = 10.0f;
        vy = 10.0f;

        sh = _holder;
        board = _ball;
    }

    public void move(Board box, float x, float y)
    {
        if (!isCollision(box, x, y)) {
            setX(x);
            setY(y);
        }
    }

    public boolean isCollision(Board box, float x, float y) {
        boolean collision = false;

        if (x + radius > box.xMax || x - radius < box.xMin) {
            collision = true;
        }
        if (y + radius > box.yMax || y - radius < box.yMin) {
            collision = true;
        }

        return collision;
    }

    public void setRunnable(boolean _run) {

        run = _run;
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

    @Override
    public void run() {

        while(run) {

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
    }

    public Canvas getCanvas() {

        if(canvas != null) {

            return canvas;

        } else {

            return null;
        }
    }
}
