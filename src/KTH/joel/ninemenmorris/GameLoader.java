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
    private GameBoard[] games;

    public GameLoader(Context context, String fileName)
    {
        this.context = context;
        this.fileName = fileName;
    }

    private File getFileResource(String filename)
    {
        return new File(context.getFilesDir(), filename);
    }

    public GameBoard[] loadGames()
    {
        File file = getFileResource(fileName);
        Log.d("load", file.getAbsoluteFile().toString());

        //file.delete();

        if (file.exists() && file.canRead()) {
            BufferedReader reader = null;
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                games = (GameBoard[])ois.readObject();
                Log.d("load", "Successfully read from file");
            } catch (Exception fe) {
                Log.d("load", "" + fe.getMessage());
                games = new GameBoard[5];
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {}
                }
            }
        } else {
            games = new GameBoard[5];
        }

        return games;
    }

    /**
     * @description Write serialized data to file and send current datetime
     * @author Joel Denke
     *
     */
    public void writeGames(GameBoard[] games)
    {
        if (games == null) {
            return;
        }

        for (GameBoard board: games) {
            if (board != null) {
                board.stopAnimations();
            }
        }
        File file = getFileResource(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d("load", "Failed create new file");
            }
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(games);
            oos.flush(); // flush the stream to insure all of the information was written to file
            oos.close();
            Log.d("load", "Successfully wrote to file");
            //activity.flashMessage("Successfully wrote currency data to file");
        } catch (Exception e) {
            Log.d("load", "Failed write games to file");
        }
    }
}
