package com.example.android.airquality.utility;

import android.Manifest;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.dataholders.StationList;
import com.example.android.airquality.layout.WidgetItem;
import com.example.android.airquality.main.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import xdroid.toaster.Toaster;

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
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    try {
                        StationList.getStationListInstance(getApplicationContext())
                                .sortStationsByDistance(getApplicationContext(), location);
                    } catch (NullPointerException e) {
                        Toaster.toast(R.string.no_location_access);
                    }
                    fetchDataFromWeb();
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void fetchDataFromWeb() {
        if (MainActivity.isConnected(getApplicationContext())) {
            widgetItemList = createWidgetItemListWithStationNames(5);
            fetchSensorDataForWidgetItems(widgetItemList);
            saveWidgetItemList();
            Intent widgetUpdateIntent = new Intent();
            widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            sendBroadcast(widgetUpdateIntent);
        } else {
            Toaster.toast(R.string.no_internet_connection);
        }
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
            Station currentStation = StationList.getStationListInstance(getApplicationContext()).getStation(i);
            widgetItem.setStationId(Integer.parseInt(currentStation.getId()));
            widgetItem.setStationName(currentStation.getName());
            widgetItemList.add(widgetItem);
        }
        return widgetItemList;
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
