package io.github.maksymilianrozanski.main;

import android.Manifest;
import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.StationList;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static io.github.maksymilianrozanski.TestHelperKt.atPosition;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityMenuInstrumentedTest {

    private MockWebServer server;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule
            = new ActivityTestRule<>(MainActivity.class, true, false);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() throws Exception {

        server = new MockWebServer();
        server.start();

        StationList.STATIONS_BASE_URL = server.url("/").toString();
    }

    @Test
    public void displayingMenuTest() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        mainActivityRule.launchActivity(intent);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.reload_data)).check(matches(isDisplayed()));
        onView(withText(R.string.find_nearest_station)).check(matches(isDisplayed()));
        onView(withText(R.string.sort_stations_by_distance)).check(matches(isDisplayed()));
        onView(withText(R.string.sort_stations_by_city_name)).check(matches(isDisplayed()));
    }

    @Test   //Before test mock location to: Pałac Kultury, Warszawa 52.231964, 21.005927
    public void findNearestStationTest() throws Exception {
        String fileName = "stationsResponse.json";
        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        mainActivityRule.launchActivity(intent);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.find_nearest_station)).perform(click());
        onView(withId(R.id.sensorsViewStationName)).check(matches(withText("Warszawa-Marszałkowska")));
    }

    @Test
    public void reloadDataTest() throws Exception {
        String fileName = "stationsResponse.json";
        String fileName2 = "stationsResponse2.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));
        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName2)));

        Intent intent = new Intent();
        mainActivityRule.launchActivity(intent);

        onView(withText("mocked station name 1")).check(matches(isDisplayed()));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.reload_data)).perform(click());

        onView(withText("mocked station name 1 updated")).check(matches(isDisplayed()));
    }

    @Test
    public void menuClickDuringLoading() throws Exception {
        String fileName = "stationsResponse.json";
        String fileName2 = "stationsResponse2.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        server.enqueue(new MockResponse().setResponseCode(200).setBodyDelay(2500, TimeUnit.MILLISECONDS)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName2)));

        Intent intent = new Intent();
        mainActivityRule.launchActivity(intent);

        onView(withText("mocked station name 1")).check(matches(isDisplayed()));


        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        Thread uiAutomatorThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                    UiDevice device = UiDevice.getInstance(getInstrumentation());
                    assertThat(device, notNullValue());

                    device.pressMenu();
                    UiObject2 menuRefreshButton = device.findObject(By.text(mainActivityRule.getActivity().getString(R.string.reload_data)));
                    assertTrue(menuRefreshButton.isEnabled());
                    //menu buttons, except refresh button should be disabled - clicking them should do nothing
                    UiObject2 menuFindNearestStation = device.findObject(By.text(mainActivityRule.getActivity().getString(R.string.find_nearest_station)));
                    menuFindNearestStation.click();
                    UiObject2 menuSortByDistance = device.findObject(By.text(mainActivityRule.getActivity().getString(R.string.sort_stations_by_distance)));
                    menuSortByDistance.click();
                    UiObject2 menuSortByCityName = device.findObject(By.text(mainActivityRule.getActivity().getString(R.string.sort_stations_by_city_name)));
                    menuSortByCityName.click();
                    device.pressBack();
                } catch (InterruptedException e) {
                    Log.e("Log", "Anonymous thread interrupted");
                    e.printStackTrace();
                }
            }
        };
        uiAutomatorThread.start();

        onView(withText(R.string.reload_data)).perform(click());
        uiAutomatorThread.join();
        onView(withText("mocked station name 1 updated")).check(matches(isDisplayed()));
    }

    @Test
    public void sortByCityNameTest() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        mainActivityRule.launchActivity(intent);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.sort_stations_by_city_name)).perform(click());

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0, hasDescendant(withText("Augustów - mobilne ")))));
        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(0, hasDescendant(withText("Augustów")))));

        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(1, hasDescendant(withText("Belsk-IGFPAN")))));
        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(1, hasDescendant(withText("Belsk Duży")))));

        onView(withId(R.id.stationsRecyclerView)).perform(RecyclerViewActions.scrollToPosition(19));
        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(19, hasDescendant(withText("KMŚ Puszcza Borecka")))));
        onView(withId(R.id.stationsRecyclerView)).check(matches(atPosition(19, hasDescendant(withText("Diabla Góra")))));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}
