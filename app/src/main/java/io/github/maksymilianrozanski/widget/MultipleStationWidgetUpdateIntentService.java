package io.github.maksymilianrozanski.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.widget.model.MultipleStationWidgetModelImpl;
import xdroid.toaster.Toaster;

public class MultipleStationWidgetUpdateIntentService extends IntentService
        implements MultipleStationWidgetContract.Model.OnFinishedListener {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public MultipleStationWidgetUpdateIntentService() {
        super("MultipleStationWidgetUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyLocationProvider locationProvider = new MyLocationProviderImpl(this);
        ConnectionCheck connectionCheck = new ConnectionCheckImpl(this);
        MultipleStationWidgetContract.Model model = new MultipleStationWidgetModelImpl(this, locationProvider, connectionCheck);
        Log.d("Log", "Inside onHandleIntent of intent service");
        if (intent != null) {
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                appWidgetId = intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                Log.d("Log", "set appWidgetId: " + appWidgetId);
            }
        }

        model.fetchData(this);
    }

    @Override
    public void onFinished(@NotNull List<WidgetItem> stations) {
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        Log.d("Log", "Putting widget items into widgetUpdateIntent, size: " + stations.size());
        widgetUpdateIntent.putParcelableArrayListExtra(MultipleStationWidgetProvider.INTENT_KEY_PARCELABLE_ARRAY_LIST_EXTRA, (ArrayList<? extends Parcelable>) stations);
        sendBroadcast(widgetUpdateIntent);
    }

    @Override
    public void onFailure(@NotNull Throwable throwable) {
        if (throwable.getMessage().equals(ThrowableMessagesKt.no_internet_connection_exception)) {
            Toaster.toast(R.string.no_internet_connection);
        } else if (throwable.getMessage().equals(ThrowableMessagesKt.access_to_location_not_granted)) {
            Toaster.toast(R.string.no_location_access);
        }
    }
}
