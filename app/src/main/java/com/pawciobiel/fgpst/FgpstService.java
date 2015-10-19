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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

public class FgpstService extends IntentService implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    public static final String NOTIFICATION = "com.pawciobiel.fgpst";

    public static boolean isRunning;
    public static Calendar runningSince;
    public Calendar stoppedOn;

    private final static String MY_TAG = "FgpstService";

    private SharedPreferences preferences;
    private String urlText;
    private String vehicle_id;
    private int pref_gps_updates;
    private long latestUpdate;
    private Location currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean apiClientConnected;

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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("stoppedOn", 0);
        editor.commit();

        // Read preferences
        pref_gps_updates = Integer.parseInt(preferences.getString("pref_gps_updates", "5")); // seconds
        vehicle_id = preferences.getString("vehicle_id", "");
        String defaultUrl = this.getResources().getString(R.string
                .pref_url_default);
        urlText = preferences.getString("URL", defaultUrl);

        setupGoogleApiClient();
    }

    protected synchronized void setupGoogleApiClient() {
        apiClientConnected = false;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(MY_TAG, "GoogleApiClient now connected");
        apiClientConnected = true;
        // We want location updates as we start.
        startLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Failed to connect, so what?
        Log.d(MY_TAG, "GoogleApiClient failed to connect");
        apiClientConnected = false;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Google APIs crashed. Too bad.
        Log.d(MY_TAG, "GoogleApiClient crashed");
        apiClientConnected = false;
    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(pref_gps_updates);
        mLocationRequest.setFastestInterval(pref_gps_updates);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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

        if (apiClientConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

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
        float accuracy = currentLocation.getAccuracy();
        float speed = currentLocation.getSpeed();
        float bearing = currentLocation.getBearing();
        long timestamp = currentLocation.getTime();

        JSONObject json = new JSONObject();
        try {
            json.put("lat", getDoubleValue(String.valueOf(lat), 6));
            json.put("lon", getDoubleValue(String.valueOf(lon), 6));
            json.put("accuracy", accuracy);
            json.put("altitude", altitude);
            json.put("speed", speed);
            json.put("bearing", bearing);
            json.put("timestamp", timestamp);
            json.put("vehicle_id", vehicle_id);
        } catch (org.json.JSONException exc){
            Log.d(MY_TAG, "error generating json: " + exc.toString());
        }
        return json;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(MY_TAG, "in onLocationChanged, latestUpdate == " + latestUpdate);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        vehicle_id = preferences.getString("vehicle_id", "");

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
}
