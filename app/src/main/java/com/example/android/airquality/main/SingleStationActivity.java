package com.example.android.airquality.main;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.vieweditors.SensorAdapter;
import com.example.android.airquality.vieweditors.SensorLoader;

import java.util.ArrayList;
import java.util.List;

public class SingleStationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Sensor>>{

    private static final int SENSOR_LOADER_ID = 2;
    private static final String LOG_TAG = MainActivity.class.getName();
    private SensorAdapter sensorAdapter;
    private LoaderManager loaderManager;

    Integer stationId;
    String stationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_station);

        Intent intent = getIntent();
        stationId = intent.getIntExtra("StationId", 0);
        stationName = intent.getStringExtra("StationName");

        //find reference to TextView which displays name of the station
        TextView stationNameTextView = (TextView) findViewById(R.id.sensorsViewStationName);
        stationNameTextView.setText(stationName);

        ListView sensorListView = (ListView) findViewById(R.id.listViewOfSensors);

        sensorAdapter = new SensorAdapter(this, new ArrayList<Sensor>());

        sensorListView.setAdapter(sensorAdapter);

        loaderManager = getLoaderManager();
        loaderManager.initLoader(SENSOR_LOADER_ID, null, this);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshSingleStation);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        reloadSensors();
                        Log.v(LOG_TAG, "Inside setOnRefreshListener");
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
        );
    }

    private void reloadSensors() {
        if (MainActivity.isConnected(getApplicationContext())) {
            loaderManager.restartLoader(SENSOR_LOADER_ID, null, this);
        } else {
            Log.v("info", "No Internet connection");
            Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<List<Sensor>> onCreateLoader(int id, Bundle args) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshSingleStation);
        swipeRefreshLayout.setRefreshing(true);
        return new SensorLoader(this, stationId);
    }

    @Override
    public void onLoadFinished(Loader<List<Sensor>> loader, List<Sensor> data) {
        sensorAdapter.clear();
        //If there is valid list of sensors add them to adapter's data set.
        //This will trigger the ListView to update
        if (data != null && !data.isEmpty()) {
            sensorAdapter.addAll(data);
        }
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshSingleStation);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Sensor>> loader) {
        sensorAdapter.clear();
    }

}
