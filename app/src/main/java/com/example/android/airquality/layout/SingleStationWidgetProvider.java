package com.example.android.airquality.layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.utility.WidgetUpdateService;
import com.example.android.airquality.vieweditors.SensorAdapter;

public class SingleStationWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_station_widget);
        views.setTextViewText(R.id.widgetStationName, "Tap to refresh");

        Intent intentSendToService = new Intent(context, WidgetUpdateService.class);

        intentSendToService.putExtra(WidgetUpdateService.WIDGET_ID_TO_UPDATE, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intentSendToService, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.singleStationWidgetLayout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        SharedPreferences sharedPreferences = context.getSharedPreferences(SingleStationWidgetConfigActivity.SHARED_PREF_KEY_WIDGET, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int id : appWidgetIds) {
            editor.remove(String.valueOf(id));
        }
        editor.apply();
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            Sensor sensorFromIntent = intent.getParcelableExtra(WidgetUpdateService.OUTPUT_SENSOR);
            String stationName = intent.getStringExtra(WidgetUpdateService.OUTPUT_STATION_NAME);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_station_widget);
            if (sensorFromIntent != null) {
                views.setTextViewText(R.id.widgetStationName, stationName);
                String highestPercentValue = String.format("%.0f", sensorFromIntent.percentOfMaxValue());
                int colorOfValueBackground = SensorAdapter.chooseColorOfBackground(Double.parseDouble(highestPercentValue), context);
                views.setInt(R.id.widgetNameAndValueOfParam, "setBackgroundColor", colorOfValueBackground);
                views.setTextViewText(R.id.widgetNameAndValueOfParam, sensorFromIntent.getParam() + ": " + highestPercentValue + "%");
                views.setTextViewText(R.id.widgetUpdateDate, removeSecondsFromDate(sensorFromIntent.getLastDate()));
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            if (intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID) != null) {
                int widgetIdToUpdate = Integer.parseInt(intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID));
                appWidgetManager.updateAppWidget(widgetIdToUpdate, views);
            } else {
                ComponentName thisWidget = new ComponentName(context, SingleStationWidgetProvider.class);
                appWidgetManager.updateAppWidget(thisWidget, views);
            }
        }
    }

    private String removeSecondsFromDate(String notFormattedDate) {
        int notFormattedDateLength = notFormattedDate.length();
        return notFormattedDate.substring(0, notFormattedDateLength - 3);
    }
}
