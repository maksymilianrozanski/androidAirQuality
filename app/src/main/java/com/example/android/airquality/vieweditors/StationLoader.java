package com.example.android.airquality.vieweditors;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.dataholders.StationList;

import java.util.List;

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

