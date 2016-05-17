package xyz.zyten.rdmon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zyten on 18/5/2016.
 */
public class NotificationAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "xyz.zyten.rdmon.NotificationService";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}