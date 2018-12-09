package io.github.maksymilianrozanski.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.widget.model.MultipleStationWidgetModelImpl;
import xdroid.toaster.Toaster;

public class MultipleStationWidgetUpdateService extends Service
        implements
        MultipleStationWidgetContract.Model.OnFinishedListener {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private static final String LOG_TAG = MultipleStationWidgetUpdateService.class.getName();
    private static List<WidgetItem> widgetItemList = new ArrayList<>();

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

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        Log.d("Log", "Putting widget items into widgetUpdateIntent, size: " + widgetItemList.size());
        widgetUpdateIntent.putParcelableArrayListExtra(MultipleStationWidgetProvider.INTENT_KEY_PARCELABLE_ARRAY_LIST_EXTRA, (ArrayList<? extends Parcelable>) widgetItemList);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }

    @Override
    public void onFailure(@NotNull Throwable throwable) {
        Toaster.toast(R.string.no_internet_connection);
        this.stopSelf();
    }
}
