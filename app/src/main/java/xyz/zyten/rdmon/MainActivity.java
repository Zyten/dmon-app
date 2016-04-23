package xyz.zyten.rdmon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    private TextView tempTextView, humidityTextView, dustTextView;
    String link, result;
    BufferedReader bufferedReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        tempTextView = (TextView) findViewById(R.id.tempTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        dustTextView = (TextView) findViewById(R.id.dustTextView);

        /*try {

            link = "https://thingspeak.com/channels/108012/field/1/last";
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                ThingDataTextView.setText(stringBuilder.toString());
            }
            catch(Exception ex){
                Log.e("ERROR", ex.getMessage(), ex);
            }
            finally{
                con.disconnect();
            }
        }
        catch(Exception e) {
             Log.e("ERROR", e.getMessage(), e);
        }*/
    }

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

        if (id == R.id.nav_profile) {
            Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
            profile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(profile);
        } else if (id == R.id.nav_history) {
            /*Intent history = new Intent(MainActivity.this, HistoryActivity.class);
            history.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(history);*/
        } else if (id == R.id.nav_air) {

        } else if (id == R.id.nav_share) {

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

    class FetchThingspeakTask extends AsyncTask<Void, Void, String> {


        protected void onPreExecute() {

            tempTextView.setText("");
            humidityTextView.setText("");
            dustTextView.setText("");
        }

        protected String doInBackground(Void... urls) {
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

        protected void onPostExecute(String response) {
            if(response == null) {
                Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                return;
            }


            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                double temp = channel.getDouble("field1");
                double humidity = channel.getDouble("field2");
                double dust = channel.getDouble("field3");

                tempTextView.setText(String.valueOf(temp));
                humidityTextView.setText(String.valueOf(humidity));
                dustTextView.setText(String.valueOf(dust));
                Log.e(TAG, String.valueOf(temp) + String.valueOf(humidity) + String.valueOf(dust));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

   /*private class getStreamAsyncTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            return sendData();
        }

        protected String sendData(){
            String device="D-MON@zytenn.zytenn";  //SUBSTITUTE YOUR OWN VALUE
            String apikey="f88617251146351ca66cad2bd0945f3149c65169eb1cf3a16e13604df7b9ffc5";  //SUBSTITUTE YOUR OWN VALUE
            String request = "https://api.carriots.com/streams";
            String decodedString="";
            String returnMsg="";
            String writeOut="";
            URL url;
            HttpURLConnection connection = null;
            try
            {
                url = new URL(request);
                connection = (HttpURLConnection) url.openConnection();
                //establish the parameters for the http post request
                connection.setDoOutput(true);
                connection.addRequestProperty("carriots.apikey", apikey);
                connection.addRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");
                //construct the json string to be sent
                writeOut = "{\"protocol\":\"v2\",\"checksum\":\"\"," +
                        "\"device\":\"" + device +
                        "\",\"at\":\"now\",\"data\":{\"test\":\"ok\"}}";
                //create an output stream writer and write the json string to it
                final OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(writeOut);
                osw.close();
                //create a buffered reader to interpret the incoming message from the carriots system
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((decodedString = in.readLine()) != null) {
                    returnMsg+=decodedString;
                }
                in.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                returnMsg=""+e;
            }
            return returnMsg;
        }

        protected void onPostExecute(String result){
            //show the message returned from Carriots to the user
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }*/
}


