package com.example.android.airquality.vieweditors;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.airquality.utility.QueryStationsList;
import com.example.android.airquality.dataholders.Station;

import java.util.List;

/**
 * Created by Max on 16.08.2017.
 */

public class StationLoader extends AsyncTaskLoader<List<Station>> {

    //Tag fir log messages
    private static final String LOG_TAG = StationLoader.class.getName();

    //Query url
    private String url;


    /**
     * Constructs a new StationLoader
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public StationLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Station> loadInBackground() {
        if (url == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of stations
        return QueryStationsList.fetchStationData(url);
    }
}

