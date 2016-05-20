package xyz.zyten.rdmon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;

/**
 * Created by zyten on 20/5/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    static Boolean notifyMe;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();

        setTheme(R.style.PreferenceTheme);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();}

        return super.onOptionsItemSelected(item);
    }

    public void save(View v) {

        try {
            SharedPreferences setpref = SettingsActivity.this.getSharedPreferences("myProfile", Context.MODE_PRIVATE);
            SharedPreferences.Editor profileEditor = setpref.edit();
            profileEditor.putBoolean("notifyMe", notifyMe);
            profileEditor.commit();

            if(notifyMe == true)
                startNotificationService();
            else
                stopNotificationService();

            Toast.makeText(SettingsActivity.this, "Settings updated.", Toast.LENGTH_SHORT).show();
        }
        catch (NullPointerException ex){
            Toast.makeText(SettingsActivity.this, "Failed to update settings.", Toast.LENGTH_SHORT).show();
            Log.e("TAG", "notifyMe = null");
        }
            Intent main = new Intent(SettingsActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            SettingsActivity.this.startActivity(main);

    }

    private void startNotificationService(){
        Intent NotificationStartService = new Intent(SettingsActivity.this, NotificationStartService.class);
        SettingsActivity.this.startService(NotificationStartService);
    }

    private void stopNotificationService(){
        Intent NotificationStartService = new Intent(SettingsActivity.this, NotificationStartService.class);
        SettingsActivity.this.stopService(NotificationStartService);

        Intent intentstop = new Intent(this, NotificationAlarmReceiver.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this,
                NotificationAlarmReceiver.REQUEST_CODE, intentstop, 0);
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManagerstop.cancel(senderstop);
        Log.e("TAG", "NotificationService Alarm disabled");
    }
}