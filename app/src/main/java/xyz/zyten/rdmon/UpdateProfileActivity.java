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

public class UpdateProfileActivity extends AsyncTask<String, Void, String> {
    private Context context;

    String username, gender, birthday, hometown, currResidence, googleID;

    public UpdateProfileActivity(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... arg0) {
        username = arg0[0];
        gender = arg0[1];
        birthday = arg0[2];
        hometown = arg0[3];
        currResidence = arg0[4];
        googleID = arg0[5];

        String link;
        String data;
        BufferedReader bufferedReader;
        String result;

        try {
            data = "?username=" + URLEncoder.encode(username, "UTF-8");
            data += "&gender=" + URLEncoder.encode(gender, "UTF-8");
            data += "&birthday=" + URLEncoder.encode(birthday, "UTF-8");
            data += "&hometown=" + URLEncoder.encode(hometown, "UTF-8");
            data += "&currResidence=" + URLEncoder.encode(currResidence, "UTF-8");
            data += "&googleID=" + URLEncoder.encode(googleID, "UTF-8");

            link = "http://zyten.xyz/testo/register.php" + data;
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
                    SharedPreferences.Editor profileEditor = setpref.edit();
                    profileEditor.putBoolean("profile_complete", true);
                    profileEditor.putString("googleID", googleID);
                    profileEditor.putString("username", username);
                    profileEditor.putString("gender", gender);
                    profileEditor.putString("birthday", birthday);
                    profileEditor.putString("hometown", hometown);
                    profileEditor.putString("currResidence", currResidence);
                    profileEditor.commit();

                    SharedPreferences settemppref = context.getSharedPreferences("temp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor tempEditor = settemppref.edit();
                    tempEditor.putBoolean("newUser", false);
                    tempEditor.commit();

                    Toast.makeText(context, "Personal profile updated.", Toast.LENGTH_SHORT).show();
                } else if (query_result.equals("FAILURE")) {
                    Toast.makeText(context, "Update 233537878failed.", Toast.LENGTH_SHORT).show();
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