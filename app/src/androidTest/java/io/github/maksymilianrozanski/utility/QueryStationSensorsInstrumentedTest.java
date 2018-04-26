package io.github.maksymilianrozanski.utility;

import org.junit.Assert;
import org.junit.Test;

import io.github.maksymilianrozanski.dataholders.Sensor;

public class QueryStationSensorsInstrumentedTest {

    @Test
    public void addValueAndDateInstrumentedTest() {
        Sensor sensor = new Sensor(1, "NO2");
        String response = "{\"key\":\"C6H6\",\"values\":[" +
                "{\"date\":\"2018-04-26 20:00:00\",\"value\":null}," +
                "{\"date\":\"2018-04-26 19:00:00\",\"value\":1.143}]}";
        QueryStationSensors.addValueAndDate(sensor, response);
        Assert.assertTrue(sensor.getValue() == 1.143);
        Assert.assertTrue(sensor.getLastDate().equals("2018-04-26 19:00:00"));
    }

    @Test
    public void addValueAndDateInstrumentedTest2() {
        Sensor sensor = new Sensor(1, "NO2");
        String response = "{\"key\":\"C6H6\",\"values\":[]}";
        QueryStationSensors.addValueAndDate(sensor, response);
        Assert.assertTrue(sensor.getValue() == 0);
        Assert.assertTrue(sensor.getLastDate().equals("no data to display"));
        //TODO: modify code to return default date
    }
}