package com.example.android.airquality.layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.utility.WidgetUpdateService;
import com.example.android.airquality.vieweditors.SensorAdapter;

/**
 * Implementation of App Widget functionality.
 */
public class SingleStationWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_station_widget);
        views.setTextViewText(R.id.widgetStationName, "Text set from updateAppWidget");

        Intent intentSendToService = new Intent(context, WidgetUpdateService.class);

        intentSendToService.putExtra(WidgetUpdateService.PARAM_IN_MSG, "Example text from widget");
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intentSendToService, 0);
        views.setOnClickPendingIntent(R.id.singleStationWidgetLayout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v("LOG", "Inside onUpdate....");

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            Sensor sensorFromIntent = intent.getParcelableExtra(WidgetUpdateService.OUTPUT_SENSOR);
            String stationName = intent.getStringExtra(WidgetUpdateService.OUTPUT_STATION_NAME);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_station_widget);
            if (sensorFromIntent != null) {
                views.setTextViewText(R.id.widgetStationName, stationName);
                String highestPercentValue = String.format("%.0f", SensorAdapter.percentOfMaxValue(sensorFromIntent));
                views.setTextViewText(R.id.widgetNameAndValueOfParam, sensorFromIntent.getParam() + ": " + highestPercentValue + "%");
                views.setTextViewText(R.id.widgetUpdateDate, sensorFromIntent.getLastDate());
            }
            ComponentName thisWidget = new ComponentName(context, SingleStationWidget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(thisWidget, views);
        }
        super.onReceive(context, intent);
    }
}

