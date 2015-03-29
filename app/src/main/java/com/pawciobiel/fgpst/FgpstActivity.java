package com.pawciobiel.fgpst;

import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.util.Patterns;

public class FgpstActivity extends ActionBarActivity implements LocationListener {

    private final static String CONNECTIVITY = "android.net.conn.CONNECTIVITY_CHANGE";

    private LocationManager locationManager;
    private ConnectivityManager connectivityManager;

    SharedPreferences preferences;
    private TextView text_pref_status;
    private TextView text_gps_status;
    private TextView text_network_status;
    private ToggleButton button_toggle;
    private TextView text_running_since;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(FgpstService.NOTIFICATION)) {
                updateServiceStatus();
            }
            if (action.equals(CONNECTIVITY)) {
                updateNetworkStatus();
            }
            if (action.equals(FgpstPreferenceActivity.PREFERENCES)) {
                updatePrefStatus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fgpst);

        ActionBar actionBar = getSupportActionBar();
        if (!actionBar.isShowing())
            actionBar.show();

        text_pref_status = (TextView) findViewById(R.id.text_pref_status);
        text_gps_status = (TextView) findViewById(R.id.text_gps_status);
        text_network_status = (TextView) findViewById(R.id.text_network_status);
        button_toggle = (ToggleButton) findViewById(R.id.button_toggle);
        text_running_since = (TextView) findViewById(R.id.text_running_since);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        updatePrefStatus();


        registerReceiver(receiver, new IntentFilter(FgpstService.NOTIFICATION));
        registerReceiver(receiver, new IntentFilter(FgpstActivity.CONNECTIVITY));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        int pref_gps_updates = Integer.parseInt(preferences.getString("pref_gps_updates", "30")); // seconds
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pref_gps_updates * 1000, 1, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            onProviderDisabled(LocationManager.GPS_PROVIDER);
        }

        updateNetworkStatus();

        updateServiceStatus();

        updatePrefStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fgpst, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_settings:
                i = new Intent(this, FgpstPreferenceActivity.class);
                startActivity(i);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void onToggleClicked(View view) {
        Intent intent = new Intent(this, FgpstService.class);
        if (((ToggleButton) view).isChecked()) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

	/* -------------- GPS stuff -------------- */

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        text_gps_status.setText(getString(R.string.text_gps_status_disabled));
        text_gps_status.setTextColor(Color.RED);
    }

    @Override
    public void onProviderEnabled(String provider) {
        text_gps_status.setText(getString(R.string.text_gps_status_enabled));
        text_gps_status.setTextColor(Color.BLACK);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /* ----------- utility methods -------------- */

    private void updatePrefStatus() {
        if (!PrefValidator.isPrefUrlValid(preferences)) {
            button_toggle.setEnabled(false);
            text_pref_status.setText(getString(R.string.text_legend_url_invalid));
            text_pref_status.setTextColor(Color.RED);
            return;
        }
        if (!PrefValidator.isPrefUserKeyValid(preferences)) {
            button_toggle.setEnabled(false);
            text_pref_status.setText(getString(R.string.pref_user_key_invalid));
            text_pref_status.setTextColor(Color.RED);
            return;
        }
        if (!PrefValidator.isPrefDeviceKeyValid(preferences)) {
            button_toggle.setEnabled(false);
            text_pref_status.setText(getString(R.string.pref_device_key_invalid));
            text_pref_status.setTextColor(Color.RED);
            return;
        }
        button_toggle.setEnabled(true);
    }

    private void updateServiceStatus() {

        if (FgpstService.isRunning) {
            Toast.makeText(this, getString(R.string.toast_service_running), Toast.LENGTH_SHORT).show();
            button_toggle.setChecked(true);
            text_running_since.setText(getString(R.string.text_running_since) + " "
                    + DateFormat.getDateTimeInstance().format(FgpstService.runningSince.getTime()));
        } else {
            Toast.makeText(this, getString(R.string.toast_service_stopped), Toast.LENGTH_SHORT).show();
            button_toggle.setChecked(false);
            if (preferences.contains("stoppedOn")) {
                long stoppedOn = preferences.getLong("stoppedOn", 0);
                if (stoppedOn > 0) {
                    text_running_since.setText(getString(R.string.text_stopped_on) + " "
                            + DateFormat.getDateTimeInstance().format(new Date(stoppedOn)));
                } else {
                    text_running_since.setText(getText(R.string.text_killed));
                }
            }
        }
    }

    private void updateNetworkStatus() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            text_network_status.setText(getString(R.string.text_network_status_enabled));
            text_network_status.setTextColor(Color.BLACK);
        } else {
            text_network_status.setText(getString(R.string.text_network_status_disabled));
            text_network_status.setTextColor(Color.RED);
        }
    }
}