package xyz.zyten.rdmon;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by zyten on 20/5/2016.
 */

class FetchThingSpeakTask extends AsyncTask<Void, Context, String> {

    Context context;
    private static final String TAG = "FetchThingSpeakTask";
    private final AsyncTaskCompleteListener listener;
    private SensorData myData= new SensorData();

    public FetchThingSpeakTask(Context ctx, AsyncTaskCompleteListener listener)
    {
        this.context = ctx;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(Void... urls) {
        if(MainActivity.InternetAvailable) {
            Log.i (TAG, "Internet Connected");
            return getLatestFeed();
            //return getAveragedFeed();
        }
        else {
            Log.i (TAG, "Internet Not Connected");
            return "no_internet";
        }
    }

    @Override
    protected void onPostExecute(String response) {

        super.onPostExecute(response);
        if(response == null) {
            myData.toastMessage = "No response from server. Please try again later";
        }

        else if(response == "no_internet") {
            myData.toastMessage = "No Internet Connection";
        }

        else {
            try {
                //Sample response at End of File

                /*JSONObject channel = new JSONObject(response);
                JSONArray feedArray = channel.getJSONArray("feeds");
                JSONObject feedObject = feedArray.getJSONObject(0);

                Log.e(TAG, String.valueOf(channel));
                Log.e(TAG, String.valueOf(feedObject));
                double temp = feedObject.getDouble("field1");
                double humidity = feedObject.getDouble("field2");
                double dust = feedObject.getDouble("field3");
                String ctime = feedObject.getString("created_at");
                String updateTime = ctime.replace("T", " ").replace(":00+08:00", "");

                lastupdateTextView.setText(updateTime);*/

                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                double temp = channel.getDouble("field1");
                double humidity = channel.getDouble("field2");
                double dust = channel.getDouble("field3");
                Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                String DateToStr = format.format(curDate);
                Date currHour = toNearestWholeHour(curDate);
                String HourToStr = format.format(currHour);

                myData.lastupdateText = HourToStr;

                myData.API = getAPI(BigDecimal.valueOf(dust).floatValue());

                myData.tempText = String.valueOf(temp)+ context.getString(R.string.unit_temp);
                myData.humidityText = String.valueOf(humidity)+ context.getString(R.string.unit_humidity);
                myData.dustText = String.valueOf(dust)+ context.getString(R.string.unit_dust);
                myData.APIText = String.valueOf(myData.API);


                Log.e(TAG, String.valueOf(temp) + String.valueOf(humidity) + String.valueOf(dust));

                try {
                    SharedPreferences getpref = context.getSharedPreferences("myProfile", 0);
                    myData.isSensitive = getpref.getInt("isSensitive", 0);
                    myData.doesExercise = getpref.getInt("doesExercise", 0);
                }
                catch(NullPointerException ex){
                    Log.e("myProfile", ex.getMessage());
                    myData.isSensitive = 0;
                    myData.doesExercise = 0;
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e(TAG, String.valueOf(myData));
            myData.toastMessage = "Updated";
            listener.onTaskComplete(myData);
        }
    }

    private Date toNearestWholeHour(Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);

            /*if (c.get(Calendar.MINUTE) >= 30)
                c.add(Calendar.HOUR, 1);*/

        //Truncate to nearest hour
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    private String getAveragedFeed(){

        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date currHour = toNearestWholeHour(curDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currHour);
        calendar.add(Calendar.HOUR, -1);
        String HourToStr = format.format(currHour);
        String endtime = format.format(calendar.getTime());
        String start_time, end_time;
        try {
            start_time = URLEncoder.encode(HourToStr, "UTF-8");
            end_time = URLEncoder.encode(endtime, "UTF-8");
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
        try {
            //http://api.thingspeak.com/channels/108012/feeds.json?start=2016-05-17%2017:00&end=2016-05-17%2018:00&average=60&timezone=Asia/Kuala_Lumpur

            URL url = new URL("https://api.thingspeak.com/channels/" + 108012 +
                    "/feeds.json?start="+ start_time + "&end="+ end_time +
                    "&average=60" + "&timezone=Asia/Kuala_Lumpur");
                /*URL url = new URL("https://api.thingspeak.com/channels/" + 108012 +
                        "/feeds/last?" + "key" + "=" +
                        "MHJRONDJFO3TD3WA" + "");*/
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    private String getLatestFeed(){

        try {
            URL url = new URL("https://api.thingspeak.com/channels/" + 108012 +
                    "/feeds/last?" + "key" + "=" +
                    "MHJRONDJFO3TD3WA" + "");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    private int getAPI(float dust)
    {
        int api = 0;
        int tmp =0;
        int i =0;

        if(dust <= 0)
            api = 0;
        else if(dust <= 50)
            api = Math.round(dust);
        else if (dust <= 250){
            tmp = 60;
            for(i=5; i <=100; i+=5){
                if(dust <= tmp){
                    api = tmp - i;
                    break;
                }
                tmp += 10;
            }
        }
        else{
            api = -1;
        }
        return api;
    }
}