package com.example.android.airquality.layout;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void setPendingIntentToRequestNewData(Context context, int appWidgetId, RemoteViews remoteViews){
        Intent refreshIntent = new Intent(context, MultipleStationWidgetUpdateService.class);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, refreshIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.multiple_station_refresh, pendingIntent);
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

        setPendingIntentToRequestNewData(context, appWidgetId, remoteViews);

        return remoteViews;
    }


    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}

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
    }

    private void notifyAdapter(Context context, AppWidgetManager appWidgetManager) {
        ComponentName thisAppWidget = new ComponentName
                (context.getPackageName(), MultipleStationWidgetProvider.class.getName());
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetStationList);
    }
}
