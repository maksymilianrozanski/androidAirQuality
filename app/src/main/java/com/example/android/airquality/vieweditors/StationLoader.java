package com.example.android.airquality.vieweditors;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.utility.QueryStationsList;

import java.util.List;

public class StationLoader extends AsyncTaskLoader<List<Station>> {

    private static final String LOG_TAG = StationLoader.class.getName();
    private String url;

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
        //check if there is saved list of stations in SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        String listOfStations = sharedPreferences.getString("STATIONS", "");
        boolean isStationListSaved = !listOfStations.isEmpty();
        //if list of stations is saved return list of stations loaded from SharedPreferences
        if (isStationListSaved) {
            Log.v(LOG_TAG, "Loading list of stations from SharedPreferences...");
            return QueryStationsList.fetchStationDataFromSharedPreferences(getContext());
        } else {
            // Perform the network request, parse the response, and extract a list of stations
            Log.v(LOG_TAG, "Making request to load list of stations from server...");
            return QueryStationsList.fetchStationData(url, getContext());
        }
    }
}

