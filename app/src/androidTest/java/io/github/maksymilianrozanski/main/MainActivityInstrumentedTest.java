package io.github.maksymilianrozanski.main;

import android.content.Intent;

import androidx.core.os.BuildCompat;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static io.github.maksymilianrozanski.TestHelperKt.atPosition;
import static io.github.maksymilianrozanski.TestHelperKt.stubAllIntents;
import static org.hamcrest.Matchers.containsString;
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

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0,
                hasDescendant(withText(containsString(expectedStation0Name))))));

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(1,
                hasDescendant(withText(containsString(expectedStation1Name))))));
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

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0,
                hasDescendant(withText(containsString(expectedStation0Name))))));

        mainActivityRule.getActivity().goToNearestStation();
        stubAllIntents();

        //TODO: remove repeated code of setting Allow/Deny text
        String allowText;
        if (BuildCompat.isAtLeastQ()) allowText = "Allow only while using the app";
        else allowText = "ALLOW";

        uiDevice.findObject(new UiSelector().text(allowText)).click();

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

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0,
                hasDescendant(withText(containsString(expectedStation0Name))))));

        mainActivityRule.getActivity().goToNearestStation();

        String denyText;
        if (BuildCompat.isAtLeastQ()) denyText = "Deny";
        else denyText = "DENY";
        uiDevice.findObject(new UiSelector().text(denyText)).click();
    }

    @Test
    public void sortStationsByDistanceAskForPermission() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        mainActivityRule.launchActivity(null);

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0,
                hasDescendant(withText(containsString(expectedStation0Name))))));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.sort_stations_by_distance)).perform(click());

        UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());

        String allowText;
        if (BuildCompat.isAtLeastQ()) allowText = "Allow only while using the app";
        else allowText = "ALLOW";
        uiDevice.findObject(new UiSelector().text(allowText)).click();

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0,
                hasDescendant(withText(containsString("Warszawa-Marszałkowska"))))));
    }

    @Test
    public void sortStationsByDistancePermissionDenied() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        mainActivityRule.launchActivity(null);

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0,
                hasDescendant(withText(containsString(expectedStation0Name))))));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.sort_stations_by_distance)).perform(click());

        UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());

        String denyText;
        if (BuildCompat.isAtLeastQ()) denyText = "Deny";
        else denyText = "DENY";
        uiDevice.findObject(new UiSelector().text(denyText)).click();

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0,
                hasDescendant(withText(containsString(expectedStation0Name))))));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
        mainActivityRule.finishActivity();
    }
}
