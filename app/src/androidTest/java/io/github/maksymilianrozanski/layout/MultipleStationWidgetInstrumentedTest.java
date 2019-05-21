package io.github.maksymilianrozanski.layout;

import android.Manifest;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.StationList;
import io.github.maksymilianrozanski.main.MainActivity;
import io.github.maksymilianrozanski.main.RestServiceTestHelper;
import io.github.maksymilianrozanski.widget.FetchWidgetItem;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MultipleStationWidgetInstrumentedTest {

    private UiDevice device;
    private MockWebServer server;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule
            = new ActivityTestRule<>(MainActivity.class, true, false);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void serverSetup() throws Exception {
        String fileName = "stationsResponse.json";
        String station544FileName = "station544sensors.json";
        String station530FileName = "station530sensors.json";
        String station531FileName = "station531sensors.json";
        String station552FileName = "station552sensors.json";
        String station488FileName = "station488sensors.json";
        String station550FileName = "station550sensors.json";

        String sensor3688FileName = "sensor3688.json";
        String sensor3694FileName = "sensor3694.json";
        String sensor3691FileName = "sensor3691.json";
        String sensor16287FileName = "sensor16287.json";

        String sensor3576FileName = "sensor3576.json";
        String sensor3584FileName = "sensor3584.json";
        String sensor3575FileName = "sensor3575.json";
        String sensor3580FileName = "sensor3580.json";
        String sensor3585FileName = "sensor3585.json";

        String sensor3591FileName = "sensor3591.json";

        String sensor3764FileName = "sensor3764.json";
        String sensor3760FileName = "sensor3760.json";
        String sensor14688FileName = "sensor14688.json";    //null values
        String sensor3762FileName = "sensor3762.json";
        String sensor3769FileName = "sensor3769.json";

        String sensor3339FileName = "sensor3339.json";
        String sensor14779FileName = "sensor14779.json";
        String sensor14352FileName = "sensor14352.json";
        String sensor3348FileName = "sensor3348.json";

        String sensor3730FileName = "sensor3730.json";
        String sensor3725FileName = "sensor3725.json";
        String sensor3731FileName = "sensor3731.json";
        String sensor3727FileName = "sensor3727.json";
        String sensor3736FileName = "sensor3736.json";

        server = new MockWebServer();
        server.start();
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                try {
                    switch (request.getPath()) {
                        case "/pjp-api/rest/station/findAll/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName));
                        case "/pjp-api/rest/station/sensors/544/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station544FileName));
                        case "/pjp-api/rest/station/sensors/530/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station530FileName));
                        case "/pjp-api/rest/station/sensors/531/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station531FileName));
                        case "/pjp-api/rest/station/sensors/552/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station552FileName));
                        case "/pjp-api/rest/station/sensors/488/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station488FileName));
                        case "/pjp-api/rest/station/sensors/550/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station550FileName));
                        case "/pjp-api/rest/data/getData/3688/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3688FileName));
                        case "/pjp-api/rest/data/getData/3694/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3694FileName));
                        case "/pjp-api/rest/data/getData/3691/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3691FileName));
                        case "/pjp-api/rest/data/getData/16287/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor16287FileName));
                        case "/pjp-api/rest/data/getData/3576/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3576FileName));
                        case "/pjp-api/rest/data/getData/3584/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3584FileName));
                        case "/pjp-api/rest/data/getData/3575/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3575FileName));
                        case "/pjp-api/rest/data/getData/3580/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3580FileName));
                        case "/pjp-api/rest/data/getData/3585/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3585FileName));
                        case "/pjp-api/rest/data/getData/3591/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3591FileName));
                        case "/pjp-api/rest/data/getData/3764/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3764FileName));
                        case "/pjp-api/rest/data/getData/3760/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3760FileName));
                        case "/pjp-api/rest/data/getData/14688/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor14688FileName));
                        case "/pjp-api/rest/data/getData/3762/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3762FileName));
                        case "/pjp-api/rest/data/getData/3769/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3769FileName));
                        case "/pjp-api/rest/data/getData/3339/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3339FileName));
                        case "/pjp-api/rest/data/getData/14779/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor14779FileName));
                        case "/pjp-api/rest/data/getData/14352/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor14352FileName));
                        case "/pjp-api/rest/data/getData/3348/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3348FileName));
                        case "/pjp-api/rest/data/getData/3730/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3730FileName));
                        case "/pjp-api/rest/data/getData/3725/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3725FileName));
                        case "/pjp-api/rest/data/getData/3731/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3731FileName));
                        case "/pjp-api/rest/data/getData/3727/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3727FileName));
                        case "/pjp-api/rest/data/getData/3736/":
                            return new MockResponse().setResponseCode(200)
                                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3736FileName));
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        StationList.STATIONS_BASE_URL = server.url("/").toString();
        FetchWidgetItem.timeInHours = 14000;     //time in hours since 19/12/2017 17:30   //TODO: make value calculated automatically
    }

    @Before
    public void before() {
        device = UiDevice.getInstance(getInstrumentation());

        assertThat(device, notNullValue());

        // Start from the home screen
        device.pressHome();
    }

    //Before test mock location to: Pałac Kultury, Warszawa 52.231964, 21.005927,
    //place multiple station widget on home screen,set refresh button  to visible
    //TODO: isn't passing if stations are not sorted by distance before test
    @Test
    public void widgetTest() throws Exception {
        mainActivityRule.launchActivity(new Intent());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.reload_data)).perform(click());

        device.pressHome();

        String refreshButtonText = getInstrumentation().getTargetContext().getString(R.string.refresh).toUpperCase();
        UiObject2 refreshButton = device.findObject(By.text(refreshButtonText));

        refreshButton.click();

        String expectedZeroStationName = "Warszawa-Marszałkowska";
        UiObject stationIndexZeroNameObject = device.findObject(new UiSelector().text(expectedZeroStationName));
        Assert.assertEquals(stationIndexZeroNameObject.getText(), expectedZeroStationName);

        String expectedZeroStationValue = "PM2.5: 114%";
        UiObject stationIndexZeroObjectValue = device.findObject(new UiSelector().text(expectedZeroStationValue));
        Assert.assertEquals(stationIndexZeroObjectValue.getText(), expectedZeroStationValue);

        String expectedZeroStationDate = "2017-12-19 13:00";
        UiObject stationIndexZeroDate = device.findObject(new UiSelector().text(expectedZeroStationDate));
        Assert.assertEquals(stationIndexZeroDate.getText(), expectedZeroStationDate);

        String expectedOneStationName = "Warszawa-Komunikacyjna";
        UiObject stationIndexOneNameObject = device.findObject(new UiSelector().text(expectedOneStationName));
        Assert.assertEquals(stationIndexOneNameObject.getText(), expectedOneStationName);

        String expectedOneStationValue = "PM2.5: 195%";
        UiObject stationIndexOneObjectValue = device.findObject(new UiSelector().text(expectedOneStationValue));
        Assert.assertEquals(stationIndexOneObjectValue.getText(), expectedOneStationValue);

        String expectedOneStationDate = "2017-12-19 16:00";
        UiObject stationIndexOneDate = device.findObject(new UiSelector().text(expectedOneStationDate));
        Assert.assertEquals(stationIndexOneDate.getText(), expectedOneStationDate);
        //TODO: add assertion, check: is background of 4th list item - station name grey: color NoData
    }
}
