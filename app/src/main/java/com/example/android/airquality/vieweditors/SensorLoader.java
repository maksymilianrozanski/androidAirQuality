package com.example.android.airquality.vieweditors;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.main.MainActivity;
import com.example.android.airquality.utility.QueryStationSensors;

import java.io.IOException;
import java.util.List;

import xdroid.toaster.Toaster;

public class SensorLoader extends AsyncTaskLoader<List<Sensor>> {

    private static final String LOG_TAG = SensorLoader.class.getSimpleName();
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
        try {
            return QueryStationSensors.fetchSensorData(stationId);
        }catch (IOException e){
            Log.e(LOG_TAG, "fetching data failed");
            Toaster.toast(R.string.could_not_connect_to_server);
            return null;
        }
    }
}
