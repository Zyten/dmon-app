package xyz.zyten.rdmon;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zyten on 13/5/2016.
 */
public class ServiceManager extends ContextWrapper {

    private static final String TAG = "ServiceManager";
    public ServiceManager(Context base) {
        super(base);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public boolean isInternetAvailable()
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
