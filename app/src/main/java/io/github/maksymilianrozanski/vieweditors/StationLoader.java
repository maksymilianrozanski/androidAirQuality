package io.github.maksymilianrozanski.vieweditors;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

import io.github.maksymilianrozanski.dataholders.Station;
import io.github.maksymilianrozanski.dataholders.StationList;

public class StationLoader extends AsyncTaskLoader<List<Station>> {

    private static final String LOG_TAG = StationLoader.class.getName();

    public StationLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Station> loadInBackground() {
        return StationList.getStationListInstance(getContext()).getStations();
    }
}

