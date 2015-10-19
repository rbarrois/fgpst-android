package com.pawciobiel.fgpst;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.widget.Toast;

import java.util.regex.Pattern;

public class FgpstPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

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
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new FgpstPreferenceFragment())
            .commit();
    }

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {

        if (key.equals("URL")) {
            String newValue = preferences.getString("URL", "");
            if (!PrefValidator.isUrlValid((String)newValue)) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.text_legend_url_invalid), Toast.LENGTH_LONG).show();
            } else if (FgpstService.isRunning) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_prefs_restart),
                        Toast.LENGTH_LONG).show();
            }
            sendBroadcastPrefChanged();
        } else if (key.equals("vehicle_id")) {
            String newValue = preferences.getString("vehicle_id", "");
            if (!PrefValidator.isVehicleIDValid((String) newValue)) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.pref_vehicle_id_invalid),
                        Toast.LENGTH_LONG).show();
            } else if (FgpstService.isRunning) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_prefs_restart),
                        Toast.LENGTH_LONG).show();
            }
            sendBroadcastPrefChanged();
        } else if (key.equals("pref_gps_updates")) {
            int newValue = Integer.parseInt(preferences.getString
                    ("pref_gps_updates", "0"));
            if (newValue < 2) { // user has been warned
                Toast.makeText(getApplicationContext(), getString(R.string.pref_gps_updates_too_low), Toast.LENGTH_SHORT).show();
            } else if (FgpstService.isRunning) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_prefs_restart), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendBroadcastPrefChanged() {
        Intent i = new Intent(FgpstPreferenceActivity.PREFERENCES);
        sendBroadcast(i);
    }
}
