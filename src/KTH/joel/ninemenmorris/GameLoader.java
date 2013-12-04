package KTH.joel.ninemenmorris;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: joel
 * Date: 2013-12-04
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public class GameLoader
{
    private Context context;
    private String fileName;

    public GameLoader(Context context, String fileName)
    {
        this.context = context;
        this.fileName = fileName;
    }

    private File getFileResource(String filename)
    {
        return new File(context.getFilesDir(), filename);
    }

    public GameData[] loadGames()
    {
        File file = getFileResource(fileName);
        Log.d("load", file.getAbsoluteFile().toString());
        GameData[] gameData = new GameData[5];
        //file.delete();

        if (file.exists() && file.canRead()) {
            BufferedReader reader = null;
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                gameData = (GameData[])ois.readObject();
                Log.d("load", "Successfully read from file");
            } catch (Exception fe) {
                Log.d("load", "" + fe.getMessage());
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {}
                }
            }
        }

        Log.d("load", String.format("Got %d number of datainstances", gameData.length));

        return gameData;
    }

    /**
     * @description Write serialized data to file and send current datetime
     * @author Joel Denke
     *
     */
    public void writeGames(GameData[] gameData)
    {
        if (gameData == null) {
            Log.d("load", "Writing empty data ...");
        } else {
            int i;
            for (i = 0; i < gameData.length; i++) {
                if (gameData[i] == null) {
                    Log.d("load", String.format("Element %d is fucking empty ...", i+1));
                }
            }
        }

        File file = getFileResource(fileName);
        if (!file.exists()) {
            try {
                Log.d("load", "Try to create a new file");
                file.createNewFile();
            } catch (IOException e) {
                Log.d("load", "Failed create new file");
            }
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(gameData);
            oos.flush(); // flush the stream to insure all of the information was written to file
            oos.close();
            Log.d("load", "Successfully wrote to file");
            //activity.flashMessage("Successfully wrote currency data to file");
        } catch (Exception e) {
            Log.d("load", "Failed write games to file");
        }
    }
}
