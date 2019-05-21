package io.github.maksymilianrozanski.main;

import android.Manifest;
import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.StationList;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static io.github.maksymilianrozanski.TestHelperKt.stubAllIntents;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentsInstrumentedTest {

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

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        StationList.STATIONS_BASE_URL = server.url("/").toString();
    }

    @Test
    public void checkSendingIntent() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        stubAllIntents();
        onView(withId(R.id.stationsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(allOf(hasExtra("StationId", EXPECTED_STATION_INDEX_0_ID), hasExtra("StationName", EXPECTED_STATION_INDEX_0_NAME),
                toPackage("io.github.maksymilianrozanski")));
    }

    @Test
    public void checkSendingIntent2() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        stubAllIntents();
        onView(withId(R.id.stationsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(5, click()));
        intended(allOf(hasExtra("StationId", EXPECTED_STATION_INDEX_5_ID), hasExtra("StationName", EXPECTED_STATION_INDEX_5_NAME),
                toPackage("io.github.maksymilianrozanski")));
    }

    @Test
    public void checkSendingIntent3() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        stubAllIntents();
        onView(withId(R.id.stationsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(25, click()));
        intended(allOf(hasExtra("StationId", EXPECTED_STATION_INDEX_25_ID), hasExtra("StationName", EXPECTED_STATION_INDEX_25_NAME),
                toPackage("io.github.maksymilianrozanski")));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}
