package com.pawciobiel.fgpst;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.widget.Toast;

import java.util.regex.Pattern;

public class FgpstPreferenceActivity extends PreferenceActivity {

    public static final String PREFERENCES = "com.pawciobiel.fgpst.preferences";

    @Override
    protected void onStop() {
        super.onStop();
        Intent i = new Intent(PREFERENCES);
        sendBroadcast(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference pref;

        pref = findPreference("URL");
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String oldValue = preferences.getString("URL", "");
                if (!PrefValidator.isUrlValid((String)newValue)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.text_legend_url_invalid), Toast.LENGTH_LONG).show();
                    return false;
                } else if (FgpstService.isRunning
                        && (newValue.toString() != oldValue)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_prefs_restart),
                            Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        pref = findPreference("user_key");
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                String oldValue = preferences.getString("user_key", "");
                if (!PrefValidator.isUserKeyValid((String)newValue)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.pref_user_key_invalid),
                            Toast.LENGTH_LONG).show();
                    return false;
                } else if (FgpstService.isRunning
                        && (newValue.toString() != oldValue)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_prefs_restart),
                            Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        pref = findPreference("device_key");
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String oldValue = preferences.getString("device_key", "");
                if (!PrefValidator.isDeviceKeyValid((String) newValue)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.pref_device_key_invalid),
                            Toast.LENGTH_LONG).show();
                    return false;
                } else if (FgpstService.isRunning
                        && (newValue.toString() != oldValue)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_prefs_restart),
                            Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

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
