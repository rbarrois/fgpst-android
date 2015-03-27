package com.pawciobiel.fgpst;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.regex.Pattern;

public class FgpstPreferenceActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference pref;

        pref = findPreference("pref_gps_updates");
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int oldValue = Integer.parseInt(preferences.getString
                        ("pref_gps_updates", "0"));
                if (newValue == null
                        || newValue.toString().length() == 0
                        || !Pattern.matches("^\\d{1,5}$", newValue.toString())) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
                    return false;
                } else if (Integer.parseInt(newValue.toString()) < 30) { // user has been warned
                    Toast.makeText(getApplicationContext(), getString(R.string.pref_gps_updates_too_low), Toast.LENGTH_SHORT).show();
                    return false;
                } else if (FgpstService.isRunning
                        && Integer.parseInt(newValue.toString()) != oldValue) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_prefs_restart), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        pref = findPreference("pref_max_run_time");
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) { // hours
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int prefGpsUpdates = Integer.parseInt(preferences.getString
                        ("pref_gps_updates", "0")); // seconds
                int oldValue = Integer.parseInt(preferences.getString("pref_max_run_time", "0"));
                if (newValue == null
                        || newValue.toString().length() == 0
                        || !Pattern.matches("^\\d{1,5}$", newValue.toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
                    return false;
                } else if (Integer.parseInt(newValue.toString()) * 3600 < prefGpsUpdates) { // would not make sense...
                    Toast.makeText(getApplicationContext(), getString(R.string.pref_max_run_time_too_low), Toast.LENGTH_LONG).show();
                    return false;
                } else if (FgpstService.isRunning
                        && Integer.parseInt(newValue.toString()) != oldValue) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_prefs_restart), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}
