package io.github.maksymilianrozanski.main;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Sensor;
import io.github.maksymilianrozanski.vieweditors.SensorAdapter;
import io.github.maksymilianrozanski.vieweditors.SensorLoader;

public class SingleStationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Sensor>> {

    private static final int SENSOR_LOADER_ID = 2;
    private SensorAdapter sensorAdapter;
    private LoaderManager loaderManager;

    Integer stationId;
    String stationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_station);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        stationId = intent.getIntExtra("StationId", 0);
        stationName = intent.getStringExtra("StationName");

        //find reference to TextView which displays name of the station
        TextView stationNameTextView = (TextView) findViewById(R.id.sensorsViewStationName);
        stationNameTextView.setText(stationName);

        ListView sensorListView = (ListView) findViewById(R.id.listViewOfSensors);

        sensorAdapter = new SensorAdapter(this, new ArrayList<>());

        sensorListView.setAdapter(sensorAdapter);

        loaderManager = getLoaderManager();
        loaderManager.initLoader(SENSOR_LOADER_ID, null, this);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshSingleStation);
        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    reloadSensors();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void reloadSensors() {
        if (MainActivity.isConnected(getApplicationContext())) {
            loaderManager.restartLoader(SENSOR_LOADER_ID, null, this);
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
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
