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

import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;

import java.io.Serializable;

enum States
{
    Placing, Moving, Flying, Removing, End
}

public class GameBoard extends SurfaceView implements SurfaceHolder.Callback, Serializable, Runnable
{
    private int width, height;
    private NineMorrisGame main;
    private GameData data;
    private Point markerStart;
    private Point start;
    private Board board;
    private Thread thread = null;
    private Canvas canvas = null;

    public GameBoard(NineMorrisGame main, GameData data, int width, int height)
    {
        super(main);
        this.width = width;
        this.height = height;
        this.main = main;
        if (data instanceof GameData) {
            this.data = data;
        } else {
            this.data = new GameData();
        }
        initGame();

        getHolder().addCallback(this);
        setFocusable(true);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
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

    public synchronized void update()
    {
        SurfaceHolder sh = getHolder();
        canvas = null;

        try {
            canvas = sh.lockCanvas(null);

            synchronized(sh) {
                if (canvas != null) {
                    onDraw(canvas);
                }
            }
        } finally {
            if(canvas != null) {
                sh.unlockCanvasAndPost(canvas);
            }

        }
    }

    @Override
    public void run()
    {
        while (thread != null) {
            update();
        }
    }

    public synchronized GameData getGameData()
    {
        return data;
    }

    public void initGame()
    {
        stopAnimations();
        board = new Board(0, 0, width, height);
        markerStart = board.getPlaceHolder(1, 0).getCenterPoint();

        if (data.markers[data.currentMarker] == null) {
            data.markers[data.currentMarker] = createMarker(data.turn, markerStart);
        }
        update();
        viewMessage("It is now " + ((data.turn == Color.RED) ? "red" : "blue") + " turn", true);
    }

    public void viewMessage(String message, boolean flash)
    {
        main.viewMessage(message, flash);
    }

    public Marker getMarker(Point p)
    {
        int i, dx, dy;
        double d;
        Marker marker = null, tmp;

        switch (data.state) {
            case Placing:
                if (data.currentMarker < data.markerSize) {
                    marker = data.markers[data.currentMarker];
                }
                break;
            case Moving:
            case Removing:
            case Flying:
                for (i = 0; i < data.markerSize; i++) {
                    tmp = data.markers[i];
                    if (tmp != null) {
                        dx = p.x - tmp.getX();
                        dy = p.y - tmp.getY();

                        if (Math.sqrt(dx*dx + dy*dy) <= tmp.getRadius()) {
                            data.currentMarker = i;

                            //viewMessage(String.format("[%d, %d] is inside circle [%f, %f]", p.x, p.y, tmp.getX(), tmp.getY()));
                            marker = tmp;
                        }
                    }
                }
                break;
        }

        if (marker != null) {
            if (data.state == States.Removing || marker.getColor() == data.turn) {
                return marker;
            }
        }
        return null;
    }

    private int countColor(int color)
    {
         int i, c = 0;
        for (i = 0; i < data.markerSize; i++) {
            if (data.markers[i] != null && data.markers[i].getColor() == color) {
                c++;
            }
        }
        return c;
    }

    private boolean isValidMove(Marker marker, Point to)
    {
        if (data.state == States.Removing) {
            return true;
        }
        int i;
        Point from = marker.getPosition();

        // Cannot move to same position you are in
        if (from != null && from.equals(to.x, to.y)) {
            return false;
        }

        // Cannot move to same place another marker is positioned
        for (i = 0; i < data.markerSize; i++) {
            if (data.markers[i] != null && data.markers[i].getPosition() != null && data.markers[i].getPosition().equals(to.x, to.y)) {
                return false;
            }
        }

        // Flying
        if (countColor(data.turn) <= 3) {
            return true;
        }

        switch (data.state) {
            case Placing:
                return true;
            case Moving:
                return (Math.abs(from.x - to.x) <= 1 && Math.abs(from.y - to.y) <= 1);
            default :
                return false;
        }
    }

    public int winner()
    {
        int i, redMarker = 0, blueMarker = 0;

        if (data.state == data.state.Placing) {
            return -1;
        }

        for (i = 0; i < data.markerSize; i++) {
            if (data.markers[i] != null) {
                if (data.markers[i].getColor() == Color.BLUE) {
                    blueMarker++;
                } else if (data.markers[i].getColor() == Color.RED) {
                    redMarker++;
                }
            }
        }

        if (redMarker < 3) {
            return Color.BLUE;
        } else if (blueMarker < 3) {
            return Color.RED;
        } else {
            return -1;
        }
    }

    private boolean removeMarker(Point p, int color)
    {
        int i;

        for (i = 0; i < data.markerSize; i++) {
            if (data.markers[i] != null && data.markers[i].getColor() == color) {
                Point p2 = data.markers[i].getPosition();

                if (p.equals(p2.x, p2.y)) {
                    board.removeMarker(p);
                    //stopThread();
                    data.markers[i] = null;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Point current = new Point((int)event.getX(), (int)event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (data.marker == null || data.state == States.End) {
                    return true;
                }

                Point p = board.circleIntersecting(current, data.marker.getRadius());

                if (p.x != -1 && p.y != -1 && isValidMove(data.marker, p)) {
                    if (data.state == States.Removing) {
                        if (removeMarker(p, data.removeColor)) {
                            data.state = data.prevState;
                        } else {
                            viewMessage(String.format("Cannot remove color %s from [%d, %d]", ((data.removeColor == Color.RED) ? "red" : "blue"), p.x, p.y), false);
                            return true;
                        }

                        int winner = winner();
                        if (winner != -1) {
                            data.state = States.End;
                            viewMessage(String.format("Color %s is the winner!", ((winner == Color.RED) ? "red" : "blue")), true);
                            return true;
                        }
                    } else {
                        board.moveTo(data.marker, p);

                        if (board.doThreeInARow(p, data.turn)) {
                            viewMessage("You created a mill, click on the other players marker to remove it", true);
                            data.prevState = data.state;
                            data.state = States.Removing;
                            data.removeColor = (data.turn == Color.RED) ? Color.BLUE : Color.RED;
                            return true;
                        }
                    }

                    data.turn = (data.turn == Color.RED) ? Color.BLUE : Color.RED;
                    viewMessage("It is now " + ((data.turn == Color.RED) ? "red" : "blue") + " turn", true);

                    if (data.state == States.Placing) {
                        if (data.currentMarker < data.markerSize - 1) {
                            data.markers[++data.currentMarker] = createMarker(data.turn, markerStart);
                        } else {
                            data.state = States.Moving;
                        }
                    }
                } else {
                    viewMessage("Invalid move", true);
                    data.marker.move(board, start);
                }
                stopThread();
                update();
                data.marker = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (data.marker == null || data.state == States.Removing || data.state == States.End) {
                    return true;
                }
                data.marker.move(board, current);
                break;
            case MotionEvent.ACTION_DOWN:
                if (data.marker != null || data.state == States.End) { // Previous animation is not finished or someone has won
                    return true;
                }

                data.marker = getMarker(current);
                start = current;

                if (data.marker == null) {
                   return true;
                }

                startThread();
                break;

        }
        return true;
    }

    public void moveSmooth(Marker marker, Point to)
    {
        Point from = new Point(marker.getX(), marker.getY());
        Animation anim = new TranslateAnimation(
                Animation.ABSOLUTE, //from xType
                from.x,
                Animation.ABSOLUTE, //to xType
                to.x,
                Animation.ABSOLUTE, //from yType
                from.y,
                Animation.ABSOLUTE, //to yType
                to.y
        );

        Animation.AnimationListener animL = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //this is just a method to delete the ImageView and hide the animation Layout until we need it again.
                clearAnimation();
            }
        };

        anim.setAnimationListener(animL);
        anim.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        anim.setDuration(2000);
        this.setAnimation(anim);
        anim.startNow();
        //marker.move(data.board, to);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        board.draw(canvas, p);

        int i;

        for (i = 0; i < data.markerSize; i++) {
            if (data.markers[i] != null) {
                data.markers[i].draw(canvas);
            }
        }

        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {}
    }

    public Marker createMarker(int color, Point p)
    {
        return new Marker(color, p, board.getPlaceHolder(0, 0).getRadius() - 10);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        invalidate();
        if (data.markers[data.currentMarker] == null) {
            data.markers[data.currentMarker] = createMarker(data.turn, markerStart);
        }
        update();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        stopAnimations();
    }

    public void stopAnimations()
    {
        stopThread();
    }
}
