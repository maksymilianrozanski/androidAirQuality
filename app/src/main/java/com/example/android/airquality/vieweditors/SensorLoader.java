package com.example.android.airquality.vieweditors;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.main.MainActivity;
import com.example.android.airquality.utility.QueryStationSensors;

import java.util.List;

import xdroid.toaster.Toaster;

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
        if (!MainActivity.isConnected(getContext())){
            Toaster.toast(R.string.no_internet_connection);
            return null;
        }
        return QueryStationSensors.fetchSensorData(stationId, getContext());
    }
}
