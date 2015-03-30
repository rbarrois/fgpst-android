package com.pawciobiel.fgpst;


import android.location.Criteria;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import org.mockito.Mockito;

public class FgpstServiceTest extends ServiceTestCase {
    private LocationManager locationManager;
    public static final String TEST_GPS_PROVIDER = "FgpstServiceTest";

    public FgpstServiceTest() {
        super(FgpstService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        locationManager = (LocationManager)
                getContext().getSystemService(Context.LOCATION_SERVICE);
        //locationManager.addTestProvider(TEST_GPS_PROVIDER, false, false,
        // false,
        //        false, false, false, false, Criteria.POWER_HIGH,
        //        Criteria.ACCURACY_FINE);
        locationManager.setTestProviderEnabled(TEST_GPS_PROVIDER, true);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (locationManager != null)
            locationManager.removeTestProvider(TEST_GPS_PROVIDER);
    }

    public void testOnLocationChanged() {

        Location testLocation = new Location(TEST_GPS_PROVIDER);
        testLocation.setLatitude(10.0);
        testLocation.setLongitude(20.0);
        locationManager.setTestProviderLocation(TEST_GPS_PROVIDER, testLocation);


        //Mockito.doReturn("This is the new value!").when(reqMock).execute();

        Intent startIntent = new Intent("test");
        startIntent.setClass(getContext(), FgpstService.class);
        IBinder bservice = bindService(startIntent);

        //assertNotNull("Bound to service ", bservice);
        FgpstService srv = (FgpstService) getService();
        srv.onLocationChanged(testLocation);
        assertEquals("current location", testLocation, srv.getCurrentLocation());

        FgpstService spy = Mockito.spy(srv);
        //Mockito.doReturn("foo").when(spy).callXXX(1234);

    }
}