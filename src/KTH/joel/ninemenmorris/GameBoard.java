package KTH.joel.ninemenmorris;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-11-26
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */

import android.view.SurfaceView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameBoard extends SurfaceView implements SurfaceHolder.Callback
{
    private Marker marker = null;

    private Bitmap bitmap;

    private float x, y;
    private float vx, vy;

    public GameBoard(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

        x = 50.0f;
        y = 50.0f;

        vx = 10.0f;
        vy = 10.0f;

        getHolder().addCallback(this);
        marker = new Marker(getHolder(), this);
    }

    protected void onDraw(Canvas canvas) {

        update(canvas);

        canvas.drawBitmap(bitmap, x, y, null);
    }

    public void update(Canvas canvas) {

        checkCollisions(canvas);

        x += vx;
        y += vy;
    }

    public void checkCollisions(Canvas canvas) {

        if(x - vx < 0) {

            vx = Math.abs(vx);

        } else if(x + vx > canvas.getWidth() - getBitmapWidth()) {

            vx = -Math.abs(vx);
        }

        if(y - vy < 0) {

            vy = Math.abs(vy);

        } else if(y + vy > canvas.getHeight() - getBitmapHeight()) {

            vy = -Math.abs(vy);
        }
    }

    public int getBitmapWidth() {

        if(bitmap != null) {

            return bitmap.getWidth();

        } else {

            return 0;
        }
    }

    public int getBitmapHeight() {

        if(bitmap != null) {

            return bitmap.getHeight();

        } else {

            return 0;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        marker.setRunnable(true);
        marker.start();

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

        boolean retry = true;
        marker.setRunnable(false);

        while(retry) {

            try {

                marker.join();
                retry = false;

            } catch(InterruptedException ie) {

                //Try again and again and again
            }

            break;
        }

        marker = null;

    }
}
