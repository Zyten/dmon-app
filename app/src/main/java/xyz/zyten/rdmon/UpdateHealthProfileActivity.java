package xyz.zyten.rdmon;

/**
 * Created by zyten on 1/4/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UpdateHealthProfileActivity extends AsyncTask<Integer, Void, String> {

    private Context context;
    private Integer isSensitive, doesExercise;
    private String googleID;

    public UpdateHealthProfileActivity(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //get sharedPreferences here
        SharedPreferences getpref = context.getSharedPreferences("myProfile", Context.MODE_PRIVATE);

        googleID = getpref.getString("googleID", "");
    }

    @Override
    protected String doInBackground(Integer... arg0) {
        isSensitive = arg0[0];
        doesExercise = arg0[1];

        Log.d("TAG", isSensitive.toString());
        Log.d("TAG", doesExercise.toString());

        String link;
        String data;
        BufferedReader bufferedReader;
        String result;

        try {
            data = "?isSensitive=" + URLEncoder.encode(isSensitive.toString(), "UTF-8");
            data += "&doesExercise=" + URLEncoder.encode(doesExercise.toString(), "UTF-8");
            data += "&googleID=" + URLEncoder.encode(googleID, "UTF-8");

            link = "http://zyten.xyz/testo/updateHProfile.php" + data;
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = bufferedReader.readLine();
            return result;
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        String jsonStr = result;
        if (jsonStr != null) {

            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String query_result = jsonObj.getString("query_result");
                if (query_result.equals("SUCCESS")) {
                    SharedPreferences setpref = context.getSharedPreferences("myProfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor hprofileEditor = setpref.edit();
                    hprofileEditor.putInt("isSensitive", isSensitive);
                    hprofileEditor.putInt("doesExercise", doesExercise);
                    hprofileEditor.commit();

                    Toast.makeText(context, "Health profile updated.", Toast.LENGTH_SHORT).show();
                } else if (query_result.equals("FAILURE")) {
                    Toast.makeText(context, "Update failed.", Toast.LENGTH_SHORT).show();
                } else if (query_result.equals("REACHED")) {
                    Toast.makeText(context, "Reached.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Couldn't connect to remote database.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();
        }
    }
}