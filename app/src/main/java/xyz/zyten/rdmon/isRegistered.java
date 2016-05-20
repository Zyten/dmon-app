package xyz.zyten.rdmon;

/**
 * Created by zyten on 1/4/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class isRegistered {
    private Context context;
    private static final String TAG = "isRegistered";
    private Integer registeredStatus;

    public isRegistered(Context context) {
        this.context = context;
    }

    protected String sendQuery(String... arg0) {
        String googleID = arg0[0];

        String link;
        String data;
        BufferedReader bufferedReader;
        String query;

            try {
                data = "?&googleID=" + URLEncoder.encode(googleID, "UTF-8");

                link = "http://zyten.xyz/testo/isRegistered.php" + data;
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                query = bufferedReader.readLine();
                getResult(query);

                return "SUCCESS";

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
    }

    protected int getResult(String query) {
        String jsonStr = query;
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String query_result = jsonObj.getString("query_result");
                if (query_result.equals("SUCCESS")) {
                    registeredStatus = 1;
                } else if (query_result.equals("FAILURE")) {
                    registeredStatus = 0;
                } else {
                    registeredStatus = -1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                registeredStatus = -1;
            }
        } else {
            registeredStatus=-1;
        }
        return registeredStatus;
    }

    protected Integer getRegisteredStatus(){
        return registeredStatus;
    }
}