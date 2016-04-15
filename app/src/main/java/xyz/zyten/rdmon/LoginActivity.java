package xyz.zyten.rdmon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    //private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final String TAG = "LoginActivity";
    //private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;
    private String idBirthdayTextView, accountIdTextView, personNameTextView, genderTextView, emailTextView;

    private GoogleApiClient mGoogleApiClient;
    private TextView mIdTokenTextView;
    private String accountId= "", username= "", email = "", sGender ="", birthday = "";
    private Integer gender = -1;

    //Give your SharedPreferences file a name and save it to a static variable
    public static final String TEMP = "temp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mIdTokenTextView = (TextView) findViewById(R.id.status);

        // Button click listeners
        Button btnlogin = (Button) findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(this);

        // [START configure_signin]
        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .build();

        // todo: Handle err if API not available

       /* btnlogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent login = new Intent(LoginActivity.this, MainActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                LoginActivity.this.startActivity(login);
               // LoginActivity.this.finish();
            }
        });*/
    }

    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();
                //String accountID = GoogleAuthUtil.getAccountId(accountName);
                //String accountID = act.getAccountId(accountName);

                if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

                    Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    accountId = currentPerson.getId();
                    int userStatus = 0;
                    isRegistered reg = new isRegistered(this);
                    if(reg.sendQuery(accountId) == "SUCCESS")
                        userStatus = reg.getRegisteredStatus();

                    if(userStatus==1) {
                        Intent gotoMain = new Intent(this, MainActivity.class);
                        LoginActivity.this.startActivity(gotoMain);
                    }
                    if (currentPerson.hasDisplayName())
                        username = currentPerson.getDisplayName();
                    if (currentPerson.hasGender()) {
                        gender = currentPerson.getGender();
                        switch (gender) {
                            case 0:
                                sGender = "Male";
                                break;
                            case 1:
                                sGender = "Female";
                                break;
                            case 2:
                                sGender = "Other";
                                break;
                            default:
                                sGender = "N/A";
                        }
                    }

                    email = acct.getEmail();

                    if (currentPerson.hasBirthday()) {
                        birthday = currentPerson.getBirthday();
                    }
                else{
                    Log.d(TAG, "Plus.PeopleApi.getCurrentPerson() is null");
                }

                /*//debug
                idBirthdayTextView=birthday;
                accountIdTextView=accountId;
                personNameTextView=personName;
                genderTextView=sGender;
                emailTextView=email;*/

                // Show signed-in UI.
                Log.d(TAG, "idToken:" + idToken);
                mIdTokenTextView.setText(getString(R.string.id_token_fmt, idToken));
                updateUI(true);

                // TODO(user): send token to server and validate server-side
            } else {
                // Show signed-out UI.
                updateUI(false);
            }
            // [END get_id_token]
        }
    }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            //debug
            ((TextView) findViewById(R.id.status)).setText(R.string.signed_in);

            SharedPreferences getpref = getSharedPreferences("myProfile", Context.MODE_PRIVATE);

            boolean profile_complete = getpref.getBoolean("profile_complete", false);

            if(profile_complete)
            {
                Intent login = new Intent(this, MainActivity.class);
                LoginActivity.this.startActivity(login);
            }

            SharedPreferences setpref = getSharedPreferences("GProfile", Context.MODE_PRIVATE);

            // We need an editor object to make changes
            SharedPreferences.Editor profileEditor = setpref.edit();

            // Set/Store data
            profileEditor.putString("accountId", accountId);
            profileEditor.putString("username", username);
            profileEditor.putString("email", email);
            profileEditor.putString("sGender", sGender);
            profileEditor.putString("birthday", birthday);
            Toast.makeText(this, sGender, Toast.LENGTH_SHORT).show();
            // Commit the changes
            profileEditor.commit();


            //temp to store which pref is available
            //SharedPreferences settemppref = getSharedPreferences(LoginActivity.TEMP, Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            // We need an editor object to make changes
            //SharedPreferences.Editor tempEditor = settemppref.edit();
            SharedPreferences.Editor tempEditor = sharedPreferences.edit();

            // Set/Store data
            tempEditor.putBoolean("GProfile", true);
            tempEditor.putBoolean("logged_in", true);

            // Commit the changes
            tempEditor.commit();

            Intent login = new Intent(this, ProfileActivity.class);
            /*//Bundle extras = new Bundle();
            //extras.putString("viewTextName",viewTextName);
            //extras.putString("viewTextEmail",viewTextEmail);
            login.putExtra("birthday", idBirthdayTextView);
            login.putExtra("accountId", accountIdTextView);
            login.putExtra("personName", personNameTextView);
            login.putExtra("gender", genderTextView);
            login.putExtra("email", emailTextView);
            //login.putExtras(extras);*/
            //login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            LoginActivity.this.startActivity(login);
        } else {
            mIdTokenTextView.setText(getString(R.string.id_token_fmt, "null"));
        }
    }

    @Override
    public void onClick(View v) {
        //boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        if(isNetworkAvailable()){
            Log.i ("Tag", "Network Connected");
            //Toast.makeText(this.getApplicationContext(), "Connected to Network", Toast.LENGTH_LONG).show();
            if (isInternetAvailable()) {
                Log.i ("Tag", "Internet Connected");
                switch (v.getId()) {
                    case R.id.btnlogin:
                        getIdToken();
                        break;
                }
            }

            else {
                Log.i ("Tag", "No Internet Connection");
                Toast.makeText(this.getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        /*else {
            Log.i ("Tag", "Network Not Connected");
            Toast.makeText(this.getApplicationContext(), "Not Connected to Network", Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.quit)
                    .setMessage(R.string.really_quit)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Stop the activity
                            LoginActivity.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
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
            return (Runtime.getRuntime().exec ("ping -c 1 google.com").waitFor() == 0);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}
