package io.github.maksymilianrozanski.dataholders;

import android.os.Parcel;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;


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

        assertEquals(sensor.getId(), sensorCreatedFromParcel.getId());
        assertEquals(sensor.getParam(), sensorCreatedFromParcel.getParam());
        assertEquals(sensor.getValue(), sensorCreatedFromParcel.getValue(), 0.0);
        assertEquals(sensor.getLastDate(), sensorCreatedFromParcel.getLastDate());
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
        assertEquals(calculatedTime, expectedTime);
    }

    @Test(expected = ParseException.class)
    public void getTimeInMillisTest2() throws Exception {
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
        assertEquals(expectedTime, calculatedTime);
    }

    @Test
    public void getTimeInMillisTest4() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setId(100);
        sensor.setParam("PM10");
        sensor.setValue(321.2d);
        sensor.setLastDate("1970-01-01 00:00:00");
        long calculatedTime = sensor.getTimeInMillis();
        long expectedTime = 0L;
        assertEquals(expectedTime, calculatedTime);
    }
}
