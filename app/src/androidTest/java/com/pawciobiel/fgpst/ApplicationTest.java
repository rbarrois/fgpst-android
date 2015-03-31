package com.pawciobiel.fgpst;



import android.location.Criteria;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 21,
        constants=BuildConfig.class)
public class ApplicationTest {

    public FgpstActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(FgpstActivity.class)
                .create().get();
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(activity);
    }

}