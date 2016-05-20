package xyz.zyten.rdmon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zyten on 20/5/2016.
 */

public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("notify_precautions");

        checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.e("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                if(newValue.toString() == "true")
                    SettingsActivity.notifyMe = true;
                else
                    SettingsActivity.notifyMe = false;
                return true;
            }
        });
    }
}