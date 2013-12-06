package KTH.joel.ninemenmorris;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-11-26
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */

import android.graphics.*;
import android.util.Log;
import android.view.*;

import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;

import java.io.Serializable;

/**
 * @description Possible states of the game
 * @author Joel Denke
 *
 */
enum States implements Serializable
{
    Placing, Moving, Flying, Removing, End
}

/**
 * @description Game board as a Surface and contains game logic
 * @author Joel Denke
 *
 */
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
        initGame(data);

        getHolder().addCallback(this);
        setFocusable(true);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    /**
     * @description Start animation
     * @author Joel Denke
     *
     */
    public void startAnimation()
    {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * @description Stops animation
     * @author Joel Denke
     *
     */
    public void stopAnimation()
    {
        thread = null;
    }

    /**
     * @description Updates canvas by redrawing game board and markers
     * @author Joel Denke
     *
     */
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

    /**
     * @description Will run the animation
     * @author Joel Denke
     *
     */
    @Override
    public void run()
    {
        while (thread != null) {
            update();
        }
    }

    /**
     * @description Get current game data
     * @author Joel Denke
     *
     */
    public synchronized GameData getGameData()
    {
        return data;
    }

    /**
     * @description Init the game with imported game data
     * @author Joel Denke
     *
     */
    public void initGame(GameData data)
    {
        if (data instanceof GameData) {
            this.data = data;
        } else {
            this.data = new GameData();
        }
        stopAnimation();
        board = new Board(0, 0, width, height);
        markerStart = board.getPlaceHolder(1, 0).getCenterPoint();

        if (this.data.markers[this.data.currentMarker] == null) {
            this.data.markers[this.data.currentMarker] = createMarker(this.data.turn, markerStart);
        }
        update();
        if (this.data.state != null && this.data.state != States.End) {
           viewMessage("It is now " + ((this.data.turn == Color.RED) ? "red" : "blue") + " turn", true);
        } else {
            int winner = winner();
            if (winner != -1) {
                viewMessage(String.format("Color %s is the winner!", ((winner == Color.RED) ? "red" : "blue")), true);
            }
        }
    }

    /**
     * @description View message in textView and flash in tost
     * @author Joel Denke
     *
     */
    public void viewMessage(String message, boolean flash)
    {
        main.viewMessage(message, flash);
    }

    /**
     * @description Get current working marker or get intersecting marker
     * @author Joel Denke
     *
     */
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

    /**
     * @description Count how many markers has a specified color
     * @author Joel Denke
     *
     */
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

    /**
     * @description Is the current move valid?
     * @author Joel Denke
     *
     */
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
        PlaceHolder holder = board.getPlaceHolder(to.x, to.y);
        if (holder == null || holder.getMarker() != null) {
            return false;
        }

        // Flying
        if (countColor(data.turn) <= 3) {
            return true;
        }

        switch (data.state) {
            case Placing:
                return true;
            case Moving:
                int diffx = 1, diffy = 1;

                if (from.x == 3) {
                      diffx = 3;
                      diffy = 1;
                } else if (from.y == 3) {
                     diffx = 1;
                    diffy = 3;
                }
                if ((from.x == 1 || from.x == 5) || (from.y == 1 || from.y == 5)) {
                     diffx = diffy = 2;
                } else if ((from.x == 0 || from.x == 6) || (from.y == 0 || from.y == 6)) {
                    diffx = diffy = 3;
                }

                if (Math.abs(from.x - to.x) <= diffx && Math.abs(from.y - to.y) == 0) {
                    return true;
                } else if (Math.abs(from.x - to.x) == 0 && Math.abs(from.y - to.y) <= diffy) {
                    return true;
                } else {
                    return false;
                }
            default :
                return false;
        }
    }

    /**
     * @description Estimate who is the winner, if any.
     * @author Joel Denke
     *
     */
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

    /**
     * @description Remove marker from animation list and placeholder
     * @author Joel Denke
     *
     */
    private boolean removeMarker(Point p, int color)
    {
        int i;

        for (i = 0; i < data.markerSize; i++) {
            if (data.markers[i] != null && data.markers[i].getColor() == color) {
                Point p2 = data.markers[i].getPosition();

                if (p.equals(p2.x, p2.y)) {
                    board.removeMarker(p);
                    //stopAnimation();
                    data.markers[i] = null;
                    return true;
                }
            }
        }

        return false;
    }

    private int getValidMoves(int turn)
    {
        int i;
        int validMoves = 0;

        switch (data.state) {
            case Placing:
                validMoves = board.markEmpty();
                break;
            case Moving :
                for (i = 0; i < data.markerSize; i++) {
                    if (data.markers[i] != null && data.markers[i].getColor() == turn) {
                        Point p = data.markers[i].getPosition();
                        int j = board.markEmpty(p);
                        validMoves += j;
                    }
                }
                break;
        }

        return validMoves;
    }

    /**
     * @description When user touch interacting on screen
     * @author Joel Denke
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // See where pointer is
        Point current = new Point((int)event.getX(), (int)event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (data.marker == null || data.state == States.End) {
                    return true;
                }

                Point p = board.circleIntersecting(current, data.marker.getRadius());

                // See if we have a intersected marker and move is valid
                if (p.x != -1 && p.y != -1 && isValidMove(data.marker, p)) {
                    if (data.state == States.Removing) {
                        if (removeMarker(p, data.removeColor)) {
                            data.state = data.prevState;
                        } else {
                            viewMessage(String.format("Cannot remove from [%d, %d]", p.x, p.y), false);
                            return true;
                        }
                    } else {
                        // Move marker
                        board.moveTo(data.marker, p);
                        board.clearValid();

                        // Do we have a mill formed?
                        //viewMessage(String.format("Has %s created a mill when placing [%d, %d]",
                        //        ((data.marker.getColor() == Color.RED) ? "red" : "blue"), p.x, p.y), true);
                        if (board.doThreeInARow(p, data.marker.getColor())) {
                            viewMessage("You created a mill, click on the other players marker to remove it", true);
                            data.prevState = data.state;
                            data.state = States.Removing;
                            data.removeColor = (data.turn == Color.RED) ? Color.BLUE : Color.RED;
                            return true;
                        }
                    }

                    data.turn = (data.turn == Color.RED) ? Color.BLUE : Color.RED;
                    viewMessage("It is now " + ((data.turn == Color.RED) ? "red" : "blue") + " turn", true);


                    /*if (getValidMoves(data.turn) == 0) {
                        data.state = States.End;
                        viewMessage(String.format("Color %s is the winner!", ((data.turn == Color.RED) ? "blue" : "red")), true);
                        return true;
                    }*/

                    if (data.state == States.Placing) {
                        // Create new marker if not all is placed yet
                        if (data.currentMarker < data.markerSize - 1) {
                            data.markers[++data.currentMarker] = createMarker(data.turn, markerStart);
                        } else {
                            data.state = States.Moving;
                        }
                    }

                    // See if anyone won?
                    int winner = winner();
                    int moves = getValidMoves(data.turn);
                    if (winner != -1 || moves == 0) {
                        if (moves == 0) {
                            winner = (data.turn == Color.RED) ? Color.BLUE : Color.RED;
                        }
                        data.state = States.End;
                        viewMessage(String.format("Color %s is the winner!", ((winner == Color.RED) ? "red" : "blue")), true);
                        return true;
                    }
                } else {
                    viewMessage(String.format("Invalid move [%d, %d]", p.x, p.y), true);
                    data.marker.move(board, board.getPlaceHolder(data.marker.getPosition().x, data.marker.getPosition().y).getCenterPoint());
                }
                stopAnimation();
                update();
                data.marker = null;
                break;
            case MotionEvent.ACTION_MOVE:
                // Move marker if in correct state
                if (data.marker == null || data.state == States.Removing || data.state == States.End) {
                    return true;
                }
                data.marker.move(board, current);
                break;
            case MotionEvent.ACTION_DOWN:
                if (data.marker != null || data.state == States.End) { // Previous animation is not finished or someone has won
                    return true;
                }

                // Get marker from current pointer coordinate
                data.marker = getMarker(current);
                start = current;

                if (data.marker == null) {
                   return true;
                }

                startAnimation();
                break;

        }
        return true;
    }

    /**
     * @description Make a smooth animation for a marker, to a new position
     * @author Joel Denke
     *
     */
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

    /**
     * @description Draws markers and game board on canvas
     * @author Joel Denke
     *
     */
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

    private void updateBoard()
    {
        int i;
        startAnimation();
        for (i = 0; i < data.markerSize; i++) {
            if (data.markers[i] != null) {
                Point p = data.markers[i].getPosition();
                PlaceHolder ph = board.getPlaceHolder(p.x, p.y);
                data.markers[i].setRadius(ph.getRadius() - 10);
                data.markers[i].move(board, ph.getCenterPoint());

                if (ph != null && ph.getMarker() == null) {
                    ph.setMarker(data.markers[i]);
                }
            }
        }
        board.clearValid();

        stopAnimation();
    }

    /**
     * @description Create a new marker
     * @author Joel Denke
     *
     */
    public Marker createMarker(int color, Point p)
    {
        return new Marker(color, p, board.getPlaceHolder(0, 0).getRadius() - 10, 1, 0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        invalidate();
        updateBoard();
        update();
    }

    /**
     * @description Update canvas when view surface is created
     * @author Joel Denke
     *
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        invalidate();
        updateBoard();
        if (data.markers[data.currentMarker] == null) {
            data.markers[data.currentMarker] = createMarker(data.turn, markerStart);
        }
        update();
    }

    /**
     * @description Make sure thread is not running when destroying view surface
     * @author Joel Denke
     *
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        stopAnimation();
    }
}
