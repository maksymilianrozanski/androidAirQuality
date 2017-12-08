package com.example.android.airquality.layout;

import android.app.Activity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.utility.WidgetUpdateService;
import com.example.android.airquality.vieweditors.StationAdapter;
import com.example.android.airquality.vieweditors.StationLoader;

import java.util.ArrayList;
import java.util.List;

public class SingleStationWidgetConfigActivity extends Activity implements LoaderManager.LoaderCallbacks<List<Station>> {

    private static final String LOG_TAG = SingleStationWidgetConfigActivity.class.getName();
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int STATION_LOADER_ID = 1;
    public static final String SHARED_PREF_KEY_WIDGET = "com.example.android.airquality.singleStationWidget";

    private StationAdapter stationAdapter;
    LoaderManager loaderManager = getLoaderManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_station_widget_config_activity);

        ListView stationListView = (ListView) findViewById(R.id.station_list_widget_config);
        stationAdapter = new StationAdapter(this, new ArrayList<>());
        stationListView.setAdapter(stationAdapter);

        loaderManager.initLoader(STATION_LOADER_ID, null, this);

        assignAppWidgetId();

        stationListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Station station = stationAdapter.getItem(position);

            int currentStationId;
            try {
                currentStationId = Integer.parseInt(station.getId());
            } catch (NullPointerException e) {
                currentStationId = 0;
                Log.e(LOG_TAG, "Error when getting station.getId", e);
            }

            saveWidgetIdAndStationId(appWidgetId, currentStationId);

            Intent intent = new Intent();
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(Activity.RESULT_OK, intent);

            Intent intentSendToService = new Intent(getApplicationContext(), WidgetUpdateService.class);
            intentSendToService.putExtra(WidgetUpdateService.WIDGET_ID_TO_UPDATE, currentStationId);
            sendBroadcast(intentSendToService);

            this.finish();
        });
    }

    private void saveWidgetIdAndStationId(int appWidgetId, int stationId) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF_KEY_WIDGET, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WidgetUpdateService.APP_WIDGET_ID_KEY + String.valueOf(appWidgetId), stationId);
        editor.apply();
    }

    private void assignAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    @Override
    public Loader<List<Station>> onCreateLoader(int i, Bundle bundle) {
        return new StationLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Station>> loader, List<Station> data) {
        stationAdapter.clear();
        if (data != null && !data.isEmpty()) {
            stationAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Station>> loader) {
        stationAdapter.clear();
    }
}
