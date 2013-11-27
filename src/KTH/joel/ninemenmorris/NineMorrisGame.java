package KTH.joel.ninemenmorris;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @description Main activity, handling convert currencies
 * @author Joel Denke
 *
 */

public class NineMorrisGame extends Activity
{
    private static final int RESULT_SETTINGS = 1;

    private final String fileName = "currencies";

    private Spinner fromSelector;
    private Spinner toSelector;
	private TextView response;
	private EditText amountInput;
	private Button convertButton;
    public static boolean FlagCancelled = false;
    private Button preferenceButton;
    private GameBoard board;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	    super.onCreate(savedInstanceState);
        initUI();
        board = new GameBoard(this);
        setContentView(board);
    }

    public void flashMessage(int resId)
    {
        flashMessage(getString(resId));
    }

    /**
     * @description Flash message on the screen
     * @author Joel Denke
     *
     */
    public void flashMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @description Initiaties GUI components
     * @author Joel Denke
     *
     */
    private void initUI()
    {
        // init the GUI
        setTitle(R.string.mainTitle);
        setContentView(R.layout.main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                showUserSettings();
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                break;
            case R.id.restartGame:
                flashMessage("Will now restart the game");
                break;

        }

        return true;
    }

    private void showUserSettings()
    {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        StringBuilder builder = new StringBuilder();

        builder.append("\n Username: "
                + sharedPrefs.getString("prefUsername", "NULL"));

        builder.append("\n Send report:"
                + sharedPrefs.getBoolean("prefSendReport", false));

        builder.append("\n Sync Frequency: "
                + sharedPrefs.getString("prefSyncFrequency", "NULL"));

        TextView settingsTextView = (TextView) findViewById(R.id.textUserSettings);

        settingsTextView.setText(builder.toString());
    }

    /**
     * @description Overrides the start method, when app is resuming.
     *              Will update to latest currency if file data is older than update frequency
     *              and if not use offline data last stored.
     *
     * @author Joel Denke
     *
     */
    @Override
    public void onStart()
    {
        Log.i("SaveActivity", "onStart called");
    	super.onStart();
    }

    /**
     * @description Listen for configuration changes and reload config as well as the gui
     * @author Joel Denke
     *
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        flashMessage(getString(R.string.changedLang) + ": " + newConfig.locale.getLanguage());

        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        initUI();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("SaveActivity", "onPause called");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.i("SaveActivity", "onStop called");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i("SaveActivity", "onDestroy called");
    }
}
