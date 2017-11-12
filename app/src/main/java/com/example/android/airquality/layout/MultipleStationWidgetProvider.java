package com.example.android.airquality.layout;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.airquality.R;
import com.example.android.airquality.main.SingleStationActivity;
import com.example.android.airquality.utility.MultipleStationWidgetUpdateService;


public class MultipleStationWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < appWidgetIds.length; ++i) {
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
        Log.v("LOG", "context.getPackageName() inside updateWidgetListView: " + context.getPackageName());

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

        if (intent.getAction().equals(WidgetConfigActivity.SHOW_REFRESH_BUTTON)){
            if (intent.hasExtra(WidgetConfigActivity.VISIBILITY_KEY)) {
                Log.v("LOG", "inside onReceive, intent.hasExtra(visibility)......." + intent.getBooleanExtra("visibility", true));
                boolean visibility = intent.getBooleanExtra(WidgetConfigActivity.VISIBILITY_KEY, true);


                RemoteViews remoteViews = new RemoteViews(
                        context.getPackageName(), R.layout.multiple_station_listview);
                if (visibility) {
                    remoteViews.setViewVisibility(R.id.multiple_station_refresh, View.VISIBLE);
                } else {
                    remoteViews.setViewVisibility(R.id.multiple_station_refresh, View.GONE);
                }

                AppWidgetManager appWidgetManager = AppWidgetManager
                        .getInstance(context);
                final ComponentName provider = new ComponentName(context, this.getClass());
                appWidgetManager.updateAppWidget(provider, remoteViews);
            }
        }
    }

    private void notifyAdapter(Context context, AppWidgetManager appWidgetManager) {
        ComponentName thisAppWidget = new ComponentName
                (context.getPackageName(), MultipleStationWidgetProvider.class.getName());
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetStationList);
    }
}
