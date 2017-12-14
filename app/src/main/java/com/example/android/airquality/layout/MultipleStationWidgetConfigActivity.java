package com.example.android.airquality.layout;

import android.app.Activity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.utility.MultipleStationWidgetUpdateService;
import com.example.android.airquality.utility.NearestStationFinder;
import com.example.android.airquality.vieweditors.StationLoader;

import java.util.List;

public class MultipleStationWidgetConfigActivity extends Activity implements OnClickListener, LoaderManager.LoaderCallbacks<List<Station>> {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int MY_PERMISSION_REQUEST = 0;
    private static final int STATION_LOADER_ID = 1;
    public static final String SHOW_REFRESH_BUTTON = "SHOW_REFRESH_BUTTON";
    public static final String VISIBILITY_KEY = "VISIBILITY_KEY";
    LoaderManager loaderManager = getLoaderManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loaderManager.initLoader(STATION_LOADER_ID, null, this);

        setContentView(R.layout.multiple_station_widget_config_activity);

        assignAppWidgetId();

        Button requestPermissionButton = (Button) findViewById(R.id.requestPermissionButton);
        requestPermissionButton.setOnClickListener(this);

        Button startButton = (Button) findViewById(R.id.widgetStartButton);
        startButton.setOnClickListener(this);
    }

    /**
     * Widget configuration activity,always receives appwidget Id appWidget Id =
     * unique id that identifies your widget analogy : same as setting view id
     * via @+id/viewname on layout but appwidget id is assigned by the system
     * itself
     */
    private void assignAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.widgetStartButton:
                startWidget();
                break;
            case R.id.requestPermissionButton:
                NearestStationFinder.askForLocationPermissionIfNoPermission(this, MY_PERMISSION_REQUEST);
                break;
        }
    }

    /**
     * This method right now displays the widget and starts a Service to fetch
     * remote data from Server
     */
    private void startWidget() {
        // this intent is essential to show the widget
        // if this intent is not included,you can't show
        // widget on homescreen
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(Activity.RESULT_OK, intent);

        sendIntentRefreshButtonVisibility();

        // start your service
        // to fetch data from web
        Intent serviceIntent = new Intent(this, MultipleStationWidgetUpdateService.class);
        serviceIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startService(serviceIntent);

        // finish this activity
        this.finish();
    }

    private void sendIntentRefreshButtonVisibility(){
        CheckBox checkBox = (CheckBox) findViewById(R.id.displayRefreshButtonCheckBox);
        Log.v("LOG", "state of checkbox: " + checkBox.isChecked());
        Intent refreshButtonVisibility = new Intent(getApplicationContext(), MultipleStationWidgetProvider.class);
        refreshButtonVisibility.setAction(MultipleStationWidgetConfigActivity.SHOW_REFRESH_BUTTON);
        refreshButtonVisibility.putExtra(VISIBILITY_KEY, checkBox.isChecked());
        refreshButtonVisibility.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        sendBroadcast(refreshButtonVisibility);
    }

    @Override
    public Loader<List<Station>> onCreateLoader(int i, Bundle bundle) {
        return new StationLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Station>> loader, List<Station> stations) {

    }

    @Override
    public void onLoaderReset(Loader<List<Station>> loader) {

    }
}
