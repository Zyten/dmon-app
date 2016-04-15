package xyz.zyten.rdmon;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

    //debug
    //private TextView idBirthdayTextView, genderTextView, emailTextView, personNameTextView;
    private TextView accountIdTextView;

    private EditText usernameEditText, birthdayEditText, hometownEditText, currResidenceEditText;

    private RadioGroup radioSexGroup;
    private RadioButton rbMale, rbFemale;

    private String genderVal, birthdayVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String accountId = "";
        String username = "";
        String email = "";
        String sGender = "";
        String birthday = "";

        SharedPreferences gettemppref = getSharedPreferences("temp", Context.MODE_PRIVATE);

        Boolean newUser =  gettemppref.getBoolean("GProfile", false);

        boolean logged_in = gettemppref.getBoolean("logged_in", true);

        if(newUser) {
            SharedPreferences getpref = getSharedPreferences("GProfile", Context.MODE_PRIVATE);

            accountId = getpref.getString("accountId", "");
            username = getpref.getString("username", "test");
            email = getpref.getString("email", "");
            sGender = getpref.getString("sGender", "");
            birthday = getpref.getString("birthday", "");
        }
       /*debug //Bundle extras = getIntent().getExtras();
        try {
            BirthdayTextView_string = getIntent().getStringExtra("birthday");
            accountIdTextView_string = getIntent().getStringExtra("accountId");
            String personNameTextView_string = getIntent().getStringExtra("personName");
            String genderTextView_string = getIntent().getStringExtra("gender");
            String emailTextView_string = getIntent().getStringExtra("email");
            String[] separated = BirthdayTextView_string.split("-");
            BirthdayTextView_string = separated[2]+"/"+separated[1]+"/"+separated[0];
        }
        catch (Exception ex){
            //no G+
            accountIdTextView_string="";
        }*/

        //initialize RadioGroup and RadioButton
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        radioSexGroup.clearCheck();

        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);

        //debug
        accountIdTextView = (TextView) findViewById(R.id.accountIdTextView);
       /* idBirthdayTextView = (TextView) findViewById(R.id.idBirthdayTextView);
        personNameTextView = (TextView) findViewById(R.id.personNameTextView);
        genderTextView = (TextView) findViewById(R.id.genderTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);*/

        //Initialise EditTexta
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        birthdayEditText = (EditText) findViewById(R.id.birthdayEditText);
        hometownEditText = (EditText) findViewById(R.id.homeTownEditText);
        currResidenceEditText = (EditText) findViewById(R.id.currResidenceEditText);

        //Fill in data from G+
        usernameEditText.setText(username);

        if(sGender.equals("Male"))
            radioSexGroup.check(R.id.rbMale);
        if(sGender.equals("Female"))
            radioSexGroup.check(R.id.rbFemale);

        birthdayEditText.setText(birthday);
        birthdayVal =  birthday;

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
            Toast.makeText(this, "Saving..", Toast.LENGTH_SHORT).show();

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
}
