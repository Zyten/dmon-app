package xyz.zyten.rdmon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HealthProfileActivity extends AppCompatActivity{

    private static final String TAG = "HistoryActivity";
    private static ListView lv;
    private HCondition[] hconditions ;
    private ArrayAdapter<HCondition> listAdapter ;
    private static ArrayList<HCondition> hconditionList;
    public static Integer isSensitive = 0, doesExercise = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hprofile);

        initLayout();
        initViews();

        SharedPreferences gettemppref = getSharedPreferences("temp", Context.MODE_PRIVATE);
        Boolean newUser =  gettemppref.getBoolean("newUser", true);
        if(!newUser)
        {
            SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);

            if(settings.getInt("isSensitive", -1) == 0 || settings.getInt("isSensitive", -1) == 1)
                isSensitive = settings.getInt("isSensitive", -1);

            if(settings.getInt("doesExercise", -1) == 0 || settings.getInt("doesExercise", -1) == 1)
                doesExercise = settings.getInt("doesExercise", -1);
        }

        Boolean stat0 = false, stat1 =false;

        if(isSensitive == 1){
            stat0 = true;
        }

        if(doesExercise == 1){
            stat1 = true;
        }

        for(HCondition h : hconditionList){
            if(h.getID() == 0)
                h.setChecked(stat0);
            if(h.getID() == 1)
                h.setChecked(stat1);
        }
    }

    private void initLayout(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Health Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initViews(){
        lv = (ListView)findViewById(R.id.category_list);

        // When item is tapped, toggle checked properties of CheckBox and HCondition.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View item,
                                     int position, long id) {
                HCondition hcondition = listAdapter.getItem( position );
                hcondition.toggleChecked();
                HConditionViewHolder viewHolder = (HConditionViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked( hcondition.isChecked() );

                if(hcondition.getID() == 0){
                    Log.e(TAG, "Is Sensitive");
                    if(hcondition.isChecked() == true){
                        Log.e(TAG, "Is Sensitive is selected");
                        isSensitive = 1;
                    }
                    else
                        isSensitive = 0;
                }
                else if(hcondition.getID() == 1){
                    Log.e(TAG, "Does Exercise");
                    if(hcondition.isChecked() == true){
                        Log.e(TAG, "Does Exercise is selected");
                        doesExercise = 1;
                    }
                    else
                        doesExercise = 0;
                }
                else{
                    // hcondition.setChecked( cb.isChecked());
                    //hcondition.toggleChecked();
                    Log.e(TAG, "Not None");
                }
            }
        });

        // Create and populate hconditions.
        //hconditions = (HCondition[]) getLastCustomNonConfigurationInstance() ;
        hconditions = new HCondition[] {
                new HCondition("I have health sensitivities related to\n heart or respiratory organs", 0),
                new HCondition("Exercising outside is a significant \npart of my life", 1),
                // new HCondition("None of the above", 2)
        };
        hconditionList = new ArrayList<HCondition>();
        hconditionList.addAll( Arrays.asList(hconditions) );

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new HConditionArrayAdapter(this,  hconditionList);
        lv.setAdapter( listAdapter);
    }

    /** Holds hcondition data. */
    private static class HCondition {
        private String name = "" ;
        private boolean checked = false ;
        private Integer id;
        public HCondition() {}
        public HCondition( String name, Integer id ) {
            this.name = name;
            this.id = id;
        }
        public HCondition( String name, boolean checked ) {
            this.name = name ;
            this.checked = checked ;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getID() {
            return id;
        }
        public boolean isChecked() {
            return checked;
        }
        public void setChecked(boolean checked) {
            this.checked = checked;
        }
        public String toString() {
            return name ;
        }
        public void toggleChecked() {
            checked = !checked ;
        }
    }

    /** Holds child views for one row. */
    private static class HConditionViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public HConditionViewHolder() {}
        public HConditionViewHolder( TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
            this.textView.setTextColor(Color.WHITE);
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    /** Custom adapter for displaying an array of HCondition objects. */
    private static class HConditionArrayAdapter extends ArrayAdapter<HCondition> {

        private LayoutInflater inflater;

        public HConditionArrayAdapter( Context context, List<HCondition> hconditionList ) {
            super( context, R.layout.simplerow, R.id.rowTextView, hconditionList );
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // HCondition to display
            HCondition hcondition = this.getItem( position );

            // The child views in each row.
            CheckBox checkBox ;
            TextView textView ;

            // Create a new row view
            if ( convertView == null ) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById( R.id.rowTextView );
                checkBox = (CheckBox) convertView.findViewById( R.id.CheckBox01 );

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag( new HConditionViewHolder(textView,checkBox) );

                // If CheckBox is toggled, update the hcondition it is tagged with.
                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        HCondition hcondition = (HCondition) cb.getTag();
                        hcondition.setChecked( cb.isChecked());
                        if(hcondition.getID() == 0){
                            Log.e(TAG, "Is Sensitive");
                            if(hcondition.isChecked() == true){
                                Log.e(TAG, "Is Sensitive is selected");
                                isSensitive = 1;
                            }
                            else
                                isSensitive = 0;
                        }
                        else if(hcondition.getID() == 1){
                            Log.e(TAG, "Does Exercise");
                            if(hcondition.isChecked() == true){
                                Log.e(TAG, "Does Exercise is selected");
                                doesExercise = 1;
                            }
                            else
                                doesExercise = 0;
                        }
                        else{
                            // hcondition.setChecked( cb.isChecked());
                            //hcondition.toggleChecked();
                            Log.e(TAG, "Not None");
                        }
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                HConditionViewHolder viewHolder = (HConditionViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox() ;
                textView = viewHolder.getTextView() ;
            }

            // Tag the CheckBox with the HCondition it is displaying, so that we can
            // access the hcondition in onClick() when the CheckBox is toggled.
            checkBox.setTag( hcondition);

            // Display hcondition data
            checkBox.setChecked( hcondition.isChecked() );
            textView.setText( hcondition.getName() );

            return convertView;
        }

    }

    public Object onRetainCustomNonConfigurationInstance () {
        return hconditions ;
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

    //Submit to remote db
    public void save(View v) {

            new UpdateHealthProfileTask(this).execute(isSensitive, doesExercise);

            Intent main = new Intent(HealthProfileActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            HealthProfileActivity.this.startActivity(main);
    }
}