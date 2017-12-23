package com.example.android.airquality.utility;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.dataholders.SensorList;
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

    @VisibleForTesting
    public static int timeInHours = 5;

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
        int stationId = widgetItems.get(stationIndex).getStationId();
        StationList stationList = StationList.getStationListInstance(context);
        Station station = stationList.findStationWithId(stationId);
        QueryStationSensors queryStationSensors = new QueryStationSensors();
        List<Sensor> sensors = queryStationSensors.fetchSensorData(Integer.parseInt(station.getId()));
        SensorList sensorList = new SensorList(sensors);
        sensorList.removeSensorsWhereValueOlderThan(timeInHours);
        return sensorList.getSensorWithHighestValue();
    }

    private String removeSecondsFromDate(String notFormattedDate) {
        int notFormattedDateLength = notFormattedDate.length();
        return notFormattedDate.substring(0, notFormattedDateLength - 3);
    }
}
