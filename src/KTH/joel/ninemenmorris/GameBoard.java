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
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import java.io.Serializable;

enum States
{
    Placing, Moving, Flying, Removing, End
}

public class GameBoard extends SurfaceView implements SurfaceHolder.Callback, Serializable
{
    private Marker markers[];
    private int width, height;
    private int currentMarker, markerSize = 18;
    private int removeColor;
    private Board board;
    private Point start;
    private int turn;
    private States state;
    private States prevState;
    private Marker marker;
    private Point markerStart;
    private NineMorrisGame main;

    public GameBoard(NineMorrisGame main, int width, int height)
    {
        super(main);
        this.width = width;
        this.height = height;
        this.main = main;
        initGame();

        getHolder().addCallback(this);
        setFocusable(true);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    public void initGame()
    {
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        board = new Board(Color.BLACK, 0, 0, width, height);
        turn = Color.BLUE;
        state = States.Placing;
        prevState = States.Placing;

        markers = new Marker[markerSize];
        // Always put new markers is empty space
        markerStart = board.getPlaceHolder(1, 0).getCenterPoint();
        currentMarker = 0;
        marker = null;
        stopAnimations();

        if (markers[currentMarker] == null) {
            markers[currentMarker] = createMarker(turn, markerStart);
        }
        markers[currentMarker].update();
        viewMessage("It is now " + ((turn == Color.RED) ? "red" : "blue") + " turn", true);
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

        switch (state) {
            case Placing:
                if (currentMarker < markerSize) {
                    marker = markers[currentMarker];
                }
                break;
            case Moving:
            case Removing:
            case Flying:
                //marker = board.getPlaceHolder(p.x, p.y).getMarker();

                for (i = 0; i < markerSize; i++) {
                    tmp = markers[i];
                    if (tmp != null) {
                        dx = p.x - tmp.getX();
                        dy = p.y - tmp.getY();

                        if (Math.sqrt(dx*dx + dy*dy) <= tmp.getRadius()) {
                            currentMarker = i;

                            //viewMessage(String.format("[%d, %d] is inside circle [%f, %f]", p.x, p.y, tmp.getX(), tmp.getY()));
                            marker = tmp;
                        }
                    }
                }
                break;
        }

        if (marker != null) {
            if (state == States.Removing || marker.getColor() == turn) {
                return marker;
            }
        }
        return null;
    }

    private int countColor(int color)
    {
         int i, c = 0;
        for (i = 0; i < markerSize; i++) {
            if (markers[i] != null && markers[i].getColor() == color) {
                c++;
            }
        }
        return c;
    }

    private boolean isValidMove(Marker marker, Point to)
    {
        if (state == States.Removing) {
            return true;
        }
        int i;
        Point from = marker.getPosition();

        // Cannot move to same position you are in
        if (from != null && from.equals(to.x, to.y)) {
            return false;
        }

        // Cannot move to same place another marker is positioned
        for (i = 0; i < markerSize; i++) {
            if (markers[i] != null && markers[i].getPosition() != null && markers[i].getPosition().equals(to.x, to.y)) {
                return false;
            }
        }

        // Flying
        if (countColor(turn) <= 3) {
            return true;
        }

        switch (state) {
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

        if (state == state.Placing) {
            return -1;
        }

        for (i = 0; i < markerSize; i++) {
            if (markers[i] != null) {
                if (markers[i].getColor() == Color.BLUE) {
                    blueMarker++;
                } else if (markers[i].getColor() == Color.RED) {
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

        for (i = 0; i < markerSize; i++) {

            if (markers[i] != null && markers[i].getColor() == color) {
                Point p2 = markers[i].getPosition();

                if (p.equals(p2.x, p2.y)) {
                    board.removeMarker(p);
                    markers[i].stopThread();
                    markers[i] = null;
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
        //float scalingFactor = event.getSize();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (marker == null || state == States.End) {
                    return true;
                }

                Point p = board.circleIntersecting(current, marker.getRadius());

                if (p.x != -1 && p.y != -1 && isValidMove(marker, p)) {
                    if (state == States.Removing) {
                        if (removeMarker(p, removeColor)) {
                            state = prevState;
                        } else {
                            viewMessage(String.format("Cannot remove color %s from [%d, %d]", ((removeColor == Color.RED) ? "red" : "blue"), p.x, p.y), false);
                            return true;
                        }

                        int winner = winner();
                        if (winner != -1) {
                            state = States.End;
                            viewMessage(String.format("Color %s is the winner!", ((winner == Color.RED) ? "red" : "blue")), true);
                            return true;
                        }
                    } else {
                        //viewMessage(String.format("Moving to matrix position [%d, %d]", p.x, p.y));
                        board.moveTo(marker, p);
                        //viewMessage(String.format("Moved marker to [%d, %d]", p.x, p.y));

                        if (board.doThreeInARow(p, turn)) {
                            viewMessage("You created a mill, click on the other players marker to remove it", true);
                            prevState = state;
                            state = States.Removing;
                            removeColor = (turn == Color.RED) ? Color.BLUE : Color.RED;
                            return true;
                        }
                    }

                    turn = (turn == Color.RED) ? Color.BLUE : Color.RED;
                    viewMessage("It is now " + ((turn == Color.RED) ? "red" : "blue") + " turn", true);

                    if (state == States.Placing) {
                        //marker.setLock(true);

                        if (currentMarker < markerSize - 1) {
                            markers[++currentMarker] = createMarker(turn, markerStart);
                        } else {
                            state = States.Moving;
                        }
                    }
                } else {
                    viewMessage("Invalid move", true);
                    marker.move(board, start);
                }
                marker.stopThread();
                //marker.update();
                marker = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (marker == null || state == States.Removing || state == States.End) {
                    return true;
                }
                marker.move(board, current);
                break;
            case MotionEvent.ACTION_DOWN:
                if (marker != null || state == States.End) { // Previous animation is not finished or someone has won
                    return true;
                }

                marker = getMarker(current);
                start = current;

                if (marker == null) {
                   return true;
                }
                //viewMessage("Will now try to move marker");
                marker.startThread();
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
        //marker.move(board, to);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);
        board.draw(canvas);
        //canvas.drawBitmap(bitmap, x, y, null);

        int i;

        for (i = 0; i < markerSize; i++) {
            if (markers[i] != null) {
                markers[i].draw(canvas);
            }
        }

        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {}
    }

    public Marker createMarker(int color, Point p)
    {
        Marker marker =  new Marker(getHolder(), this, color, p, board.getPlaceHolder(0, 0).getRadius() - 10);
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
        stopAnimations();
    }

    public void stopAnimations()
    {
        int i;

        for (i = 0; i < markerSize; i++) {
            if (markers[i] != null) {
                markers[i].stopThread();
            }
        }
    }
}
