package xyz.zyten.rdmon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zyten on 18/5/2016.
 */
public class OnBootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, NotificationStartService.class);
            context.startService(serviceIntent);
        }
        else{
            Intent serviceIntent = new Intent(context, NotificationStartService.class);
            context.startService(serviceIntent);
        }
    }
}