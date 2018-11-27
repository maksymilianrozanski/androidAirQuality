package io.github.maksymilianrozanski.main;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.StationList;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityMenuInstrumentedTest {

    private MockWebServer server;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule
            = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws Exception {

        server = new MockWebServer();
        server.start();

        StationList.STATIONS_BASE_URL = server.url("/").toString();
    }

    @Before
    public void clearSharedPreferences() {
        File root = InstrumentationRegistry.getTargetContext().getFilesDir().getParentFile();
        String[] sharedPreferencesFileNames = new File(root, "shared_prefs").list();
        for (String fileName : sharedPreferencesFileNames) {
            InstrumentationRegistry.getTargetContext().getSharedPreferences(fileName.replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().commit();
        }
    }

    @Test
    public void displayingMenuTest() throws Exception {
        String fileName = "stationsResponse.json";

        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        mainActivityRule.launchActivity(intent);

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
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

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
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

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
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


        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());

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

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.sort_stations_by_city_name)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).onChildView(withId(R.id.stationname)).check(matches(withText("Augustów - mobilne ")));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).onChildView(withId(R.id.cityname)).check(matches(withText("Augustów")));

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(1).onChildView(withId(R.id.stationname)).check(matches(withText("Belsk-IGFPAN")));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(1).onChildView(withId(R.id.cityname)).check(matches(withText("Belsk Duży")));

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(19).onChildView(withId(R.id.stationname)).check(matches(withText("KMŚ Puszcza Borecka")));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(19).onChildView(withId(R.id.cityname)).check(matches(withText("Diabla Góra")));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}
