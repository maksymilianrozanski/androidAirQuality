package com.example.android.airquality.layout;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SingleStationWidgetInstrumentedTest extends InstrumentationTestCase {

    private UiDevice device;

    @Before
    public void before() throws Exception {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertThat(device, notNullValue());

        // Start from the home screen
        device.pressHome();
    }

    @Test
    public void firstTest() throws Exception {

    }
}
