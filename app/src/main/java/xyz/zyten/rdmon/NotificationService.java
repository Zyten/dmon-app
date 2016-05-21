package xyz.zyten.rdmon;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by zyten on 18/5/2016.
 */
public class NotificationService extends IntentService {

    private static final String TAG = "NotificationService";
    // Must create a default constructor
    int isSensitive, doesExercise, rangeID, API;
    String heartPrecaution ="",exercisePreacaution="", generalPrecaution="";

    public NotificationService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        Context context = getApplicationContext();
        Log.d("NotificationService", "Service running");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        Log.e("TAG", "Notif triggered!");
        FetchThingSpeak();


    }

    private void FetchThingSpeak(){

        String response = getLatestFeed();

        if(response == null) {
            Log.e(TAG, "null");
            return;
        }

        if(response == "no_internet") {
            Log.e(TAG, "no_internet");
            return;
        }

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
            double dust = channel.getDouble("field3");

            API = getAPI(BigDecimal.valueOf(dust).floatValue());

            try {
                SharedPreferences getpref = getSharedPreferences("myProfile", 0);
                isSensitive = getpref.getInt("isSensitive", 0);
                doesExercise = getpref.getInt("doesExercise", 0);
            }
            catch(NullPointerException ex){
                Log.e("myProfile", ex.getMessage());}

            setRangeID();
            notifyPrecaution();

        } catch (JSONException e) {
            e.printStackTrace();
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
    private void setRangeID(){
        if(API < 51)
            rangeID=0;
        else if(API < 101)
            rangeID=1;
        else if (API < 151)
            rangeID = 2;
        else
            rangeID = -1;
    }

    private void notifyPrecaution()
    {
        if(rangeID == 2) { //Unhealthy
            if (isSensitive == 1) {
                heartPrecaution ="There is a slight chance of respiratory distress in people with health sensitivities. Please prepare accordingly.";
                pushNotification("Unhealthy Air Quality", heartPrecaution);
            }
            else if (doesExercise == 1) {
                exercisePreacaution="Our recommendation: search for a cleaner area for a physical outdoor activity.";
                pushNotification("Unhealthy Air Quality", exercisePreacaution);
            }
            //if (isSensitive != 1 && doesExercise !=1){
            else {
                generalPrecaution="You can still go outside but you should continue tracking the air quality around you.";
                pushNotification("Unhealthy Air Quality", generalPrecaution);
            }
        }
        else
            Log.e("Tag", "API Range < 150");
    }

    private void pushNotification(String title, String desc){

        NotificationManager notificationManager;
        Notification mNotification;
        PendingIntent mPendingIntent;

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle(title);
        mBuilder.setTicker(title);
        //mBuilder.setSubText("Be careful");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(desc));
        //mBuilder.setContentText(desc);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_big);
        mBuilder.setContentIntent(mPendingIntent);
        mBuilder.setOngoing(false);

        //API level 16

        /*mBuilder.setNumber(150);
        mBuilder.build();*/
        mNotification = mBuilder.getNotification();
        notificationManager.notify(11, mNotification);
    }
}