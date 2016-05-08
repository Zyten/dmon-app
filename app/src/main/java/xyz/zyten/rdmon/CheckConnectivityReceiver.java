package xyz.zyten.rdmon;

/**
 * Created by zyten on 29/3/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class CheckConnectivityReceiver extends BroadcastReceiver{

    private static final String TAG = "CheckConnectivityRcvr";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Internet Connectivity Changed");

        boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(!isConnected){
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

        }
    }

    private boolean isInternetAvailable()
    {
        try
        {
            HttpURLConnection urlc = (HttpURLConnection)
                    (new URL("http://clients3.google.com/generate_204")
                            .openConnection());
            urlc.setRequestProperty("User-Agent", "Android");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();

            return (urlc.getResponseCode() == 204 &&
                    urlc.getContentLength() == 0);
            //return (Runtime.getRuntime().exec ("ping -c 1 google.com").waitFor() == 0);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}