package xyz.zyten.rdmon;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.support.v7.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    protected TextView mUserTextView, mEmailTextView, tempTextView, humidityTextView, dustTextView, APITextView, lastupdateTextView,
            HeartPrecautionTextView,ExercisePrecautionTextView,GeneralPrecautionTextView, AirDescTextView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Precaution> precautions;
    Boolean paused = false;
    Integer rangeID;
    Integer isSensitive = 0;
    Integer doesExercise = 0;
    Integer API = 0;
    public static Boolean InternetAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        initViews();
        initPrecautions();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "Started");
        paused = false;
        startNotificationService();
        mGoogleApiClient.connect();
        doTheAutoRefresh();
        //new FetchThingSpeakTask(MainActivity.this, new FetchThingSpeakTaskCompleteListener()).execute();
    }

   @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
       Log.e(TAG, "Paused");
       paused = true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.e(TAG, "Resumed");
        paused = false;
    }

    @Override
    public void onStop() {
        super.onStop();  // Always call the superclass method first
        Log.e(TAG, "Stopped");
        paused = true;
        startNotificationService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass method first
        Log.e(TAG, "Dead");
        paused = true;
        startNotificationService();
    }
    private final Handler handler = new Handler();

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //new FetchThingspeakTask().execute();
                if(paused != true)
                    new FetchThingSpeakTask(MainActivity.this, new FetchThingSpeakTaskCompleteListener()).execute();
                doTheAutoRefresh();
            }
        }, 60000); //1000 ms = 1 s
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            //textInfo.setText("WAIT: doing something");
            new FetchThingSpeakTask(MainActivity.this, new FetchThingSpeakTaskCompleteListener()).execute();


            //simulate doing something
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(MainActivity.this, "DONE", Toast.LENGTH_SHORT).show();
                }

            }, 2000);
        }};

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
            gotoActivity(ProfileActivity.class);
        } else if (id == R.id.nav_health_profile) {
            gotoActivity(HealthProfileActivity.class);
        } else if (id == R.id.nav_history) {
            gotoActivity(HistoryActivity.class);
        } else if (id == R.id.nav_air) {
            gotoActivity(ApiGuidelineActivity.class);
        } else if (id == R.id.nav_settings) {
            gotoActivity(SettingsActivity.class);
        }
        else if (id == R.id.nav_signout) {
            signout();
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

    private void initLayout(){
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

        SharedPreferences getpref = getSharedPreferences("GProfile", Context.MODE_PRIVATE);

        String username = getpref.getString("username", "");
        String email = getpref.getString("email", "");

        mUserTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userName);
        mEmailTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        mUserTextView.setText(username);
        mEmailTextView.setText(email);

    }

    private void initViews(){

        tempTextView = (TextView) findViewById(R.id.tempTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        dustTextView = (TextView) findViewById(R.id.dustTextView);
        APITextView = (TextView) findViewById(R.id.APITextView);
        lastupdateTextView = (TextView) findViewById(R.id.lastupdateTextView);
        HeartPrecautionTextView = (TextView) findViewById(R.id.HeartPrecautionTextView);
        ExercisePrecautionTextView = (TextView) findViewById(R.id.ExercisePrecautionTextView);
        GeneralPrecautionTextView = (TextView) findViewById(R.id.GeneralPrecautionTextView);
        AirDescTextView = (TextView) findViewById(R.id.AirDescTextView);

        View HeartPrecaution = findViewById(R.id.HeartPrecaution);
        View ExercisePrecaution = findViewById(R.id.ExercisePrecaution);
        HeartPrecaution.setVisibility(View.GONE);
        ExercisePrecaution.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    private void gotoActivity(Class myClass){
        MainActivity.this.startActivity(new Intent(MainActivity.this, myClass).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void signout(){
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

    private void startNotificationService(){
        SharedPreferences gettemppref;
        gettemppref = getSharedPreferences("myProfile", 0);
        Boolean notifyMe = gettemppref.getBoolean("notifyMe", false);

        if(notifyMe){
            Intent NotificationStartService = new Intent(MainActivity.this, NotificationStartService.class);
            MainActivity.this.startService(NotificationStartService);
            Log.e(TAG,"Triggered NotifyService");
        }
        else
            Log.e(TAG,"NotifyService off");
    }

    private void initPrecautions(){
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

    public class FetchThingSpeakTaskCompleteListener implements AsyncTaskCompleteListener<SensorData>
    {

        public FetchThingSpeakTaskCompleteListener(){

        }

        @Override
        public void onTaskComplete(SensorData mydata)
        {
            if(mydata.toastMessage == "Updated") {
                lastupdateTextView.setText(mydata.lastupdateText);
                API = mydata.API;
                tempTextView.setText(mydata.tempText);
                humidityTextView.setText(mydata.humidityText);
                dustTextView.setText(mydata.dustText);
                APITextView.setText(mydata.APIText);

                Log.e(TAG, "Received: " + String.valueOf(mydata.tempText) + String.valueOf(mydata.humidityText) + String.valueOf(mydata.dustText));

                isSensitive = mydata.isSensitive;
                doesExercise = mydata.doesExercise;

                // do something with the result
                View HeartPrecaution = findViewById(R.id.HeartPrecaution);
                View ExercisePrecaution = findViewById(R.id.ExercisePrecaution);
                HeartPrecaution.setVisibility(View.GONE);
                ExercisePrecaution.setVisibility(View.GONE);

                setRangeID();
                setPrecaution(HeartPrecaution, ExercisePrecaution);
                updateUI(rangeID);
            }
            else{
                Log.e(TAG, "failed");
            }

            Toast.makeText(MainActivity.this, String.valueOf(mydata.toastMessage), Toast.LENGTH_SHORT).show();
        }
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

    private void setPrecaution(View HeartPrecaution, View ExercisePrecaution)
    {
        if(rangeID == 0) { //Good
            if (isSensitive == 1) {
                HeartPrecautionTextView.setText(precautions.get(0).getPrecaution());
                HeartPrecaution.setVisibility(View.VISIBLE);
            }
            if (doesExercise == 1){
                ExercisePrecautionTextView.setText(precautions.get(1).getPrecaution());
                ExercisePrecaution.setVisibility(View.VISIBLE);}
            if (isSensitive != 1 && doesExercise !=1){
                HeartPrecautionTextView.setText("");
                ExercisePrecautionTextView.setText("");
                HeartPrecaution.setVisibility(View.GONE);
                ExercisePrecaution.setVisibility(View.GONE);
            }
            GeneralPrecautionTextView.setText(precautions.get(2).getPrecaution());
            AirDescTextView.setText("EXCELLENT AIR QUALITY");
        }
        else if(rangeID == 1) { //Moderate
            if (isSensitive == 1){
                HeartPrecautionTextView.setText(precautions.get(3).getPrecaution());
                HeartPrecaution.setVisibility(View.VISIBLE);}
            if (doesExercise == 1) {
                ExercisePrecautionTextView.setText(precautions.get(4).getPrecaution());
                ExercisePrecaution.setVisibility(View.VISIBLE);}
            if (isSensitive != 1 && doesExercise !=1){
                HeartPrecautionTextView.setText("");
                ExercisePrecautionTextView.setText("");
                HeartPrecaution.setVisibility(View.GONE);
                ExercisePrecaution.setVisibility(View.GONE);
            }
            GeneralPrecautionTextView.setText(precautions.get(5).getPrecaution());
            AirDescTextView.setText("MODERATE AIR QUALITY");
        }
        else if(rangeID == 2) { //Unhealthy
            if (isSensitive == 1) {
                HeartPrecautionTextView.setText(precautions.get(6).getPrecaution());
                HeartPrecautionTextView.setVisibility(View.VISIBLE);
            }
            if (doesExercise == 1) {
                ExercisePrecautionTextView.setText(precautions.get(7).getPrecaution());
                ExercisePrecaution.setVisibility(View.VISIBLE);}
            if (isSensitive != 1 && doesExercise !=1){
                HeartPrecautionTextView.setText("");
                ExercisePrecautionTextView.setText("");
                HeartPrecaution.setVisibility(View.GONE);
                ExercisePrecaution.setVisibility(View.GONE);
            }
            GeneralPrecautionTextView.setText(precautions.get(8).getPrecaution());
            AirDescTextView.setText("UNHEALTHY AIR QUALITY");
        }
        else
            Log.d("Tag", "Invalid rangeID");
    }

    private void updateUI(Integer rangeID){
        int mcolor, tcolor, dcolor;

        if (rangeID== 0){ //Good
            mcolor = R.color.mblue;
            tcolor = R.color.tblue;
            dcolor = R.color.descblue;
        }

        else if(rangeID == 1){ //Moderate
            mcolor = R.color.mgreen;
            tcolor = R.color.tgreen;
            dcolor = R.color.descgreen;
        }
        else if (rangeID== 2){ //Unhealthy
            mcolor = R.color.myellow;
            tcolor = R.color.tyellow;
            dcolor = R.color.descyellow;
        }

        else{
            mcolor = R.color.mgreen;
            tcolor = R.color.tgreen;
            dcolor = R.color.descgreen;
        }
        animateBackground(mcolor, tcolor, dcolor);
    }

    private void animateBackground(int mc, int tc, int dc ){

        int color = Color.TRANSPARENT;
        Drawable background = findViewById(R.id.content_main).getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        int colorFrom = color;
        int colorTo = getApplicationContext().getResources().getColor(mc);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(2500); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                findViewById(R.id.content_main).setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        int tcolor = Color.TRANSPARENT;
        Drawable tbackground = findViewById(R.id.toolbar).getBackground();
        if (background instanceof ColorDrawable)
            tcolor = ((ColorDrawable) tbackground).getColor();
        int tcolorFrom = tcolor;
        int tcolorTo = getApplicationContext().getResources().getColor(tc);

        ValueAnimator tcolorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), tcolorFrom, tcolorTo);
        tcolorAnimation.setDuration(2500); // milliseconds
        tcolorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator tanimator) {
                findViewById(R.id.toolbar).setBackgroundColor((int) tanimator.getAnimatedValue());
            }
        });

        int dcolor = Color.TRANSPARENT;
        Drawable dbackground = findViewById(R.id.desc).getBackground();
        if (background instanceof ColorDrawable)
            dcolor = ((ColorDrawable) dbackground).getColor();
        int dcolorFrom = dcolor;
        int dcolorTo = getApplicationContext().getResources().getColor(dc);

        ValueAnimator dcolorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), dcolorFrom, dcolorTo);
        dcolorAnimation.setDuration(2500); // milliseconds
        dcolorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator danimator) {
                findViewById(R.id.desc).setBackgroundColor((int) danimator.getAnimatedValue());
                findViewById(R.id.todo).setBackgroundColor((int) danimator.getAnimatedValue());
            }
        });
        colorAnimation.start();
        tcolorAnimation.start();
        dcolorAnimation.start();
    }
}


/*{"channel":{
    "id":108012,
    "name":"rdmon",
    "latitude":"0.0",
    "longitude":"0.0",
    "field1":"temp",
    "field2":"humidity",
    "field3":"dust",
    "field4":"api",
    "created_at":"2016-04-13T17:04:56+08:00",
    "updated_at":"2016-05-17T17:40:01+08:00",
    "last_entry_id":183},
    "feeds":[{
        "created_at":"2016-05-17T17:00:00+08:00",
        "field1":"24.4",
        "field2":"51.2","
        field3":"95.6",
        "field4":"72.0"
    }]
}*/