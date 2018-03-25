package io.github.maksymilianrozanski.layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Sensor;
import io.github.maksymilianrozanski.utility.SingleStationWidgetUpdateService;
import io.github.maksymilianrozanski.vieweditors.SensorAdapter;
import xdroid.toaster.Toaster;


public class SingleStationWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.single_station_widget);
            setRefreshOnClick(view, context, appWidgetIds[i]);

            appWidgetManager.updateAppWidget(appWidgetIds[i], view);

            try {
                createPendingRefreshIntent(context, appWidgetIds[i]).send();
            } catch (PendingIntent.CanceledException e) {
                Log.e("Log", "exception canceledException: " + e);
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private PendingIntent createPendingRefreshIntent(Context context, int appWidgetId) {
        Log.d("Log", "createPendingRefreshIntent called, appWidgetId");
        Intent refreshIntent = new Intent(context, SingleStationWidgetUpdateService.class);
        refreshIntent.putExtra(SingleStationWidgetUpdateService.APP_WIDGET_ID_TO_UPDATE, appWidgetId);
        return PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setRefreshOnClick(RemoteViews view, Context context, int appWidgetId) {
        Intent refreshIntent = new Intent(context, SingleStationWidgetUpdateService.class);
        refreshIntent.putExtra(SingleStationWidgetUpdateService.APP_WIDGET_ID_TO_UPDATE, appWidgetId);
        refreshIntent.setData(Uri.parse("http://" + String.valueOf(appWidgetId)));
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.singleStationWidgetLayout, pendingIntent);
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
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())
                && intent.hasExtra(SingleStationWidgetUpdateService.OUTPUT_SENSOR)
                && intent.hasExtra(SingleStationWidgetUpdateService.OUTPUT_STATION_NAME)) {
            Sensor sensorFromIntent = intent.getParcelableExtra(SingleStationWidgetUpdateService.OUTPUT_SENSOR);
            String stationName = intent.getStringExtra(SingleStationWidgetUpdateService.OUTPUT_STATION_NAME);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_station_widget);
            if (sensorFromIntent != null) {
                views.setTextViewText(R.id.widgetStationNameSingleStation, stationName);
                String highestPercentValue = String.format("%.0f", sensorFromIntent.percentOfMaxValue());
                int colorOfValueBackground = SensorAdapter.chooseColorOfBackground(Double.parseDouble(highestPercentValue), context);
                views.setInt(R.id.widgetNameAndValueOfParam, "setBackgroundColor", colorOfValueBackground);
                views.setTextViewText(R.id.widgetNameAndValueOfParam, sensorFromIntent.getParam() + ": " + highestPercentValue + "%");
                views.setTextViewText(R.id.widgetUpdateDate, removeSecondsFromDate(sensorFromIntent.getLastDate()));

                int widgetId = 0;
                if (intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID) != null) {
                    widgetId = Integer.parseInt(intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID));
                }
                setRefreshOnClick(views, context, widgetId);

            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            if (intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID) != null) {
                int widgetIdToUpdate = Integer.parseInt(intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID));
                Toaster.toast("received update of widget with id: " + widgetIdToUpdate);
                Log.d("Log", "received update of widget with id: " + widgetIdToUpdate);
                appWidgetManager.partiallyUpdateAppWidget(widgetIdToUpdate, views);
            } else {
                Log.d("Log", "inside else, before ComponentName thisWidget...");
                ComponentName thisWidget = new ComponentName(context, SingleStationWidgetProvider.class);
                appWidgetManager.updateAppWidget(thisWidget, views);
            }
        }
        super.onReceive(context, intent);
    }

    private String removeSecondsFromDate(String notFormattedDate) {
        int notFormattedDateLength = notFormattedDate.length();
        return notFormattedDate.substring(0, notFormattedDateLength - 3);
    }
}

