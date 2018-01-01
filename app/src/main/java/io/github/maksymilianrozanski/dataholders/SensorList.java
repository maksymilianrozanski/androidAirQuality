package io.github.maksymilianrozanski.dataholders;

import android.support.annotation.VisibleForTesting;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SensorList {

    private List<Sensor> sensors;
    @VisibleForTesting
    Calendar calendar;
    private static final String LOG_TAG = SensorList.class.getName();

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

    public Sensor getSensorWithHighestValue() throws IOException {
        if (sensors.size() == 0) throw new IOException(LOG_TAG + "sensor list is empty");
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
