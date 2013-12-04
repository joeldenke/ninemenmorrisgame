package KTH.joel.ninemenmorris;

import android.graphics.Color;
import android.graphics.Point;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-12-04
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class GameData
{
    public Marker markers[];
    public int currentMarker, markerSize = 18;
    public int removeColor;
    public Board board;
    public Point start;
    public int turn;
    public States state;
    public States prevState;
    public Marker marker;
    public Point markerStart;

    private GameBoard game;

    public GameData(GameBoard game, int width, int height)
    {
        this.game = game;
        board = new Board(Color.BLACK, 0, 0, width, height);
        turn = Color.BLUE;
        state = States.Placing;
        prevState = States.Placing;

        markers = new Marker[markerSize];
        // Always put new markers is empty space
        markerStart = board.getPlaceHolder(1, 0).getCenterPoint();
        currentMarker = 0;
        marker = null;

        if (markers[currentMarker] == null) {
            markers[currentMarker] = createMarker(turn, markerStart);
        }
        markers[currentMarker].update();
    }

    public Marker createMarker(int color, Point p)
    {
        Marker marker =  new Marker(game.getHolder(), game, color, p, board.getPlaceHolder(0, 0).getRadius() - 10);
        marker.update();
        return marker;
    }
}
