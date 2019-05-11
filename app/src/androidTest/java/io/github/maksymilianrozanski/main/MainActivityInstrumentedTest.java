package io.github.maksymilianrozanski.main;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.StationList;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    private MockWebServer server;
    private final String expectedStation0Name = "mocked station name 1";
    private final String expectedStation1Name = "mocked station name 2";

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule
            = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        StationList.STATIONS_BASE_URL = server.url("/").toString();
    }

    @Test
    public void correctResponseTest() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        mainActivityRule.launchActivity(intent);

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0)
                .onChildView(withId(R.id.stationListItemLinearLayout))
                .onChildView(withId(R.id.stationname)).check(matches(withText(expectedStation0Name)));

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(1)
                .onChildView(withId(R.id.stationListItemLinearLayout))
                .onChildView(withId(R.id.stationname)).check(matches(withText(expectedStation1Name)));
    }

    @Test
    public void invalidResponseTest() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(503)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        mainActivityRule.launchActivity(intent);

        onView(withText(R.string.could_not_connect_to_server)).inRoot(withDecorView(not(mainActivityRule.getActivity()
                .getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
        mainActivityRule.finishActivity();
    }
}
