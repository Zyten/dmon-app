package xyz.zyten.rdmon;

/**
 * Created by zyten on 29/3/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


public class NetworkChangeReceiver extends BroadcastReceiver{

    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "Fired",Toast.LENGTH_LONG).show();
        if(checkInternet(context))
        {
            Log.i (TAG, "InternetAvailable = true");
            MainActivity.InternetAvailable = true;
        }
        else{
            Log.i (TAG, "InternetAvailable = false");
            MainActivity.InternetAvailable = false;
        }
    }

    protected static boolean checkInternet(Context context) {

        ServiceManager serviceManager = new ServiceManager(context);

        if(serviceManager.isNetworkAvailable()){
            Log.i (TAG, "Connected to Network");
            return true;
            /*
            if (serviceManager.isInternetAvailable()) {
                Log.i (TAG, "Connected to Internet");
                return true;
            }
            else{
                return false;
            }*/
        }
        else
            return false;
    }


}