package com.example.android.airquality.utility;

import android.support.test.runner.AndroidJUnit4;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QueryUtilitiesTest {

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
        assertTrue(response.equals(mockedResponse));
    }

}
