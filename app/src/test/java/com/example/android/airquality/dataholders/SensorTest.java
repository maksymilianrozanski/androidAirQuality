package com.example.android.airquality.dataholders;

import com.example.android.airquality.vieweditors.SensorAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SensorAdapter.class)
public class SensorTest {

    @Before
    public void initialize() throws Exception {
        Map<String, Integer> maxConcentrationsMap = new HashMap<String, Integer>();
        maxConcentrationsMap.put("C6H6", 5);
        maxConcentrationsMap.put("NO2", 200);
        maxConcentrationsMap.put("SO2", 125);
        maxConcentrationsMap.put("PM10", 50);
        maxConcentrationsMap.put("CO", 10000);
        maxConcentrationsMap.put("PM2.5", 25);
        maxConcentrationsMap.put("O3", 120);

        mockStatic(SensorAdapter.class, Mockito.CALLS_REAL_METHODS);
        PowerMockito.doReturn(maxConcentrationsMap).when(SensorAdapter.class, "getMaxConcentrations");
    }

    @Test
    public void percentOfMaxValueTest1() throws Exception {

        Sensor sensor = new Sensor();
        sensor.setId(10);
        sensor.setValue(25);
        sensor.setParam("PM2.5");
        Double percentOfMaxValue = sensor.percentOfMaxValue();
        String percentOfMaxValueString = String.format("%.0f", percentOfMaxValue);
        String expectedValue = "100";

        assertTrue(expectedValue.equals(percentOfMaxValueString));
    }

    @Test
    public void percentOfMaxValueTest2() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setId(10);
        sensor.setValue(75);
        sensor.setParam("PM10");
        Double percentOfMaxValue = sensor.percentOfMaxValue();
        String percentOfMaxValueString = String.format("%.0f", percentOfMaxValue);
        String expectedValue = "150";

        assertTrue(expectedValue.equals(percentOfMaxValueString));
    }

    @Test
    public void percentOfMaxValueKeyNotInMap() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setId(10);
        sensor.setValue(25);
        sensor.setParam("KeyNotInMap");
        Double value = sensor.percentOfMaxValue();
        assertTrue(value == -1);
    }
}
