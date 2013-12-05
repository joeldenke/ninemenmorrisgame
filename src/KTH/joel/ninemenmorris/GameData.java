package KTH.joel.ninemenmorris;

import android.graphics.Color;

import java.io.Serializable;

/**
 * @description Metadata/State of the game, to store in file
 * @author Joel Denke
 *
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
        currentMarker = 0;
        marker = null;
    }
}
