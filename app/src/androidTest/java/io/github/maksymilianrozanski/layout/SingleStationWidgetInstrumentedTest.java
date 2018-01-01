package io.github.maksymilianrozanski.layout;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.maksymilianrozanski.R;

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

    @Test   //TODO: mock server
    public void firstTest() throws Exception {
        Thread.sleep(1000);
        final String initialWidgetText = InstrumentationRegistry.getTargetContext().getString(R.string.tap_to_refresh);
        Thread.sleep(1000);

        UiObject2 singleStationWidget = device.findObject(By.text(initialWidgetText));

        Thread.sleep(1000);
        singleStationWidget.click();
        Thread.sleep(1000);

        UiObject2 stationName = device.findObject(By.text("mocked station name 1"));

        Assert.assertTrue(stationName != null);
    }
}
