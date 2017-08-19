package com.example.android.airquality.vieweditors;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.utility.QueryStationSensors;

import java.util.List;

/**
 * Created by Max on 19.08.2017.
 */

public class SensorLoader extends AsyncTaskLoader<List<Sensor>> {

    private int stationId;


    public SensorLoader(Context context, int stationId) {
        super(context);
        this.stationId = stationId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Sensor> loadInBackground() {
        return QueryStationSensors.fetchSensorData(stationId);
    }
}
