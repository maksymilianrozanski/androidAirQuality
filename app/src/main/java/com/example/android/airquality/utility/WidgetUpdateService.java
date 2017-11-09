package com.example.android.airquality.utility;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.dataholders.StationList;
import com.example.android.airquality.layout.SingleStationWidget;

import java.io.IOException;
import java.util.List;

import xdroid.toaster.Toaster;

public class WidgetUpdateService extends IntentService {

    public static final String REQUESTED_STATION_INDEX = "imsg";
    public static final String OUTPUT_SENSOR = "omsg";
    public static final String OUTPUT_STATION_NAME = "outputStationName";
    private static final String LOG_TAG = WidgetUpdateService.class.getName();

    public WidgetUpdateService() {
        super(WidgetUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int requestedStationIndex;
        try {
            requestedStationIndex = intent.getIntExtra(REQUESTED_STATION_INDEX, 0);
        } catch (NullPointerException e) {
            requestedStationIndex = 0;
            Log.e(LOG_TAG, "No IntExtra in intent" + e);
        }
        Sensor sensor;
        try {
            sensor = fetchSensorWithHighestPercentValue(requestedStationIndex);
        } catch (IOException e) {
            Toaster.toast(R.string.could_not_connect_to_server);
            return;
        }
        Intent intentSendBackToWidget = new Intent(this.getApplicationContext(), SingleStationWidget.class);
        intentSendBackToWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentSendBackToWidget.putExtra(OUTPUT_SENSOR, sensor);
        intentSendBackToWidget.putExtra(OUTPUT_STATION_NAME, getStationName(requestedStationIndex));
        sendBroadcast(intentSendBackToWidget);
    }

    private String getStationName(int indexOnStationList) {
        return StationList.getStationListInstance(getApplicationContext()).getStation(indexOnStationList).getName();
    }

    private Sensor fetchSensorWithHighestPercentValue(int stationIndex) throws IOException {
        List<Station> stationList = StationList.getStationListInstance(getApplicationContext()).getStations();
        Station station = stationList.get(stationIndex);
        List<Sensor> sensors = QueryStationSensors.fetchSensorData(Integer.parseInt(station.getId()));
        return getSensorWithHighestValue(sensors);
    }

    private Sensor getSensorWithHighestValue(List<Sensor> sensors) {
        if (sensors.size() == 1) return sensors.get(0);
        double highestValue = Double.MIN_VALUE;
        Sensor sensorHighestCalculatedValue = sensors.get(0);
        for (int i = 1; i < sensors.size(); i++) {
            double calculatedValue = sensors.get(i).percentOfMaxValue();
            if (calculatedValue > highestValue) {
                highestValue = calculatedValue;
                sensorHighestCalculatedValue = sensors.get(i);
            }
        }
        return sensorHighestCalculatedValue;
    }
}
