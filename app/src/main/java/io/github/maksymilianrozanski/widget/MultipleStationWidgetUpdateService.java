package io.github.maksymilianrozanski.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.widget.model.MultipleStationWidgetModelImpl;
import xdroid.toaster.Toaster;

public class MultipleStationWidgetUpdateService extends Service
        implements
        MultipleStationWidgetContract.Model.OnFinishedListener {

    public static final String LIST_TAG = "io.github.maksymilianrozanski.widgetItemList";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private static final String LOG_TAG = MultipleStationWidgetUpdateService.class.getName();
    private static List<WidgetItem> widgetItemList = new ArrayList<>();

    public static List<WidgetItem> getWidgetItemListFromSharedPreferences(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(LIST_TAG, null);
        Type listType = new TypeToken<ArrayList<WidgetItem>>() {
        }.getType();
        return widgetItemList = gson.fromJson(json, listType);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLocationProvider locationProvider = new MyLocationProviderImpl(this);
        ConnectionCheck connectionCheck = new ConnectionCheckImpl(this);
        MultipleStationWidgetContract.Model model = new MultipleStationWidgetModelImpl(this, locationProvider, connectionCheck);

        if (intent != null) {
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                appWidgetId = intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
            }
        }

        model.fetchData(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onFinished(@NotNull List<WidgetItem> stations) {
        widgetItemList = stations;

        saveWidgetItemList();
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }

    @Override
    public void onFailure(@NotNull Throwable throwable) {
        Toaster.toast(R.string.no_internet_connection);
        this.stopSelf();
    }

    private void saveWidgetItemList() {
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
