package io.github.maksymilianrozanski.widget;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.BuildCompat;

import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Station;
import io.github.maksymilianrozanski.utility.NearestStationFinder;
import io.github.maksymilianrozanski.vieweditors.StationLoader;
import io.github.maksymilianrozanski.widget.service.MultipleStationWidgetUpdateIntentService;

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

        CheckBox requestPermissionCheckBox = (CheckBox) findViewById(R.id.requestPermissionCheckBox);

        if (isLocationPermissionGranted()) {
            requestPermissionCheckBox.setChecked(true);
            requestPermissionCheckBox.setEnabled(false);
        } else {
            requestPermissionCheckBox.setOnClickListener(this);
        }

        Button startButton = (Button) findViewById(R.id.widgetStartButton);
        startButton.setOnClickListener(this);
    }

    private boolean isLocationPermissionGranted() {
        return isForegroundPermissionGranted() && isBackgroundPermissionGranted();
    }

    private boolean isForegroundPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isBackgroundPermissionGranted() {
        if (BuildCompat.isAtLeastQ()) {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else return true;
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
            case R.id.requestPermissionCheckBox:
                //TODO: ask for background permission if Android Q
                NearestStationFinder.askForLocationPermissionIfNoPermission(this, MY_PERMISSION_REQUEST);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CheckBox requestPermissionCheckBox = (CheckBox) findViewById(R.id.requestPermissionCheckBox);
        if (isLocationPermissionGranted()) {
            requestPermissionCheckBox.setChecked(true);
            requestPermissionCheckBox.setEnabled(false);
        } else {
            requestPermissionCheckBox.setChecked(false);
            requestPermissionCheckBox.setEnabled(true);
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
        Intent serviceIntent = new Intent(this, MultipleStationWidgetUpdateIntentService.class);
        serviceIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startService(serviceIntent);

        // finish this activity
        this.finish();
    }

    private void sendIntentRefreshButtonVisibility() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.displayRefreshButtonCheckBox);
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
