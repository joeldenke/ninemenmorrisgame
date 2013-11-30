package KTH.joel.ninemenmorris;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-11-26
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */

import android.graphics.*;
import android.view.*;

import android.content.Context;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameBoard extends SurfaceView implements SurfaceHolder.Callback
{
    private Marker markers[] = new Marker[2];
    private int currentMarker = 0;
    private Board board;
    private Point start;
    private int turn = Color.BLUE;
    private int state = 1;
    private Context context;
    private boolean moving = false;
    Marker marker = null;
    Rules rules = new Rules();
    Point markerStart = new Point(50, 50);

    public GameBoard(Context context)
    {
        super(context);
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        board = new Board(Color.BLACK, 0, 0, size.x, size.y);

        getHolder().addCallback(this);
        setFocusable(true);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    public void switchState()
    {
        int i;

        switch (state) {
            case 1:
                currentMarker = 0;
                for (i = 0; i < markers.length; i++) {
                    if (markers[i] != null) {
                        markers[i].setLock(false);
                    }
                }
                state = 2;
                break;
            case 2:
                state = 3;
                break;
            case 3:
                state = 1;
                break;
        }

        flashMessage("Switch to state: " + state);
       // marker.setRunnable(mode);
    }

    public void flashMessage(String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public Marker getMarker(Point p)
    {
        int i;
        Marker marker;

        for (i = 0; i < markers.length; i++) {
            marker = markers[i];
            if (marker != null) {
                if (Math.pow(p.x - marker.getX(), 2) + Math.pow(p.y - marker.getX(), 2) <= Math.pow(marker.getRadius(), 2)) {
                    flashMessage(String.format("[%d, %d] is inside circle [%f, %f]", p.x, p.y, marker.getX(), marker.getY()));
                    return marker;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Point current = new Point((int)event.getX(), (int)event.getY());
        //float scalingFactor = event.getSize();
        int cursor;

        if (marker == null) {
        switch (state) {
            case 1:
                marker = markers[currentMarker];
                break;
            case 2:
                Marker tmp = getMarker(current);

                if (tmp != null && tmp.getColor() == turn) {
                    marker = tmp;
                }
                break;
        } }

        if (marker == null) {
            return true;
        }


        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Point p = board.blockIntersecting(current, (int)marker.getRadius());

                if (p.x != -1 && p.y != -1) {
                    marker.move(board, p);
                    turn = (turn == Color.RED) ? Color.BLUE : Color.RED;

                    if (state == 1) {
                        //marker.setLock(true);

                        if (currentMarker < markers.length - 1) {
                            markers[++currentMarker] = createMarker(turn, markerStart);
                        } else {
                            currentMarker = 0;
                            int i;
                            for (i = 0; i < markers.length; i++) {
                                if (markers[i] != null) {
                                    markers[i].setLock(false);
                                }
                            }
                            state = 2;//switchState();
                        }
                    }
                } else {
                    marker.moveTo(board, markerStart);
                }
                marker.stopThread();
                marker = null;
                break;
            case MotionEvent.ACTION_MOVE:
                marker.move(board, current);
                break;
            case MotionEvent.ACTION_DOWN:
                start = current;
                marker.startThread();
                break;

        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);
        board.draw(canvas);
        //canvas.drawBitmap(bitmap, x, y, null);
        int i;

        for (i = 0; i < markers.length; i++) {
            if (markers[i] != null) {
                markers[i].draw(canvas);
            }
        }

        try {
            Thread.sleep(30);
        } catch (InterruptedException e) { }

        //invalidate();  // Force a re-draw
    }

    public Marker createMarker(int color, Point p)
    {
        Marker marker =  new Marker(getHolder(), this, color, p);
        marker.update();
        return marker;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (markers[currentMarker] == null) {
            markers[currentMarker] = createMarker(turn, markerStart);
        }
        markers[currentMarker].update();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        int i;

        for (i = 0; i < markers.length; i++) {
            if (markers[i] != null) {
                markers[i].stopThread();
            }
        }
    }
}
