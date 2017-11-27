package com.example.android.airquality.main;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.StationList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentsInstrumentedTest extends InstrumentationTestCase {

    private MockWebServer server;
    private static final String EXPECTED_STATION_INDEX_0_NAME = "mocked station name 1";
    private static final int EXPECTED_STATION_INDEX_0_ID = 236;
    private static final String EXPECTED_STATION_INDEX_5_NAME = "Jelenia Góra - Ogińskiego";
    private static final int EXPECTED_STATION_INDEX_5_ID = 9153;
    private static final String EXPECTED_STATION_INDEX_25_NAME = "Bolesławiec";
    private static final int EXPECTED_STATION_INDEX_25_ID = 10035;

    @Rule
    public IntentsTestRule<MainActivity> activityRule
            = new IntentsTestRule<>(MainActivity.class, true, false);

    @Before
    public void clearSharedPreferences() {
        File root = InstrumentationRegistry.getTargetContext().getFilesDir().getParentFile();
        String[] sharedPreferencesFileNames = new File(root, "shared_prefs").list();
        for (String fileName : sharedPreferencesFileNames) {
            InstrumentationRegistry.getTargetContext().getSharedPreferences(fileName.replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().commit();
        }
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        server = new MockWebServer();
        server.start();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        StationList.STATIONS_BASE_URL = server.url("/").toString();
    }

    @Test
    public void checkSendingIntent() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        stubAllExternalIntents();

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        intended(allOf(hasExtra("StationId", EXPECTED_STATION_INDEX_0_ID), hasExtra("StationName", EXPECTED_STATION_INDEX_0_NAME),
                toPackage("com.example.android.airquality")));
    }

    @Test
    public void checkSendingIntent2() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        stubAllExternalIntents();

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(5).perform(click());

        intended(allOf(hasExtra("StationId", EXPECTED_STATION_INDEX_5_ID), hasExtra("StationName", EXPECTED_STATION_INDEX_5_NAME),
                toPackage("com.example.android.airquality")));
    }

    @Test
    public void checkSendingIntent3() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        stubAllExternalIntents();

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(25).perform(click());

        intended(allOf(hasExtra("StationId", EXPECTED_STATION_INDEX_25_ID), hasExtra("StationName", EXPECTED_STATION_INDEX_25_NAME),
                toPackage("com.example.android.airquality")));
    }

    private void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}
