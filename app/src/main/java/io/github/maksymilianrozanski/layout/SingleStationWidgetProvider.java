package io.github.maksymilianrozanski.layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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


public class SingleStationWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = SingleStationWidgetProvider.class.getName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.single_station_widget);
            setRefreshOnClick(view, context, appWidgetId);

            appWidgetManager.updateAppWidget(appWidgetId, view);

            sendRefreshIntent(context, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void sendRefreshIntent(Context context, int appWidgetId) {
        Intent refreshIntent = createRefreshIntent(context, appWidgetId);
        try {
            PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT).send();
        } catch (PendingIntent.CanceledException e) {
            Log.e(LOG_TAG, "exception canceledException: " + e);
        }
    }

    private Intent createRefreshIntent(Context context, int appWidgetId) {
        Intent refreshIntent = new Intent(context, SingleStationWidgetUpdateService.class);
        refreshIntent.putExtra(SingleStationWidgetUpdateService.APP_WIDGET_ID_TO_UPDATE, appWidgetId);
        refreshIntent.setData(Uri.parse("http://" + String.valueOf(appWidgetId)));  //setData is used to compare intents
        return refreshIntent;
    }

    private void setRefreshOnClick(RemoteViews view, Context context, int appWidgetId) {
        Intent refreshIntent = createRefreshIntent(context, appWidgetId);
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
                && intent.hasExtra(SingleStationWidgetUpdateService.OUTPUT_STATION_NAME)
                && intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            Sensor sensorFromIntent = intent.getParcelableExtra(SingleStationWidgetUpdateService.OUTPUT_SENSOR);
            String stationName = intent.getStringExtra(SingleStationWidgetUpdateService.OUTPUT_STATION_NAME);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_station_widget);
            int widgetIdFromIntent = Integer.parseInt(intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID));

            if (sensorFromIntent != null) {
                views.setTextViewText(R.id.widgetStationNameSingleStation, stationName);
                String highestPercentValue = String.format("%.0f", sensorFromIntent.percentOfMaxValue());
                int colorOfValueBackground = SensorAdapter.chooseColorOfBackground(Double.parseDouble(highestPercentValue), context);
                views.setInt(R.id.widgetNameAndValueOfParam, "setBackgroundColor", colorOfValueBackground);
                views.setTextViewText(R.id.widgetNameAndValueOfParam, sensorFromIntent.getParam() + ": " + highestPercentValue + "%");
                views.setTextViewText(R.id.widgetUpdateDate, removeSecondsFromDate(sensorFromIntent.getLastDate()));
                setRefreshOnClick(views, context, widgetIdFromIntent);
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.partiallyUpdateAppWidget(widgetIdFromIntent, views);
        }
        super.onReceive(context, intent);
    }

    private String removeSecondsFromDate(String notFormattedDate) {
        int notFormattedDateLength = notFormattedDate.length();
        return notFormattedDate.substring(0, notFormattedDateLength - 3);
    }
}

