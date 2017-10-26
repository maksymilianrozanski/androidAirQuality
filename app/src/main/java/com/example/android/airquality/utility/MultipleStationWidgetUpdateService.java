package com.example.android.airquality.utility;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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
        widgetItemList = createWidgetItemListWithStationNames(5);
        fetchSensorDataForWidgetItems(widgetItemList);

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(MultipleStationWidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);
        this.stopSelf();
    }

    private void fetchSensorDataForWidgetItems(ArrayList<WidgetItem> widgetItemList) {
        FetchWidgetItem[] threads = new FetchWidgetItem[widgetItemList.size()];

        for (int i = 0; i < widgetItemList.size(); i++) {
            threads[i] = new FetchWidgetItem(i, getApplicationContext(), widgetItemList);
            threads[i].start();
        }

        for (FetchWidgetItem thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Exception: " + e);
            }
        }
    }

    private ArrayList<WidgetItem> createWidgetItemListWithStationNames(int numberOfStations) {
        ArrayList<WidgetItem> widgetItemList = new ArrayList<>();
        for (int i = 0; i < numberOfStations; i++) {
            WidgetItem widgetItem = new WidgetItem();
            widgetItem.setStationName(getStationName(i));
            widgetItemList.add(widgetItem);
        }
        return widgetItemList;
    }

    private String getStationName(int indexOnStationList) {
        List<Station> stationList = QueryStationsList.fetchStationDataFromSharedPreferences(getApplicationContext());
        return stationList.get(indexOnStationList).getName();
    }
}
