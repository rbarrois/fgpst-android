package com.pawciobiel.fgpst;



import android.content.Intent;
import android.content.SharedPreferences;
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
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.*;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPreferenceManager;

//import com.pawciobiel.BuildConfig;


@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21,
        constants=BuildConfig.class)
public class FgpstServiceTest {

    @Mock
    public LocationManager mLocationManager;

    @Mock
    public FgpstServiceRequest mFgpstServiceRequest;

    @InjectMocks
    public FgpstService srv = Robolectric.setupService(FgpstService.class);

    public static final String TEST_GPS_PROVIDER = "FgpstServiceTest";
    public FgpstActivity activity;

    @Before
    public void initMocks() {
        System.setProperty("dexmaker.dexcache", "/tmp");
        //System.setProperty("dexmaker.dexcache", getInstrumentation()
        //        .getTargetContext().getCacheDir().getPath());
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setUp() throws Exception {


        SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());
        sharedPreferences.edit().putString("vehicle_id", "12345").commit();
        /*(mLocationManager = (LocationManager)
                getContext().getSystemService(Context.LOCATION_SERVICE);
        //mLocationManager.addTestProvider(TEST_GPS_PROVIDER, false, false,
        // false,
        //        false, false, false, false, Criteria.POWER_HIGH,
        //        Criteria.ACCURACY_FINE);
        mLocationManager.setTestProviderEnabled(TEST_GPS_PROVIDER, true);
        */
    }


    @Test
    public void testOnLocationChanged() {

        Location testLocation = new Location(TEST_GPS_PROVIDER);
        testLocation.setLatitude(new Double("10.123456789"));
        testLocation.setLongitude(new Double("20.987654321"));
        mLocationManager.setTestProviderLocation(TEST_GPS_PROVIDER, testLocation);

        //Mockito.doReturn("This is the new value!").when(reqMock).execute();

        FgpstService spy = Mockito.spy(srv);
        Mockito.doNothing().when(spy).executeRequest(Mockito.anyString(),
                Mockito.<JSONObject>any());

        spy.onLocationChanged(testLocation);
        assertEquals("current location", testLocation,
                spy.getCurrentLocation());

        //Mockito.doReturn("foo").when(spy).executeRequest(1234);
        String url = "http://ws.bluebus.fr/gps-tracker/0/";
        JSONObject tjson = new JSONObject();
        try {
            tjson.put("aaa", "bbb");
        } catch (JSONException exc) {

        }
        // {"lat":10.123457,"lon":20.987654,"alt":0,"speed":0,"bearing":0,"timestamp":"2015-04-04T12:36:26.484Z","vehicle_id":""}
        Mockito.verify(spy, Mockito.times(1)).executeRequest(Mockito.eq(url),
                Mockito.<JSONObject>any());
                //Mockito.<JSONObject>eq(tjson));
        // !!! altitude
        // TODO: test buildPositionMsgFromCurrLocation if json/string is correct
    }
}
