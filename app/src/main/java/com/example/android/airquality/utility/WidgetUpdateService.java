package com.example.android.airquality.utility;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.dataholders.StationList;
import com.example.android.airquality.layout.SingleStationWidget;

import java.io.IOException;

import xdroid.toaster.Toaster;

public class WidgetUpdateService extends IntentService {

    public static final String REQUESTED_STATION_ID = "imsg";
    public static final String OUTPUT_SENSOR = "omsg";
    public static final String OUTPUT_STATION_NAME = "outputStationName";
    private static final String LOG_TAG = WidgetUpdateService.class.getName();

    public WidgetUpdateService() {
        super(WidgetUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int idOfStation;
        try {
            idOfStation = intent.getIntExtra(REQUESTED_STATION_ID, 0);
        } catch (NullPointerException e) {
            idOfStation = 0;
            Log.e(LOG_TAG, "No IntExtra in intent" + e);
        }
        StationList stationList = StationList.getStationListInstance(getApplicationContext());

        Sensor sensor;
        try {
            sensor = stationList.findSensorWithHighestPercentValue(idOfStation);
        } catch (IOException e) {
            Toaster.toast(R.string.could_not_connect_to_server);
            return;
        }
        Intent intentSendBackToWidget = new Intent(this.getApplicationContext(), SingleStationWidget.class);
        intentSendBackToWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentSendBackToWidget.putExtra(OUTPUT_SENSOR, sensor);

        try {
            intentSendBackToWidget.putExtra(OUTPUT_STATION_NAME, stationList.findStationName(idOfStation));
        }catch (IOException e){
            Toaster.toast(R.string.error_occurred);
            Log.e(LOG_TAG, "IOException, couldn't find station name, station id = "
                    + idOfStation + "exception:" + e);
            return;
        }
        sendBroadcast(intentSendBackToWidget);
    }
}
