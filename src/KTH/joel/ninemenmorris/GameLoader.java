package KTH.joel.ninemenmorris;

import android.content.Context;
import android.util.Log;

import java.io.*;

/**
 * @description Game data loader
 * @author Joel Denke
 *
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

    /**
     * @description Get file resource
     * @author Joel Denke
     *
     */
    private File getFileResource(String filename)
    {
        return new File(context.getFilesDir(), filename);
    }

    /**
     * @description Load from file and return game data
     * @author Joel Denke
     *
     */
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

        return gameData;
    }

    /**
     * @description Write serialized data to file
     * @author Joel Denke
     *
     */
    public void writeGames(GameData[] gameData)
    {
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
