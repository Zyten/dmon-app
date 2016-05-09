package xyz.zyten.rdmon;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    protected TextView tempTextView, humidityTextView, dustTextView, APITextView, lastupdateTextView,
            HeartPrecautionTextView,ExercisePrecautionTextView,GeneralPrecautionTextView, AirDescTextView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Precaution> precautions;
    Boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //toolbar.setNavigationIcon(R.drawable.refresh);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        tempTextView = (TextView) findViewById(R.id.tempTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        dustTextView = (TextView) findViewById(R.id.dustTextView);
        APITextView = (TextView) findViewById(R.id.APITextView);
        lastupdateTextView = (TextView) findViewById(R.id.lastupdateTextView);
        HeartPrecautionTextView = (TextView) findViewById(R.id.HeartPrecautionTextView);
        ExercisePrecautionTextView = (TextView) findViewById(R.id.ExercisePrecautionTextView);
        GeneralPrecautionTextView = (TextView) findViewById(R.id.GeneralPrecautionTextView);
        AirDescTextView = (TextView) findViewById(R.id.AirDescTextView);
        //Get precautions desc from xml array
        Resources res = getResources();
        String[] desc = res.getStringArray(R.array.desc);
        Integer temp=-1; //holds array index for desc

        //Programmatically create precaution objects into ArrayList
        precautions = new ArrayList<Precaution>();
        for(Integer i = 0; i<3;i++)
            for(Integer j =0; j<3;j++) {
                temp++;
                precautions.add(new Precaution(i, j, desc[temp]));
            }
        //Range ID: 0 - Good, 1 - Moderate, 2 - Unhealthy
        //HealthID: 0 - isSemsitive, 1 - doesExercise, 2 - None
    }

   @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        //paused = true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        /*paused = false;
        //simulate doing something
        if(!paused)
            doTheAutoRefresh();*/

    }

    @Override
    public void onStop() {
        super.onStop();  // Always call the superclass method first
        paused = true;
    }

    private final Handler handler = new Handler();

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new FetchThingspeakTask().execute();
                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show(); // this is where you put your refresh code
                doTheAutoRefresh();
            }
        }, 15000); //1000 ms = 1 s
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            //textInfo.setText("WAIT: doing something");
            new FetchThingspeakTask().execute();


            //simulate doing something
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(MainActivity.this, "DONE", Toast.LENGTH_SHORT).show();
                }

            }, 2000);
        }};
    //public void sendStream(View v){new getStreamAsyncTask().execute();}

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        new FetchThingspeakTask().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_personal_profile) {
            Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
            profile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(profile);
        } else if (id == R.id.nav_health_profile) {
            Intent hprofile = new Intent(MainActivity.this, HealthProfileActivity.class);
            hprofile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(hprofile);
        } else if (id == R.id.nav_history) {
            Intent history = new Intent(MainActivity.this, HistoryActivity.class);
            history.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(history);
        } else if (id == R.id.nav_air) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_signout) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                SharedPreferences settemppref = getSharedPreferences(LoginActivity.TEMP, Context.MODE_PRIVATE);
                SharedPreferences.Editor tempEditor = settemppref.edit();
                tempEditor.putBoolean("logged_in", false);
                tempEditor.commit();
                //mGoogleApiClient.connect();  //may not be needed*/
                }

            /*Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                        }

                    });*/
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(login);
            MainActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.quit)
                    .setMessage(R.string.really_quit)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Stop the activity
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    protected class Precaution{
        private Integer rangeID;
        private Integer healthID;
        private String desc;


        public Precaution(){

        }

        public Precaution(Integer rangeID, Integer healthID, String desc){
            this.rangeID = rangeID;
            this.healthID= healthID;
            this.desc = desc;
        }

        public Integer getRangeID(){
            return rangeID;
        }

        public Integer getHealthID(){
            return healthID;
        }

        public String getPrecaution(){
            return this.desc;
        }
    }

    class FetchThingspeakTask extends AsyncTask<Void, Void, String> {

    Context context;

        protected void onPreExecute() {

            tempTextView.setText("");
            humidityTextView.setText("");
            dustTextView.setText("");
        }

        protected String doInBackground(Void... urls) {
            if(isNetworkAvailable()){
                if (isInternetAvailable()) {
                    Log.i ("Tag", "Internet Connected");
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
                        }
                        finally{
                            urlConnection.disconnect();
                        }
                    }
                    catch(Exception e) {
                        Log.e("ERROR", e.getMessage(), e);
                        return null;
                    }
                }

                else {
                    Log.i ("Tag", "No Internet Connection");
                    return "no_internet";
                }
            }
            else {
                Log.i ("Tag", "Network Not Connected");
                return "no_internet";
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                return;
            }

            if(response == "no_internet") {
                Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                double temp = channel.getDouble("field1");
                double humidity = channel.getDouble("field2");
                double dust = channel.getDouble("field3");
                int API = channel.getInt("field4");

                tempTextView.setText(String.valueOf(temp)+ getString(R.string.unit_temp));
                humidityTextView.setText(String.valueOf(humidity)+ getString(R.string.unit_humidity));
                dustTextView.setText(String.valueOf(dust)+ getString(R.string.unit_dust));
                APITextView.setText(String.valueOf(API));
                Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                String DateToStr = format.format(curDate);
                lastupdateTextView.setText(DateToStr);

                Log.e(TAG, String.valueOf(temp) + String.valueOf(humidity) + String.valueOf(dust));

                Integer rangeID;

                if(API < 51)
                    rangeID=0;
                else if(API < 101)
                    rangeID=1;
                else if (API < 151)
                    rangeID = 2;
                else
                    rangeID = -1;

                Integer isSensitive = 0;
                Integer doesExercise = 0;

                try {
                    SharedPreferences getpref = getSharedPreferences("myProfile", 0);
                    isSensitive = getpref.getInt("isSensitive", 0);
                    doesExercise = getpref.getInt("doesExercise", 0);
                }
                catch(NullPointerException ex){
                    Log.e("myProfile", ex.getMessage());}

                HeartPrecautionTextView.setVisibility(View.GONE);
                ExercisePrecautionTextView.setVisibility(View.GONE);

                if(rangeID == 0) { //Good
                    if (isSensitive == 1) {
                        HeartPrecautionTextView.setText(precautions.get(0).getPrecaution());
                        HeartPrecautionTextView.setVisibility(View.VISIBLE);
                    }
                    if (doesExercise == 1){
                        ExercisePrecautionTextView.setText(precautions.get(1).getPrecaution());
                        ExercisePrecautionTextView.setVisibility(View.VISIBLE);}
                    if (isSensitive != 1 && doesExercise !=1){
                        HeartPrecautionTextView.setText("");
                        ExercisePrecautionTextView.setText("");
                        HeartPrecautionTextView.setVisibility(View.GONE);
                        ExercisePrecautionTextView.setVisibility(View.GONE);
                    }
                    GeneralPrecautionTextView.setText(precautions.get(2).getPrecaution());
                    AirDescTextView.setText("EXCELLENT AIR QUALITY");
                }
                else if(rangeID == 1) { //Moderate
                    if (isSensitive == 1){
                        HeartPrecautionTextView.setText(precautions.get(3).getPrecaution());
                        HeartPrecautionTextView.setVisibility(View.VISIBLE);}
                    if (doesExercise == 1) {
                        ExercisePrecautionTextView.setText(precautions.get(4).getPrecaution());
                        ExercisePrecautionTextView.setVisibility(View.VISIBLE);}
                    if (isSensitive != 1 && doesExercise !=1){
                        HeartPrecautionTextView.setText("");
                        ExercisePrecautionTextView.setText("");
                        HeartPrecautionTextView.setVisibility(View.GONE);
                        ExercisePrecautionTextView.setVisibility(View.GONE);
                    }
                    GeneralPrecautionTextView.setText(precautions.get(5).getPrecaution());
                    AirDescTextView.setText("MODERATE AIR QUALITY");
                }
                else if(rangeID == 2) { //Unhealthy
                    if (isSensitive == 1) {
                        HeartPrecautionTextView.setText(precautions.get(6).getPrecaution());
                        HeartPrecautionTextView.setVisibility(View.VISIBLE);}
                    if (doesExercise == 1) {
                        ExercisePrecautionTextView.setText(precautions.get(7).getPrecaution());
                        ExercisePrecautionTextView.setVisibility(View.VISIBLE);}
                    if (isSensitive != 1 && doesExercise !=1){
                        HeartPrecautionTextView.setText("");
                        ExercisePrecautionTextView.setText("");
                        HeartPrecautionTextView.setVisibility(View.GONE);
                        ExercisePrecautionTextView.setVisibility(View.GONE);
                    }
                    GeneralPrecautionTextView.setText(precautions.get(8).getPrecaution());
                    AirDescTextView.setText("UNHEALTHY AIR QUALITY");
                }
                else
                    Log.d("Tag", "Invalid rangeID");


                /*int colorFrom = ContextCompat.getColor(context, R.color.mgreen);
                int colorTo = ContextCompat.getColor(context, R.color.myellow);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(2500); // milliseconds
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        findViewById(R.id.main_content).setBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
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


