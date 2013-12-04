package KTH.joel.ninemenmorris;

import android.graphics.Color;
import android.graphics.Point;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-12-04
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class GameData implements Serializable
{
    public Marker markers[];
    public int currentMarker, markerSize = 18;
    public int removeColor;
    public int turn;
    public States state;
    public States prevState;
    public Marker marker;


    public GameData()
    {
        turn = Color.BLUE;
        state = States.Placing;
        prevState = States.Placing;

        markers = new Marker[markerSize];
        // Always put new markers is empty space
        currentMarker = 0;
        marker = null;
    }
}
