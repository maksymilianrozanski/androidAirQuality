package io.github.maksymilianrozanski.dataholders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class SensorListTest {

    private List<Sensor> sensors;
    private GregorianCalendar mockedGregorianCalendar;

    @Before
    public void setup() {
        mockedGregorianCalendar = Mockito.mock(GregorianCalendar.class);
        //time: 19:36   26.04.2018 GMT
        Mockito.when(mockedGregorianCalendar.getTimeInMillis()).thenReturn( 1524771360000L);

        Sensor sensor1 = new Sensor(1, "PM10");
        sensor1.setLastDate("2018-04-26 18:00:00");

        Sensor sensor2 = new Sensor(2, "SO2");
        sensor2.setLastDate("2018-04-26 17:00:00");

        sensors = new ArrayList<>();
        sensors.add(sensor1);
        sensors.add(sensor2);

    }

    @Test
    public void removeSensorsWhereValueOlderThanTest() {
        SensorList sensorList = new SensorList(sensors);
        sensorList.calendar = mockedGregorianCalendar;
        Assert.assertEquals(2, sensorList.getList().size());

        sensorList.removeSensorsWhereValueOlderThan(5L);
        Assert.assertEquals(2, sensorList.getList().size());
    }

    @Test
    public void removeSensorsWhereValueOlderThanTest2() {
        SensorList sensorList = new SensorList(sensors);
        sensorList.calendar = mockedGregorianCalendar;
        Assert.assertEquals(2, sensorList.getList().size());

        sensorList.removeSensorsWhereValueOlderThan(1L);
        Assert.assertEquals(0, sensorList.getList().size());
    }

    @Test
    public void removeSensorsWhereValueOlderThanTest3() {
        SensorList sensorList = new SensorList(sensors);
        sensorList.calendar = mockedGregorianCalendar;
        Assert.assertEquals(2, sensorList.getList().size());

        sensorList.removeSensorsWhereValueOlderThan(2L);
        Assert.assertEquals(1, sensorList.getList().size());
    }

    @Test
    public void removeSensorsWhereValueOlderThanTest4() {
        Sensor sensor3 = new Sensor(3, "PM10");
        sensor3.setLastDate("2018-04-26 18:00:00");

        Sensor sensor4 = new Sensor(4, "NO2");
        sensor4.setLastDate(Sensor.DEFAULT_DATE);

        List<Sensor> sensors2 = new ArrayList<>();
        sensors2.add(sensor3);
        sensors2.add(sensor4);

        SensorList sensorList2 = new SensorList(sensors2);
        sensorList2.calendar = mockedGregorianCalendar;

        Assert.assertEquals(2, sensorList2.getList().size());

        sensorList2.removeSensorsWhereValueOlderThan(5L);

        Assert.assertEquals(1, sensorList2.getList().size());
    }
}