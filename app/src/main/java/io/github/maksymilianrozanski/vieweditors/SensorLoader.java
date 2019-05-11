package io.github.maksymilianrozanski.vieweditors;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Sensor;
import io.github.maksymilianrozanski.main.MainActivity;
import io.github.maksymilianrozanski.utility.QueryStationSensors;
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
        if (!MainActivity.isConnected(getContext())) {
            Toaster.toast(R.string.no_internet_connection);
            return null;
        }
        try {
            QueryStationSensors queryStationSensors = new QueryStationSensors();
            return queryStationSensors.fetchSensorData(stationId);
        } catch (IOException e) {
            Toaster.toast(R.string.could_not_connect_to_server);
            return null;
        }
    }
}
