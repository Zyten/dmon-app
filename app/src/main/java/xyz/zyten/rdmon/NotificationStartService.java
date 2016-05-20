package xyz.zyten.rdmon;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by zyten on 18/5/2016.
 */
public class NotificationStartService extends Service {

    private static final String TAG = "NotificationStartService";
    boolean notifyMe;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        //Log.e("NotificationStart", "Service running");

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        Log.e("NotificationStart", "created");
        if(chkNotifyMe()){
            scheduleAlarm();
             Log.e("TAG", "Alarm scheduled!");
        }
        else{
            Log.e("TAG", "NotifyMe = false!");
        }
    }

    public void onDestroy() {
        Log.e("NotificationStart", "destroyed - but i won't die");
        if(chkNotifyMe()){
            scheduleAlarm();
            Log.e("TAG", "Alarm scheduled!");
        }
        else{
            Log.e("TAG", "NotifyMe = false!");
        }
    }

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), NotificationAlarmReceiver.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NotificationAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1000 * 30, pIntent); // Millisec * Second * Minute
    }

    private boolean chkNotifyMe(){
        SharedPreferences gettemppref;
        gettemppref = getSharedPreferences("myProfile", 0);
        notifyMe = gettemppref.getBoolean("notifyMe", false);

        return notifyMe;
    }
}