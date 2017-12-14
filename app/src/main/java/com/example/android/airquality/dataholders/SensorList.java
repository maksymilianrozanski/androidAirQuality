package com.example.android.airquality.dataholders;

import android.support.annotation.VisibleForTesting;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SensorList {

    private List<Sensor> sensors;
    @VisibleForTesting
    public Calendar calendar;

    public SensorList(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public List<Sensor> getList() {
        return sensors;
    }

    public void removeSensorsWhereValueOlderThan(int timeInHours) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }

        for (int i = 0; i < sensors.size(); ) {
            long oldestAcceptableTime = (calendar.getTimeInMillis() - (timeInHours * 3600000));
            long sensorTime;
            try {
                sensorTime = sensors.get(i).getTimeInMillis();
            } catch (ParseException e) {
                sensors.remove(i);
                continue;
            }
            if (sensorTime < oldestAcceptableTime) {
                sensors.remove(i);
            } else {
                i = i + 1;
            }
        }
    }

    //    //TODO: fix if station is invalid/has no sensors
    public Sensor getSensorWithHighestValue() {
        if (sensors.size() == 1) return sensors.get(0);
        double highestValue = sensors.get(0).percentOfMaxValue();
        Sensor sensorHighestCalculatedValue = sensors.get(0);
        for (int i = 1; i < sensors.size(); i++) {
            double calculatedValue;
            try {
                calculatedValue = sensors.get(i).percentOfMaxValue();
            } catch (NullPointerException e) {
                continue;
            }
            if (calculatedValue > highestValue) {
                highestValue = calculatedValue;
                sensorHighestCalculatedValue = sensors.get(i);
            }
        }
        return sensorHighestCalculatedValue;
    }
}