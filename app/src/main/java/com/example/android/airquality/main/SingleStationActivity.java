package com.example.android.airquality.main;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.vieweditors.SensorAdapter;
import com.example.android.airquality.vieweditors.SensorLoader;

import java.util.ArrayList;
import java.util.List;

public class SingleStationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Sensor>> {

    //id of loader, only matter when multiple loaders
    private static final int SENSOR_LOADER_ID = 2;

    //adapter for list of sensors
    private SensorAdapter sensorAdapter;

    Integer stationId;
    String stationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_station);

        //get Intent
        Intent intent = getIntent();
        stationId = intent.getIntExtra("StationId", 0);
        stationName = intent.getStringExtra("StationName");

        //find reference to TextView which displays name of the station
        TextView stationNameTextView = (TextView) findViewById(R.id.sensorsViewStationName);
        stationNameTextView.setText(stationName);

        //find reference to ListView in the layout
        ListView sensorListView = (ListView) findViewById(R.id.listViewOfSensors);

        //create new adapter, that takes empty sensor list as input
        sensorAdapter = new SensorAdapter(this, new ArrayList<Sensor>());

        //set the adapter, the list can be populated in the user's interface
        sensorListView.setAdapter(sensorAdapter);

        //Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(SENSOR_LOADER_ID, null, this);

        //TODO: add option to reload data if loaded unsuccessfully, add loading of station name
    }

    @Override
    public Loader<List<Sensor>> onCreateLoader(int id, Bundle args) {
        //create a new loader for given StationId
        return new SensorLoader(this, stationId);
    }

    @Override
    public void onLoadFinished(Loader<List<Sensor>> loader, List<Sensor> data) {
        //clear adapter of previous data
        sensorAdapter.clear();
        //If there is valid list of sensors add them to adapter's data set.
        //This will trigger the ListView to update
        if (data != null && !data.isEmpty()) {
            sensorAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Sensor>> loader) {
        sensorAdapter.clear();
    }

}
