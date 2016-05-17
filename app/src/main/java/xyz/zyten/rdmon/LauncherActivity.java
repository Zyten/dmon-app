package xyz.zyten.rdmon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zyten on 8/4/2016.
 */
public class LauncherActivity extends Activity {

    SharedPreferences gettemppref;
    private static final String TAG = "LauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        gettemppref = getSharedPreferences(LoginActivity.TEMP, 0);
        boolean logged_in = gettemppref.getBoolean("logged_in", false);

        if (logged_in) {
            Log.e(TAG, "logged in = true!");
            finish();
            Intent main = new Intent(LauncherActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            LauncherActivity.this.startActivity(main);
            LauncherActivity.this.finish();
        }
        else{
            Log.e(TAG, "logged in = false!");
            finish();
            Intent login = new Intent(LauncherActivity.this, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            LauncherActivity.this.startActivity(login);
            LauncherActivity.this.finish();
        }

    }


}
