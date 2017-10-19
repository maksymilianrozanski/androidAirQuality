package com.example.android.airquality.utility;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.layout.SingleStationWidget;
import com.example.android.airquality.vieweditors.SensorAdapter;

import java.util.List;

/**
 * Created by Max on 23.09.2017.
 */

public class WidgetUpdateService extends IntentService {

    public static final String PARAM_IN_MSG = "imsg";
    public static final String OUTPUT_SENSOR = "omsg";
    public static final String OUTPUT_STATION_NAME = "outputStationName";

    public WidgetUpdateService() {
        super(WidgetUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msg = intent.getStringExtra(PARAM_IN_MSG);

        Sensor sensor = fetchSensorWithHighestPercentValue(getApplicationContext());

        Intent intentSendBackToWidget = new Intent(this.getApplicationContext(), SingleStationWidget.class);
        intentSendBackToWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentSendBackToWidget.putExtra(OUTPUT_SENSOR, sensor);
        intentSendBackToWidget.putExtra(OUTPUT_STATION_NAME, getStationNameIndex0());
        sendBroadcast(intentSendBackToWidget);
    }

    private String getStationNameIndex0(){
        List<Station> stationList = QueryStationsList.fetchStationDataFromSharedPreferences(getApplicationContext());
        return stationList.get(0).getName();
    }

    private Sensor fetchSensorWithHighestPercentValue(Context context) {
        List<Station> stationList = QueryStationsList.fetchStationDataFromSharedPreferences(context);
        Station station = stationList.get(0);
        List<Sensor> sensors = QueryStationSensors.fetchSensorData(Integer.parseInt(station.getId()), context);
        return getSensorWithHighestValue(sensors);
    }

    private Sensor getSensorWithHighestValue(List<Sensor> sensors){
        if (sensors.size() == 1) return sensors.get(0);
        double highestValue = Double.MIN_VALUE;
        Sensor sensorHighestCalculatedValue = sensors.get(0);
        for (int i = 1 ; i < sensors.size(); i++){
            double calculatedValue = SensorAdapter.percentOfMaxValue(sensors.get(i));
            if (calculatedValue > highestValue){
                highestValue = calculatedValue;
                sensorHighestCalculatedValue = sensors.get(i);
            }
        }
        return sensorHighestCalculatedValue;
    }
}
