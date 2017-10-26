package com.example.android.airquality.utility;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.layout.MultipleStationWidgetProvider;
import com.example.android.airquality.layout.WidgetItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 23.09.2017.
 */

public class MultipleStationWidgetUpdateService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private static final String LOG_TAG = MultipleStationWidgetUpdateService.class.getName();
    private static ArrayList<WidgetItem> widgetItemList;

    public static ArrayList<WidgetItem> getWidgetItemList() {
        return widgetItemList;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        fetchDataFromWeb();
        return super.onStartCommand(intent, flags, startId);
    }

    private void fetchDataFromWeb() {

        long millisStart = System.currentTimeMillis();
        widgetItemList = new ArrayList<WidgetItem>();
        for (int i = 0; i < 5; i++ ){
            WidgetItem widgetItem = new WidgetItem();
            widgetItem.setStationName(getStationName(i));
            widgetItemList.add(widgetItem);
        }

        FetchWidgetItem[] threads = new FetchWidgetItem[widgetItemList.size()];

        for (int i = 0 ; i < widgetItemList.size(); i++){
            threads[i] = new FetchWidgetItem(i, getApplicationContext(), widgetItemList);
            threads[i].start();
        }


        for(int i = 0; i < threads.length; i++){
            try {
                threads[i].join();
            }catch (InterruptedException e){
                Log.e(LOG_TAG, "Exception: " + e);
            }
        }

        long millisEnd = System.currentTimeMillis();
        long operationTime = millisEnd - millisStart;
        Log.v(LOG_TAG, "Time of operation in ms: " + String.valueOf(operationTime));

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(MultipleStationWidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);
        this.stopSelf();
    }

    private String removeSecondsFromDate(String notFormattedDate) {
        int notFormattedDateLength = notFormattedDate.length();
        return notFormattedDate.substring(0, notFormattedDateLength - 3);
    }

    private String getStationName(int indexOnStationList){
        List<Station> stationList = QueryStationsList.fetchStationDataFromSharedPreferences(getApplicationContext());
        return stationList.get(indexOnStationList).getName();
    }

    private Sensor fetchSensorWithHighestPercentValue(Context context, int stationIndex) {
        List<Station> stationList = QueryStationsList.fetchStationDataFromSharedPreferences(context);
        Station station = stationList.get(stationIndex);
        List<Sensor> sensors = QueryStationSensors.fetchSensorData(Integer.parseInt(station.getId()), context);
        return getSensorWithHighestValue(sensors);
    }

    private Sensor getSensorWithHighestValue(List<Sensor> sensors){
        if (sensors.size() == 1) return sensors.get(0);
        double highestValue = Double.MIN_VALUE;
        Sensor sensorHighestCalculatedValue = sensors.get(0);
        for (int i = 1 ; i < sensors.size(); i++){
            double calculatedValue = sensors.get(i).percentOfMaxValue();
            if (calculatedValue > highestValue){
                highestValue = calculatedValue;
                sensorHighestCalculatedValue = sensors.get(i);
            }
        }
        return sensorHighestCalculatedValue;
    }
}
