package com.example.android.airquality.dataholders;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class SensorTestInstrumented {

    @Test
    public void parcelableImplementationTest(){

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

}
