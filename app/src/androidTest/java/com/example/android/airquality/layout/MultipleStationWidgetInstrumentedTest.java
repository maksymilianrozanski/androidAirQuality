package com.example.android.airquality.layout;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.example.android.airquality.dataholders.StationList;
import com.example.android.airquality.main.RestServiceTestHelper;

import org.junit.Before;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MultipleStationWidgetInstrumentedTest extends InstrumentationTestCase {

    private UiDevice device;
    private MockWebServer server;

    @Before//Before test mock location to: Pałac Kultury, Warszawa 52.231964, 21.005927
    public void serverSetup() throws Exception {
        String fileName = "stationsResponse.json";
        String station544FileName = "station544sensors.json";
        String station530FileName = "station530sensors.json";
        String station531FileName = "station531sensors.json";
        String station552FileName = "station552sensors.json";
        String station488FileName = "station488sensors.json";

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

        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                try {
                    if (request.getPath().equals("pjp-api/rest/station/findAll/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName));
                    } else if (request.getPath().equals("pjp-api/rest/station/sensors/544/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station544FileName));
                    } else if (request.getPath().equals("pjp-api/rest/station/sensors/530/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station530FileName));
                    } else if (request.getPath().equals("pjp-api/rest/station/sensors/531/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station531FileName));
                    } else if (request.getPath().equals("pjp-api/rest/station/sensors/552/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station552FileName));
                    } else if (request.getPath().equals("pjp-api/rest/station/sensors/488/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), station488FileName));
                    }
                    //values for station 544
                    else if (request.getPath().equals("/pjp-api/rest/data/getData/3688/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3688FileName));
                    } else if (request.getPath().equals("/pjp-api/rest/data/getData/3694/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3694FileName));
                    } else if (request.getPath().equals("/pjp-api/rest/data/getData/3691/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3691FileName));
                    } else if (request.getPath().equals("/pjp-api/rest/data/getData/16287/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor16287FileName));
                    }
                    //values for station 530
                    else if (request.getPath().equals("/pjp-api/rest/data/getData/3576/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3576FileName));
                    } else if (request.getPath().equals("/pjp-api/rest/data/getData/3584/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3584FileName));
                    } else if (request.getPath().equals("/pjp-api/rest/data/getData/3575/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3575FileName));
                    } else if (request.getPath().equals("/pjp-api/rest/data/getData/3580/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3580FileName));
                    } else if (request.getPath().equals("/pjp-api/rest/data/getData/3585/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3585FileName));
                    }
                    //values for station 531
                    else if (request.getPath().equals("/pjp-api/rest/data/getData/3591/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3591FileName));
                    }
                    //values for station 552
                    else if (request.getPath().equals("/pjp-api/rest/data/getData/3764/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3764FileName));
                    }else if (request.getPath().equals("/pjp-api/rest/data/getData/3760/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3760FileName));
                    }else if (request.getPath().equals("/pjp-api/rest/data/getData/14688/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor14688FileName));
                    }else if (request.getPath().equals("/pjp-api/rest/data/getData/3762/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3762FileName));
                    }else if (request.getPath().equals("/pjp-api/rest/data/getData/3769/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3769FileName));
                    }
                    //values for station 488
                    else if (request.getPath().equals("/pjp-api/rest/data/getData/3339/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3339FileName));
                    }else if (request.getPath().equals("/pjp-api/rest/data/getData/14779/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor14779FileName));
                    }else if (request.getPath().equals("/pjp-api/rest/data/getData/14352/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor14352FileName));
                    }else if (request.getPath().equals("/pjp-api/rest/data/getData/3348/")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), sensor3348FileName));
                    }
                } catch (Exception e) {
                    Log.e("Log", "exception" + e);
                    e.printStackTrace();
                }
                return null;
            }
        });

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        StationList.STATIONS_BASE_URL = server.url("/").toString();

        //TODO: mock device time (19/12/2017 17:30), write code to: re-download list of stations, check if widget is correctly displayed
    }

    @Before
    public void before() throws Exception {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertThat(device, notNullValue());

        // Start from the home screen
        device.pressHome();
    }
}
