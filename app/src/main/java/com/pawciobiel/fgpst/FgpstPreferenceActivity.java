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
                sendBroadcastPrefChanged();
                return true;
            }
        });

        pref = findPreference("vehicle_id");
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String oldValue = preferences.getString("vehicle_id", "");
                if (!PrefValidator.isVehicleIDValid((String) newValue)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.pref_vehicle_id_invalid),
                            Toast.LENGTH_LONG).show();
                    return false;
                } else if (FgpstService.isRunning
                        && (newValue.toString() != oldValue)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_prefs_restart),
                            Toast.LENGTH_LONG).show();
                }
                sendBroadcastPrefChanged();
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
                } else if (Integer.parseInt(newValue.toString()) < 2) { // user has been warned
                    Toast.makeText(getApplicationContext(), getString(R.string.pref_gps_updates_too_low), Toast.LENGTH_SHORT).show();
                    return false;
                } else if (FgpstService.isRunning
                        && Integer.parseInt(newValue.toString()) != oldValue) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_prefs_restart), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    private void sendBroadcastPrefChanged() {
        Intent i = new Intent(FgpstPreferenceActivity.PREFERENCES);
        sendBroadcast(i);
    }
}
