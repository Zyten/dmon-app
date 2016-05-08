package xyz.zyten.rdmon;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity{

    private TextView accountIdTextView;
    private EditText usernameEditText, birthdayEditText, hometownEditText, currResidenceEditText;
    private RadioGroup radioSexGroup;
    private RadioButton rbMale, rbFemale;
    private String genderVal, birthdayVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Personal Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String googleID = "";
        String username = "";
        String sGender = "";
        String birthday = "";
        String hometown = "";
        String currResidence = "";

        SharedPreferences gettemppref = getSharedPreferences("temp", Context.MODE_PRIVATE);
        Boolean newUser =  gettemppref.getBoolean("newUser", true);

        if(newUser) {
            SharedPreferences getpref = getSharedPreferences("GProfile", Context.MODE_PRIVATE);

            googleID = getpref.getString("googleID", "");
            username = getpref.getString("username", "");
            sGender = getpref.getString("sGender", "");
            birthday = getpref.getString("birthday", "");
        }

        else
        {
            SharedPreferences getpref = getSharedPreferences("myProfile", Context.MODE_PRIVATE);

            googleID = getpref.getString("googleID", "");
            username = getpref.getString("username", "");
            sGender = getpref.getString("gender", "");
            birthday = getpref.getString("birthday", "");
            hometown = getpref.getString("hometown", "");
            currResidence = getpref.getString("currResidence", "");
        }

        //initialize RadioGroup and RadioButton
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        radioSexGroup.clearCheck();
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);

        //debug
        accountIdTextView = (TextView) findViewById(R.id.accountIdTextView);

        //Initialise EditTexta
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        birthdayEditText = (EditText) findViewById(R.id.birthdayEditText);
        hometownEditText = (EditText) findViewById(R.id.homeTownEditText);
        currResidenceEditText = (EditText) findViewById(R.id.currResidenceEditText);

        //Fill in data from G+/SharedPref

        accountIdTextView.setText(googleID);
        usernameEditText.setText(username);

        if(sGender.equals("Male")) {
            radioSexGroup.check(R.id.rbMale);
            genderVal = rbMale.getText().toString();
        }
        if(sGender.equals("Female")){
            radioSexGroup.check(R.id.rbFemale);
            genderVal = rbFemale.getText().toString();
        }

        birthdayEditText.setText(birthday);
        birthdayVal =  birthday;

        hometownEditText.setText(hometown);
        currResidenceEditText.setText(currResidence);

        // TextWatcher would let us check validation error on the fly
        usernameEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(usernameEditText);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        hometownEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(hometownEditText);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        currResidenceEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(currResidenceEditText);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        //Attach Listener for Birthday datetime picker
        birthdayEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        /*Attach CheckedChangeListener to radio group */
        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if(null!=rb && checkedId > -1){
                    //Toast.makeText(ProfileActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    genderVal = rb.getText().toString();
                }

            }
        });

        EditText edit_txt = (EditText) findViewById(R.id.currResidenceEditText);

        edit_txt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(currResidenceEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

    }

    //Datetime picker
    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    //Submit to remote db
    public void save(View v) {
        if ( checkValidation () ) {
            String username = usernameEditText.getText().toString();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
            String birthday = birthdayVal;
            String hometown = hometownEditText.getText().toString();
            String currResidence = currResidenceEditText.getText().toString();
            String googleID = accountIdTextView.getText().toString();
            String gender = genderVal;
            //Toast.makeText(this, username+" "+birthday+" "+hometown+" "+currResidence+" "+googleID+" "+gender, Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "Saving..", Toast.LENGTH_SHORT).show();


            new UpdateProfileActivity(this).execute(username, gender, birthday, hometown, currResidence, googleID);

            Intent main = new Intent(ProfileActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            ProfileActivity.this.startActivity(main);
        }
        else
            Toast.makeText(ProfileActivity.this, "Make sure all fields are filled correctly.", Toast.LENGTH_LONG).show();
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat fmtOut = new SimpleDateFormat(myFormat);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");

        birthdayVal = fmt.format((myCalendar.getTime()));

        birthdayEditText.setText(fmtOut.format(myCalendar.getTime()));
    }

    public void onClear(View v) {
        /* Clears all selected radio buttons to default */
        radioSexGroup.clearCheck();
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.hasText(usernameEditText)) ret = false;
        if (!Validation.hasText(hometownEditText)) ret = false;
        if (!Validation.hasText(currResidenceEditText)) ret = false;
        if (!Validation.isChecked(radioSexGroup)) ret = false;

        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            ProfileActivity.this.finish();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
