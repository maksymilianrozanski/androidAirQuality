package com.example.android.airquality.utility;

import android.content.Context;
import android.util.Log;

import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.dataholders.StationList;
import com.example.android.airquality.layout.WidgetItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FetchWidgetItem extends Thread {

    private static final String LOG_TAG = FetchWidgetItem.class.getSimpleName();
    private int indexNumber;
    private Context context;
    private ArrayList<WidgetItem> widgetItems;

    FetchWidgetItem(int indexNumber, Context context, ArrayList<WidgetItem> widgetItems) {
        this.indexNumber = indexNumber;
        this.context = context;
        this.widgetItems = widgetItems;
    }

    @Override
    public void run() {
        Sensor tempSensor;
        WidgetItem currentWidgetItem;
        try {
            tempSensor = fetchSensorWithHighestPercentValue(context, indexNumber);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fetching data not succeed.");
            return;
        }
        currentWidgetItem = widgetItems.get(indexNumber);
        String paramType = tempSensor.getParam();
        String percentValue = String.format("%.0f", tempSensor.percentOfMaxValue());
        currentWidgetItem.setNameAndValueOfParam(paramType + ": " + percentValue + "%");
        currentWidgetItem.setUpdateDate(removeSecondsFromDate(tempSensor.getLastDate()));
    }

    private Sensor fetchSensorWithHighestPercentValue(Context context, int stationIndex) throws IOException {
        List<Station> stationList = StationList.getStationListInstance(context).getStations();
        Station station = stationList.get(stationIndex);
        List<Sensor> sensors = QueryStationSensors.fetchSensorData(Integer.parseInt(station.getId()));
        return getSensorWithHighestValue(sensors);
    }

    //TODO: fix if station is invalid/has no sensors
    private Sensor getSensorWithHighestValue(List<Sensor> sensors) {
        if (sensors.size() == 1) return sensors.get(0);
        double highestValue = sensors.get(0).percentOfMaxValue();
        Sensor sensorHighestCalculatedValue = sensors.get(0);
        for (int i = 1; i < sensors.size(); i++) {
            double calculatedValue;
            try {
                calculatedValue = sensors.get(i).percentOfMaxValue();
            } catch (NullPointerException e) {
                calculatedValue = Double.MIN_VALUE;
            }
            if (calculatedValue > highestValue) {
                highestValue = calculatedValue;
                sensorHighestCalculatedValue = sensors.get(i);
            }
        }
        return sensorHighestCalculatedValue;
    }

    private String removeSecondsFromDate(String notFormattedDate) {
        int notFormattedDateLength = notFormattedDate.length();
        return notFormattedDate.substring(0, notFormattedDateLength - 3);
    }
}
