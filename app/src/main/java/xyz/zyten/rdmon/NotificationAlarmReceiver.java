package xyz.zyten.rdmon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zyten on 18/5/2016.
 */
public class NotificationAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationAlarmRecvr"; //Exceeded char limit for TAG
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "xyz.zyten.rdmon.NotificationService";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Received Notif Alarm!");
        Intent i = new Intent(context, NotificationService.class);
        context.startService(i);
    }
}