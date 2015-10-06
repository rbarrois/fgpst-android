package com.pawciobiel.fgpst;

import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import android.util.Log;
import java.io.OutputStreamWriter;


public class FgpstServiceRequest extends AsyncTask<String, Void, Void> {
    private final static String MY_TAG = "FgpstServiceRequest";

    protected Void doInBackground(String... requestData) {
        try {
            URL url = new URL(requestData[0]);
            String data = requestData[1];
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000 /* milliseconds */);
            conn.setConnectTimeout(1500 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            conn.connect();
            int status = conn.getResponseCode();
            String msg = conn.getResponseMessage();
            Log.d(MY_TAG, "HTTP request done status=" + status + " message="
                    + msg);
            // that's ok, nothing more to do here
        } catch (Exception e) {
            // we cannot do anything about that : network may be temporarily down
            Log.d(MY_TAG, "HTTP request failed: " + e.toString());
        }
        return null;
    }
}
