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

//import com.pawciobiel.BuildConfig;


@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 15)
public class FgpstServiceTest {
    private LocationManager locationManager;
    public static final String TEST_GPS_PROVIDER = "FgpstServiceTest";

    @Before
    protected void setUp() throws Exception {
        FgpstActivity activity = Robolectric.buildActivity(FgpstActivity.class)
                .create().get();

        locationManager = (LocationManager)
                getContext().getSystemService(Context.LOCATION_SERVICE);
        //locationManager.addTestProvider(TEST_GPS_PROVIDER, false, false,
        // false,
        //        false, false, false, false, Criteria.POWER_HIGH,
        //        Criteria.ACCURACY_FINE);
        locationManager.setTestProviderEnabled(TEST_GPS_PROVIDER, true);

    }

    @Before
    protected void tearDown() throws Exception {
        super.tearDown();
        if (locationManager != null)
            locationManager.removeTestProvider(TEST_GPS_PROVIDER);
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void testOnLocationChanged() {

        Location testLocation = new Location(TEST_GPS_PROVIDER);
        testLocation.setLatitude(10.0);
        testLocation.setLongitude(20.0);
        locationManager.setTestProviderLocation(TEST_GPS_PROVIDER, testLocation);

        FgpstService srv = Robolectric.setupService(FgpstService.class);


        //Mockito.doReturn("This is the new value!").when(reqMock).execute();

        Intent startIntent = new Intent("test");

        FgpstService spy = Mockito.spy(srv);
        Mockito.doNothing().when(spy).executeRequest(Mockito.anyString(),
                Mockito.<JSONObject>any());

        spy.onLocationChanged(testLocation);
        assertEquals("current location", testLocation,
                spy.getCurrentLocation());

        //Mockito.doReturn("foo").when(spy).executeRequest(1234);
        String url = "";
        JSONObject json = new JSONObject();
        try {
            json.put("ass", "asdd");
        } catch (JSONException exc) {

        }
        Mockito.verify(spy, Mockito.times(1)).executeRequest(url, json);

    }
}