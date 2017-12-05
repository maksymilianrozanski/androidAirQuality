package com.example.android.airquality.main;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.example.android.airquality.utility.QueryStationSensors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class SingleStationActivityInstrumentedTest extends InstrumentationTestCase {

    private MockWebServer sensorsListServer;
    private MockWebServer sensorsDataServer1;


    @Rule
    public ActivityTestRule<SingleStationActivity> activityRule
            = new ActivityTestRule<>(SingleStationActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        super.setUp();
//station id 10139
        sensorsListServer = new MockWebServer();
        sensorsListServer.start();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        QueryStationSensors.BEGINNING_OF_URL_SENSORS_LIST = sensorsListServer.url("/").toString();     //10121

        sensorsDataServer1 = new MockWebServer();
        sensorsDataServer1.start();
        QueryStationSensors.BEGINNING_OF_URL_SENSOR_DATA = sensorsDataServer1.url("/").toString(); //16377
    }

    @Test
    public void checkDisplayingDataTest() throws Exception {
        String sensorsFileName = "sensorsPiastow10139.json";
        String sensorValuesFileName = "sensor16784values.json";

        sensorsListServer.enqueue(new MockResponse().setResponseCode(200)
        .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensorsFileName)));

        sensorsDataServer1.enqueue(new MockResponse().setResponseCode(200)
        .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensorValuesFileName)));

        Intent intent = new Intent();
        intent.putExtra("StationId", 10139);
        intent.putExtra("StationName", "Kraków, os. Piastów");
        activityRule.launchActivity(intent);

        Thread.sleep(2000);
        //TODO: finish test
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        sensorsListServer.shutdown();
        sensorsDataServer1.shutdown();
    }


}
