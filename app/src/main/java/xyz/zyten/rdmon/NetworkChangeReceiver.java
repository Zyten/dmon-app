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


public class NetworkChangeReceiver extends BroadcastReceiver{

    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(checkInternet(context))
        {
            Log.i (TAG, "InternetAvailable = true");
            MainActivity.InternetAvailable = true;//Toast.makeText(context, "Network Available Do operations",Toast.LENGTH_LONG).show();
        }
        else{
            MainActivity.InternetAvailable = true;
        }


        //boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        //boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        /*if(!isConnected){
            Log.i ("Tag", "Network Connected");
            //Toast.makeText(context, "Connected to Network", Toast.LENGTH_SHORT).show();
            if (isInternetAvailable()) {
                Log.i ("Tag", "Connected to Internet");
                //Toast.makeText(context, "Connected to Internet", Toast.LENGTH_SHORT).show();
            }

            else {
                Log.i ("Tag", "No Internet Connection");
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Log.i ("Tag", "Network Not Connected");
            //Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Not Connected to Network", Toast.LENGTH_SHORT).show();

        }*/
    }

    boolean checkInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        ServiceManager serviceManager = new ServiceManager(context);

        if(!isConnected){
            Log.i (TAG, "Connected to Network");
            if (serviceManager.isInternetAvailable()) {
                Log.i (TAG, "Connected to Internet");
                return true;
            }
            else{
                return false;
            }
        }
        else
            return false;


        /*if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }*/
    }


}