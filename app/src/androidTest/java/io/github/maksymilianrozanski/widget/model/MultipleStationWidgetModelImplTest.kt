package io.github.maksymilianrozanski.widget.model

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.util.Log
import io.github.maksymilianrozanski.any
import io.github.maksymilianrozanski.dataholders.StationList
import io.github.maksymilianrozanski.main.RestServiceTestHelper
import io.github.maksymilianrozanski.widget.ConnectionCheck
import io.github.maksymilianrozanski.widget.FetchWidgetItem
import io.github.maksymilianrozanski.widget.MultipleStationWidgetContract
import io.github.maksymilianrozanski.widget.MyLocationProvider
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MultipleStationWidgetModelImplTest {

    private lateinit var locationProviderMock: MyLocationProvider
    private lateinit var mockWebServer: MockWebServer
    private lateinit var contextMock: Context
    private lateinit var connectionCheckMock: ConnectionCheck
    private lateinit var sharedPreferencesMock: SharedPreferences
    private lateinit var sharedPreferencesEditorMock: SharedPreferences.Editor

    @Before
    fun setup() {
        locationProviderMock = LocationProviderMock()
        contextMock = Mockito.mock(Context::class.java)
        connectionCheckMock = ConnectionCheckMock()
        sharedPreferencesMock = Mockito.mock(SharedPreferences::class.java)
        sharedPreferencesEditorMock = Mockito.mock(SharedPreferences.Editor::class.java)

        Mockito.`when`(contextMock.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock)
        Mockito.`when`(sharedPreferencesMock.getString("STATIONS", null)).thenReturn(
                RestServiceTestHelper.getStringFromFile(getInstrumentation().context, "stationsResponse.json"))
        Mockito.`when`(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock)
    }

    @Before
    fun serverSetup() {
        val fileName = "stationsResponse.json"
        val station544FileName = "station544sensors.json"
        val station530FileName = "station530sensors.json"
        val station531FileName = "station531sensors.json"
        val station552FileName = "station552sensors.json"
        val station488FileName = "station488sensors.json"
        val station550FileName = "station550sensors.json"

        val sensor3688FileName = "sensor3688.json"
        val sensor3694FileName = "sensor3694.json"
        val sensor3691FileName = "sensor3691.json"
        val sensor16287FileName = "sensor16287.json"

        val sensor3576FileName = "sensor3576.json"
        val sensor3584FileName = "sensor3584.json"
        val sensor3575FileName = "sensor3575.json"
        val sensor3580FileName = "sensor3580.json"
        val sensor3585FileName = "sensor3585.json"

        val sensor3591FileName = "sensor3591.json"

        val sensor3764FileName = "sensor3764.json"
        val sensor3760FileName = "sensor3760.json"
        val sensor14688FileName = "sensor14688.json"    //null values
        val sensor3762FileName = "sensor3762.json"
        val sensor3769FileName = "sensor3769.json"

        val sensor3339FileName = "sensor3339.json"
        val sensor14779FileName = "sensor14779.json"
        val sensor14352FileName = "sensor14352.json"
        val sensor3348FileName = "sensor3348.json"

        val sensor3730FileName = "sensor3730.json"
        val sensor3725FileName = "sensor3725.json"
        val sensor3731FileName = "sensor3731.json"
        val sensor3727FileName = "sensor3727.json"
        val sensor3736FileName = "sensor3736.json"

        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockWebServer.setDispatcher(object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse? {
                try {
                    when (request.path) {
                        "/pjp-api/rest/station/findAll/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, fileName))
                        "/pjp-api/rest/station/sensors/544/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, station544FileName))
                        "/pjp-api/rest/station/sensors/530/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, station530FileName))
                        "/pjp-api/rest/station/sensors/531/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, station531FileName))
                        "/pjp-api/rest/station/sensors/552/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, station552FileName))
                        "/pjp-api/rest/station/sensors/488/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, station488FileName))
                        "/pjp-api/rest/station/sensors/550/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, station550FileName))
                        "/pjp-api/rest/data/getData/3688/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3688FileName))
                        "/pjp-api/rest/data/getData/3694/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3694FileName))
                        "/pjp-api/rest/data/getData/3691/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3691FileName))
                        "/pjp-api/rest/data/getData/16287/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor16287FileName))
                        "/pjp-api/rest/data/getData/3576/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3576FileName))
                        "/pjp-api/rest/data/getData/3584/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3584FileName))
                        "/pjp-api/rest/data/getData/3575/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3575FileName))
                        "/pjp-api/rest/data/getData/3580/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3580FileName))
                        "/pjp-api/rest/data/getData/3585/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3585FileName))
                        "/pjp-api/rest/data/getData/3591/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3591FileName))
                        "/pjp-api/rest/data/getData/3764/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3764FileName))
                        "/pjp-api/rest/data/getData/3760/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3760FileName))
                        "/pjp-api/rest/data/getData/14688/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor14688FileName))
                        "/pjp-api/rest/data/getData/3762/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3762FileName))
                        "/pjp-api/rest/data/getData/3769/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3769FileName))
                        "/pjp-api/rest/data/getData/3339/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3339FileName))
                        "/pjp-api/rest/data/getData/14779/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor14779FileName))
                        "/pjp-api/rest/data/getData/14352/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor14352FileName))
                        "/pjp-api/rest/data/getData/3348/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3348FileName))
                        "/pjp-api/rest/data/getData/3730/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3730FileName))
                        "/pjp-api/rest/data/getData/3725/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3725FileName))
                        "/pjp-api/rest/data/getData/3731/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3731FileName))
                        "/pjp-api/rest/data/getData/3727/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3727FileName))
                        "/pjp-api/rest/data/getData/3736/" -> return MockResponse().setResponseCode(200)
                                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().context, sensor3736FileName))
                        else -> Log.e("Inside_Test", "Requested url other than specified: " + request.path)
                    }
                } catch (e: Exception) {
                    Log.e("Log", "exception$e")
                    e.printStackTrace()
                }

                return null
            }
        })

        StationList.STATIONS_BASE_URL = mockWebServer.url("/").toString()
        FetchWidgetItem.timeInHours = 14000;     //time in hours since 19/12/2017 17:30 
    }

    @Test
    fun fetchDataTest() {
        val onFinishedListenerMock = Mockito.mock(MultipleStationWidgetContract.Model.OnFinishedListener::class.java)

        val modelImpl = MultipleStationWidgetModelImpl(contextMock, locationProviderMock, connectionCheckMock)
        modelImpl.fetchData(onFinishedListenerMock)

//TODO: add argument verification
        Mockito.verify(onFinishedListenerMock).onFinished(any())
    }

    private class LocationProviderMock : MyLocationProvider {
        override fun getLocation(): Location {
            val location = Location("")
            location.latitude = 52.231964
            location.longitude = 21.005927
            return location
        }
    }

    private class ConnectionCheckMock : ConnectionCheck {
        override fun isConnected(context: Context): Boolean {
            return true
        }
    }
}