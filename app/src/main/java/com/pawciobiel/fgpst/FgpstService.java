package com.pawciobiel.fgpst;

import java.util.Calendar;
import java.util.TimeZone;
import java.text.DecimalFormat;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

public class FgpstService extends IntentService implements LocationListener {

    public static final String NOTIFICATION = "com.pawciobiel.fgpst";

    public static boolean isRunning;
    public static Calendar runningSince;
    public Calendar stoppedOn;

    private final static String MY_TAG = "FgpstService";

    private SharedPreferences preferences;
    private String urlText;
    private String vehicle_id;
    private LocationManager locationManager;
    private int pref_gps_updates;
    private long latestUpdate;
    private Location currentLocation;

    public Location getCurrentLocation(){
        return currentLocation;
    }
    public long getLatestUpdate(){
        return latestUpdate;
    }

    public FgpstService() {
        super("FgpstService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MY_TAG, "in onCreate, init GPS stuff");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            onProviderDisabled(LocationManager.GPS_PROVIDER);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("stoppedOn", 0);
        editor.commit();
        pref_gps_updates = Integer.parseInt(preferences.getString("pref_gps_updates", "5")); // seconds
        vehicle_id = preferences.getString("vehicle_id", "");


        String defaultUrl = this.getResources().getString(R.string
                .pref_url_default);
        urlText = preferences.getString("URL", defaultUrl);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                pref_gps_updates * 1000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                pref_gps_updates * 1000, 0, this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(MY_TAG, "in onHandleIntent, run for maximum time set in preferences");
        /*
        JSONObject json = new JSONObject();
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("tracker", "start");
            json.put("command", cmd);

        } catch (JSONException exc){
            Log.d(MY_TAG, "error building json" + exc.getMessage());
        }
        new FgpstServiceRequest().execute(urlText, json.toString());
        */
        isRunning = true;
        runningSince = Calendar.getInstance();
        Intent i = new Intent(NOTIFICATION);
        sendBroadcast(i);

        Notification notification = new Notification(R.drawable.ic_notif,
                getText(R.string.toast_service_running), System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, FgpstActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.toast_service_running), pendingIntent);
        startForeground(R.id.logo, notification);

        while (true) {
            try {
                Thread.sleep(60 * 1000); // note: when device is sleeping, it may last up to 5 minutes or more
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onDestroy() {
        // (user clicked the stop button, or max run time has been reached)
        Log.d(MY_TAG, "in onDestroy, stop listening to the GPS");
        // FIXME: vehicle_id, json
        new FgpstServiceRequest().execute(urlText + "tracker=stop");

        locationManager.removeUpdates(this);

        isRunning = false;
        stoppedOn = Calendar.getInstance();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("stoppedOn", stoppedOn.getTimeInMillis());
        editor.commit();

        Intent intent = new Intent(NOTIFICATION);
        sendBroadcast(intent);
    }

    public static double getDoubleValue(String value, int digit){
        if(value==null){
            value="0";
        }
        double i=0;
        try {
            DecimalFormat digitformat = new DecimalFormat("#.######");
            digitformat.setMaximumFractionDigits(digit);
            return Double.valueOf(digitformat.format(Double.parseDouble(value)));

        } catch (NumberFormatException numberFormatExp) {
            return i;
        }
    }

	/* -------------- GPS stuff -------------- */

    public JSONObject buildPositionMsgFromCurrLocation(){
        double lat = currentLocation.getLatitude();
        double lon = currentLocation.getLongitude();
        double altitude = currentLocation.getAltitude();
        float speed = currentLocation.getSpeed();
        float bearing = currentLocation.getBearing();

        // FIXME: I should use location.getTime()
        String timestampStr = String.format("%tFT%<tT.%<tLZ",
                Calendar.getInstance(TimeZone.getTimeZone("Z")));
        JSONObject json = new JSONObject();
        try {
            json.put("lat", getDoubleValue(String.valueOf(lat), 6));
            json.put("lon", getDoubleValue(String.valueOf(lon), 6));
            json.put("altitude", altitude);
            json.put("speed", speed);
            json.put("bearing", bearing);
            json.put("timestamp", timestampStr);
            json.put("vehicle_id", vehicle_id);
        } catch (org.json.JSONException exc){
            Log.d(MY_TAG, "error generating json: " + exc.toString());
        }
        return json;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(MY_TAG, "in onLocationChanged, latestUpdate == " + latestUpdate);

        if (!PrefValidator.isVehicleIDValid(vehicle_id)){
            Intent i = new Intent(FgpstPreferenceActivity.PREFERENCES);
            sendBroadcast(i);
            return;
        }

        if ((System.currentTimeMillis() - latestUpdate) < pref_gps_updates * 1000) {
            return;
        }

        latestUpdate = System.currentTimeMillis();
        currentLocation = location;


        // TODO: update app with last position latestUpdate
        //latTextView.setText(String.format("%.6f", lat));
        //lonTextView.setText(String.format("%.6f", lon));
        //latestUpdateTimeTextView.setText(timestampStr);


        JSONObject json = buildPositionMsgFromCurrLocation();
        executeRequest(urlText, json);


    }

    public void executeRequest(String urlText, JSONObject json){
        new FgpstServiceRequest().execute(urlText, json.toString());
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
