package com.example.android.airquality.layout;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.airquality.R;
import com.example.android.airquality.main.SingleStationActivity;
import com.example.android.airquality.utility.MultipleStationWidgetUpdateService;


public class MultipleStationWidgetProvider extends AppWidgetProvider {


    private static final String SHARED_PREFERENCES_VISIBILITY_KEY = "com.example.android.airquality.refreshButtonVisibilities";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Log.v("LOG", "inside onUpdate");
            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);

            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);
            sendIntentToUpdatingService(context, appWidgetIds[i]);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void sendIntentToUpdatingService(Context context, int appWidgetId) {
        Intent refreshIntent = new Intent(context, MultipleStationWidgetUpdateService.class);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, refreshIntent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e("Log", "exception canceledException: " + e);
        }
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(), R.layout.multiple_station_listview);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, ScrollableWidgetService.class);
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(R.id.widgetStationList,
                svcIntent);

        Intent clickIntentTemplate = new Intent(context, SingleStationActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate).
                        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widgetStationList, clickPendingIntentTemplate);

        remoteViews.setEmptyView(R.id.widgetStationList, R.id.empty_view);

        setRefreshButton(context, appWidgetId, remoteViews);

        return remoteViews;
    }

    private void setRefreshButton(Context context, int appWidgetId, RemoteViews remoteViews) {
        Intent refreshIntent = new Intent(context, MultipleStationWidgetUpdateService.class);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, refreshIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.multiple_station_refresh, pendingIntent);
        boolean visibility = readRefreshButtonVisibilityFromSharedPref(appWidgetId, context);
        setRefreshButtonVisibility(visibility, remoteViews);
    }

    private boolean readRefreshButtonVisibilityFromSharedPref(int appWidgetId, Context context) {
        SharedPreferences keyValues = context.getSharedPreferences(SHARED_PREFERENCES_VISIBILITY_KEY, Context.MODE_PRIVATE);
        return keyValues.getBoolean(String.valueOf(appWidgetId), true);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences keyValues = context.getSharedPreferences(SHARED_PREFERENCES_VISIBILITY_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor keyValuesEditor = keyValues.edit();
        for (int id : appWidgetIds) {
            keyValuesEditor.remove(String.valueOf(id));
        }
        keyValuesEditor.apply();
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        onReceiveUpdateIntent(context, intent);
        onReceiveRefreshButtonVisibilityIntent(context, intent);
    }

    private void onReceiveUpdateIntent(Context context, Intent intent) {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);

            notifyAdapter(context, appWidgetManager);

            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private void notifyAdapter(Context context, AppWidgetManager appWidgetManager) {
        ComponentName thisAppWidget = new ComponentName
                (context.getPackageName(), MultipleStationWidgetProvider.class.getName());
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetStationList);
    }

    private void onReceiveRefreshButtonVisibilityIntent(Context context, Intent intent) {
        if (intent.getAction().equals(WidgetConfigActivity.SHOW_REFRESH_BUTTON)) {
            if (intent.hasExtra(WidgetConfigActivity.VISIBILITY_KEY) && intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                boolean visibility = intent.getBooleanExtra(WidgetConfigActivity.VISIBILITY_KEY, true);
                int appWidgetId = intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

                RemoteViews remoteViews = new RemoteViews(
                        context.getPackageName(), R.layout.multiple_station_listview);
                setRefreshButtonVisibility(visibility, remoteViews);
                saveRefreshButtonVisibilityToSharedPref(visibility, appWidgetId, context);

                AppWidgetManager appWidgetManager = AppWidgetManager
                        .getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
    }

    private void saveRefreshButtonVisibilityToSharedPref(boolean visible, int appWidgetId, Context context) {
        SharedPreferences keyValues = context.getSharedPreferences(SHARED_PREFERENCES_VISIBILITY_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor keyValuesEditor = keyValues.edit();
        keyValuesEditor.putBoolean(String.valueOf(appWidgetId), visible);
        keyValuesEditor.apply();
    }

    private void setRefreshButtonVisibility(boolean visible, RemoteViews remoteViews) {
        if (visible) {
            remoteViews.setViewVisibility(R.id.multiple_station_refresh, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.multiple_station_refresh, View.GONE);
        }
    }
}
