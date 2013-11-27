package KTH.joel.ninemenmorris;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-11-26
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */

import android.graphics.*;
import android.view.MotionEvent;
import android.view.SurfaceView;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameBoard extends SurfaceView implements SurfaceHolder.Callback
{
    private Marker marker = null;
    private Board board;

    public GameBoard(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        board = new Board(Color.WHITE);
        board.set(0, 0, 500, 500);

        getHolder().addCallback(this);
        setFocusable(true);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    public void setGameStarted(boolean mode)
    {
        marker.setRunnable(mode);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        float deltaX, deltaY;
        float scalingFactor = event.getSize();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                deltaX = currentX - marker.getX();
                deltaY = currentY - marker.getY();
                //vx += deltaX * scalingFactor;
                //vy += deltaY * scalingFactor;
        }
        marker.move(board, currentX, currentY);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);
        //canvas.drawBitmap(bitmap, x, y, null);
        marker.draw(canvas);

        try {
            Thread.sleep(30);
        } catch (InterruptedException e) { }

        //invalidate();  // Force a re-draw
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        marker = new Marker(getHolder(), this);
        marker.setRunnable(true);
        marker.start();

    }

    @Override
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
