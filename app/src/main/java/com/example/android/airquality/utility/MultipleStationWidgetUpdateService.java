package com.example.android.airquality.utility;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.layout.WidgetItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import xdroid.toaster.Toaster;

/**
 * Created by Max on 23.09.2017.
 */

public class MultipleStationWidgetUpdateService extends Service {

    public static final String LIST_TAG = "com.example.android.airquality.widgetItemList";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private static final String LOG_TAG = MultipleStationWidgetUpdateService.class.getName();
    private static ArrayList<WidgetItem> widgetItemList = new ArrayList<>();

    public static ArrayList<WidgetItem> getWidgetItemListFromSharedPreferences(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(LIST_TAG, null);
        Type listType = new TypeToken<ArrayList<WidgetItem>>() {}.getType();
        return widgetItemList = gson.fromJson(json, listType);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toaster.toast("Fetching data...");
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        QueryStationsList.sortStationsByDistance(getApplicationContext());
        fetchDataFromWeb();
        return super.onStartCommand(intent, flags, startId);
    }

    private void fetchDataFromWeb() {
        widgetItemList = createWidgetItemListWithStationNames(5);
        fetchSensorDataForWidgetItems(widgetItemList);
        saveWidgetItemList();
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
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
            widgetItem.setStationId(Integer.parseInt(getStation(i).getId()));
            widgetItem.setStationName(getStation(i).getName());
            widgetItemList.add(widgetItem);
        }
        return widgetItemList;
    }

    private Station getStation(int indexOnStationList) {
        List<Station> stationList = QueryStationsList
                .fetchStationDataFromSharedPreferences(getApplicationContext());
        return stationList.get(indexOnStationList);
    }

    private void saveWidgetItemList(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String widgetItemsAsJsonString = gson.toJson(widgetItemList);
        editor.remove(LIST_TAG);
        editor.apply();
        editor.putString(LIST_TAG, widgetItemsAsJsonString);
        editor.apply();
    }
}
