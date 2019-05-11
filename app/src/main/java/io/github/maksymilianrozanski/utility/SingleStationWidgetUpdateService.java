package io.github.maksymilianrozanski.utility;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Sensor;
import io.github.maksymilianrozanski.dataholders.SensorList;
import io.github.maksymilianrozanski.dataholders.StationList;
import io.github.maksymilianrozanski.layout.SingleStationWidgetConfigActivity;
import io.github.maksymilianrozanski.layout.SingleStationWidgetProvider;
import xdroid.toaster.Toaster;

public class SingleStationWidgetUpdateService extends IntentService {

    public static final String APP_WIDGET_ID_TO_UPDATE = "incomingMessage";
    public static final String OUTPUT_SENSOR = "omsg";
    public static final String OUTPUT_STATION_NAME = "outputStationName";
    public static final String APP_WIDGET_ID_KEY = "widgetData:";
    private static final String LOG_TAG = SingleStationWidgetUpdateService.class.getName();

    public SingleStationWidgetUpdateService() {
        super(SingleStationWidgetUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int idOfStation;
        int appWidgetId;
        try {
            appWidgetId = intent.getIntExtra(SingleStationWidgetUpdateService.APP_WIDGET_ID_TO_UPDATE, 0);
            Log.d(LOG_TAG, "inside onHandleIntent, requested update of widget id: " + appWidgetId);
            idOfStation = getStationIdFromSharedPref(appWidgetId);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "No IntExtra in intent" + e);
            return;
        }
        StationList stationList = StationList.getStationListInstance(getApplicationContext());

        Sensor sensor;
        try {
            sensor = findSensorWithHighestPercentValue(idOfStation, 5);
        } catch (IOException e) {
            Toaster.toast(R.string.could_not_connect_to_server);
            return;
        }
        Intent intentSendBackToWidget = new Intent(this.getApplicationContext(), SingleStationWidgetProvider.class);
        intentSendBackToWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentSendBackToWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, String.valueOf(appWidgetId));
        intentSendBackToWidget.putExtra(OUTPUT_SENSOR, sensor);

        try {
            intentSendBackToWidget.putExtra(OUTPUT_STATION_NAME, stationList.findStationName(idOfStation));
        } catch (IOException e) {
            Toaster.toast(R.string.error_occurred);
            Log.e(LOG_TAG, "IOException, couldn't find station name, station id = "
                    + idOfStation + "exception:" + e);
            return;
        }
        sendBroadcast(intentSendBackToWidget);
    }

    private Sensor findSensorWithHighestPercentValue(int stationId, int ignoreOlderThanHours) throws IOException {
        QueryStationSensors queryStationSensors = new QueryStationSensors();
        List<Sensor> sensors = queryStationSensors.fetchSensorData(stationId);
        SensorList sensorList = new SensorList(sensors);
        sensorList.removeSensorsWhereValueOlderThan(ignoreOlderThanHours);
        return sensorList.getSensorWithHighestValue();
    }

    private int getStationIdFromSharedPref(int appWidgetId) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SingleStationWidgetConfigActivity.SHARED_PREF_KEY_WIDGET, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(APP_WIDGET_ID_KEY + appWidgetId, 0);
    }
}
