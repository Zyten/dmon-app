package xyz.zyten.rdmon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

/**
 * Created by zyten on 8/4/2016.
 */
public class LauncherActivity extends Activity {
    //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    protected void onCreate() {

        File f = new File("/data/data/com.eventrid.scanner/shared_prefs/Eventrid.xml");

        SharedPreferences gettemppref = getSharedPreferences(LoginActivity.TEMP, 0);
        boolean logged_in = gettemppref.getBoolean("logged_in", false);
        //if(f.exists())

        if (logged_in) {
            Intent main = new Intent(LauncherActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            LauncherActivity.this.startActivity(main);
            LauncherActivity.this.finish();
        }
        else{
            Intent login = new Intent(LauncherActivity.this, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            LauncherActivity.this.startActivity(login);
            LauncherActivity.this.finish();
        }

    }
}
