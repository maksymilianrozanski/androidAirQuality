package io.github.maksymilianrozanski.main;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

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
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static io.github.maksymilianrozanski.TestHelperKt.stubAllIntents;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;


@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    private MockWebServer server;
    private final String expectedStation0Name = "mocked station name 1";
    private final String expectedStation1Name = "mocked station name 2";

    @Rule
    public IntentsTestRule<MainActivity> mainActivityRule
            = new IntentsTestRule<>(MainActivity.class, true, false);

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

    @Test
    public void nearestStationAskPermission() throws Exception {
        UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        mainActivityRule.launchActivity(null);

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0)
                .onChildView(withId(R.id.stationListItemLinearLayout))
                .onChildView(withId(R.id.stationname)).check(matches(withText(expectedStation0Name)));

        mainActivityRule.getActivity().goToNearestStation();
        stubAllIntents();

        uiDevice.findObject(new UiSelector().text("ALLOW")).click();

        intended(allOf(hasExtra("StationId", 544), hasExtra("StationName", "Warszawa-Marszałkowska"),
                toPackage("io.github.maksymilianrozanski")));

    }

    @Test
    public void nearestStationPermissionDenied() throws Exception {
        UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        mainActivityRule.launchActivity(null);

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0)
                .onChildView(withId(R.id.stationListItemLinearLayout))
                .onChildView(withId(R.id.stationname)).check(matches(withText(expectedStation0Name)));

        mainActivityRule.getActivity().goToNearestStation();
        uiDevice.findObject(new UiSelector().text("DENY")).click();
    }

    @Test
    public void sortStationsByDistanceAskForPermission() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        mainActivityRule.launchActivity(null);

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0)
                .onChildView(withId(R.id.stationListItemLinearLayout))
                .onChildView(withId(R.id.stationname)).check(matches(withText(expectedStation0Name)));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.sort_stations_by_distance)).perform(click());

        UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());
        uiDevice.findObject(new UiSelector().text("ALLOW")).click();

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0)
                .onChildView(withId(R.id.stationListItemLinearLayout))
                .onChildView(withId(R.id.stationname)).check(matches(withText("Warszawa-Marszałkowska")));
    }

    @Test
    public void sortStationsByDistancePermissionDenied() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        mainActivityRule.launchActivity(null);

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0)
                .onChildView(withId(R.id.stationListItemLinearLayout))
                .onChildView(withId(R.id.stationname)).check(matches(withText(expectedStation0Name)));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.sort_stations_by_distance)).perform(click());

        UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());
        uiDevice.findObject(new UiSelector().text("DENY")).click();

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0)
                .onChildView(withId(R.id.stationListItemLinearLayout))
                .onChildView(withId(R.id.stationname)).check(matches(withText(expectedStation0Name)));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
        mainActivityRule.finishActivity();
    }
}
