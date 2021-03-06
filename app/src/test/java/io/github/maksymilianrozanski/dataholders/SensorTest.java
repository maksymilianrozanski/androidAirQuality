package io.github.maksymilianrozanski.dataholders;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import io.github.maksymilianrozanski.vieweditors.SensorAdapter;

import static junit.framework.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SensorAdapter.class)
public class SensorTest {

    @Before
    public void initialize() throws Exception {
        Map<String, Integer> maxConcentrationsMap = new HashMap<>();
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
    public void percentOfMaxValueTest1() {

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
    public void percentOfMaxValueTest2() {
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
    public void percentOfMaxValueKeyNotInMap() {
        Sensor sensor = new Sensor();
        sensor.setId(10);
        sensor.setValue(25);
        sensor.setParam("KeyNotInMap");
        Double value = sensor.percentOfMaxValue();
        assertTrue(value == -1);
    }

    @Test
    public void isDateDefaultTest() {
        Sensor sensor = new Sensor();
        sensor.setLastDate("2018-01-01 00:00:00");
        Assert.assertTrue(sensor.isDateDefault());

        sensor = new Sensor();
        sensor.setLastDate("2018-03-03 01:01:01");
        Assert.assertFalse(sensor.isDateDefault());
    }
}
