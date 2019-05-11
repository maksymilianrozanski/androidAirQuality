package io.github.maksymilianrozanski.utility;

import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QueryUtilitiesTestInstrumented {

    private MockWebServer mServer;

    @Before
    public void setUp() throws Exception {
        mServer = new MockWebServer();
        mServer.play();

    }

    @After
    public void tearDown() throws Exception {
        mServer.shutdown();
    }

    @Test
    public void httpRequestTestOk() throws Exception {
        String mockedResponse = "my server response...";
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody(mockedResponse));
        URL url = mServer.getUrl("/");

        String response = QueryUtilities.retryMakingHttpRequestIfException(url);
        assertEquals(response, mockedResponse);
    }

    @LargeTest
    @Test(expected = IOException.class)
    public void httpRequestTestResponseCode404() throws Exception {
        String mockedResponse = "my server response...";
        mServer.enqueue(new MockResponse().setResponseCode(404).setBody(mockedResponse));
        URL url = mServer.getUrl("/");

        String response = QueryUtilities.retryMakingHttpRequestIfException(url);
    }

    @Test
    public void getStringFromJsonObjectTest() throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");
        jsonObject.put("key2", "value2");

        String obtainedValue1 = QueryUtilities.getStringFromJSONObject(jsonObject, "key1");
        String obtainedValue2 = QueryUtilities.getStringFromJSONObject(jsonObject, "key2");

        Assert.assertEquals("value1", obtainedValue1);
        Assert.assertEquals("value2", obtainedValue2);
    }

    @Test
    public void getStringFromJsonKeyNotExist() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");
        jsonObject.put("key2", "value2");

        String obtainedValue = QueryUtilities.getStringFromJSONObject(jsonObject, "notExistingKey");
        Assert.assertEquals("not specified", obtainedValue);
    }
}
