package io.github.maksymilianrozanski.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.StationList;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class SingleStationActivityInstrumentedTest2 extends InstrumentationTestCase {

    private MockWebServer server;

    @Rule
    public ActivityTestRule<SingleStationActivity> activityRule
            = new ActivityTestRule<>(SingleStationActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        //station id 10139
        server = new MockWebServer();
        server.start();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        StationList.STATIONS_BASE_URL = server.url("/").toString();
    }

    @Test
    public void checkDisplayingDataTest() throws Exception {
        String sensorsFileName = "sensorsPiastow10139.json";
        String sensorValuesFileName = "sensor16784values_obsolete.json";

        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                Log.v("Log", "request to mocked server: " + request.getPath());
                if (request.getPath().equals("/pjp-api/rest/station/sensors/10139/")) {
                    try {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensorsFileName));
                    } catch (Exception e) {
                        Log.e("log", "error inside test method" + e);
                    }
                } else if (request.getPath().equals("/pjp-api/rest/data/getData/16784/")) {
                    try {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensorValuesFileName));
                    } catch (Exception e) {
                        Log.e("log", "error inside test method" + e);
                    }
                }
                Log.e("Log", "invalid request to mocked server");
                return null;
            }
        });

        Intent intent = new Intent();
        intent.putExtra("StationId", 10139);
        intent.putExtra("StationName", "Kraków, os. Piastów");
        activityRule.launchActivity(intent);

        onData(anything()).inAdapterView(withId(R.id.listViewOfSensors)).atPosition(0)
                .onChildView(withId(R.id.sensorType)).check(matches(withText("PM10")));
        onData(anything()).inAdapterView(withId(R.id.listViewOfSensors)).atPosition(0)
                .onChildView(withId(R.id.paramValue)).check(matches(withText("100.00/50 μg/m³")));
        onData(anything()).inAdapterView(withId(R.id.listViewOfSensors)).atPosition(0)
                .onChildView(withId(R.id.date)).check(matches(withText("2017-12-05 18:00:00")));

        int noDataColor = ContextCompat.getColor(activityRule.getActivity(), R.color.noData);
        onData(anything()).inAdapterView(withId(R.id.listViewOfSensors)).atPosition(0)
                .onChildView(withId(R.id.date)).check(matches(withBackGroundColor(noDataColor)));

        onData(anything()).inAdapterView(withId(R.id.listViewOfSensors)).atPosition(0)
                .onChildView(withId(R.id.percentValue)).check(matches(withText("200%")));
    }

    public static Matcher<View> withBackGroundColor(int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView item) {
                ColorDrawable background = (ColorDrawable) item.getBackground();
                if (background != null) {
                    int backgroundColor = background.getColor();
                    Log.v("Log", "background color = " + background.getColor());
                    return color == backgroundColor;
                }
                Log.v("Log", "background == null");
                return false;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        server.shutdown();
    }
}
