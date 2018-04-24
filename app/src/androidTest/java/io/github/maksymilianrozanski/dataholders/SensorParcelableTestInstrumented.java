package io.github.maksymilianrozanski.dataholders;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;

import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class SensorParcelableTestInstrumented {

    @Test
    public void parcelableImplementationTest() {

        Sensor sensor = new Sensor();
        sensor.setId(100);
        sensor.setParam("PM10");
        sensor.setValue(321.2d);
        sensor.setLastDate("10-10-2017 17:30");

        Parcel parcel = Parcel.obtain();

        sensor.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Sensor sensorCreatedFromParcel = (Sensor) Sensor.CREATOR.createFromParcel(parcel);

        assertTrue(sensor.getId() == sensorCreatedFromParcel.getId());
        assertTrue(sensor.getParam().equals(sensorCreatedFromParcel.getParam()));
        assertTrue(sensor.getValue() == sensorCreatedFromParcel.getValue());
        assertTrue(sensor.getLastDate().equals(sensorCreatedFromParcel.getLastDate()));
    }

    @Test
    public void getTimeInMillisTest() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setId(100);
        sensor.setParam("PM10");
        sensor.setValue(321.2d);
        sensor.setLastDate("1970-01-01 00:00:30");
        long calculatedTime = sensor.getTimeInMillis();
        long expectedTime = 30000L;
        assertTrue(calculatedTime == expectedTime);
    }

    @Test(expected = ParseException.class)
    public void getTimeInMillisTest2() throws Exception{
        Sensor sensor = new Sensor();
        sensor.setId(100);
        sensor.setParam("PM10");
        sensor.setValue(321.2d);
        sensor.setLastDate("1970-01-01 0070:30");
        long calculatedTime = sensor.getTimeInMillis();
    }

    @Test
    public void getTimeInMillisTest3() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setId(100);
        sensor.setParam("PM10");
        sensor.setValue(321.2d);
        sensor.setLastDate("1970-01-01 00:00:33");
        long calculatedTime = sensor.getTimeInMillis();
        long expectedTime = 33000L;
        assertTrue(calculatedTime == expectedTime);
    }
}
