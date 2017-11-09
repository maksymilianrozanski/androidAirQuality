package com.example.android.airquality.dataholders;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.airquality.R;
import com.example.android.airquality.utility.QueryUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xdroid.toaster.Toaster;

public class StationList {

    //url for data - list of stations
    private static final String URL_QUERY = "http://api.gios.gov.pl/pjp-api/rest/station/findAll";
    private static final String LOG_TAG = StationList.class.getSimpleName();
    private List<Station> stations;
    private static StationList instance = null;

    private StationList(Context context) {
        fetchStationDataFromSharedPreferences(context);
        if (stations == null) {
            fetchStationDataFromWeb(URL_QUERY, context);
        }
    }

    public static StationList getStationListInstance(Context context) {
        if (instance == null) {
            instance = new StationList(context);
        }
        return instance;
    }

    public Station getStation(int index) {
        return stations.get(index);
    }

    public List<Station> getStations() {
        return stations;
    }

    private List<Station> fetchStationDataFromSharedPreferences(Context context) {
        stations = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        String jsonResponse = sharedPreferences.getString("STATIONS", null);

        try {
            stations = extractFeatureFromJson(jsonResponse, context);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Corrupted data loaded from SharedPreferences", e);
        }
        return stations;
    }

    private List<Station> fetchStationDataFromWeb(String requestUrl, Context context) {
        stations = null;
        URL url = QueryUtilities.createUrl(requestUrl);
        String jsonResponse;

        //trying to get correct response from server up to 5 times
        for (int j = 1; j < 6; ) {
            try {
                jsonResponse = QueryUtilities.retryMakingHttpRequestIfException(url);
                stations = extractFeatureFromJson(jsonResponse, context);
                saveStationsToSharedPreferences(jsonResponse, context);
                break;
            } catch (JSONException | IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request", e);
                j++;
            }
        }
        return stations;
    }

    private List<Station> extractFeatureFromJson(String stationJSON, Context context) throws JSONException {
        if (TextUtils.isEmpty(stationJSON)) {
            return null;
        }

        stations = new ArrayList<>();

        try {
            JSONArray stationsArray = new JSONArray(stationJSON);

            for (int i = 0; i < stationsArray.length(); i++) {
                stations.add(createStation(stationsArray, i));
            }
        } catch (JSONException e) {
            Log.e("QueryUtilities", "Problem parsing the JSON results", e);
            deleteStationsFromSharedPreferences(context);
            Toaster.toast(R.string.error_occurred);
            throw e;
        }
        return stations;
    }

    private void deleteStationsFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("STATIONS", "");
        editor.apply();
    }

    private Station createStation(JSONArray stations, int indexOfStation) throws JSONException {
        JSONObject currentObject = (JSONObject) stations.get(indexOfStation);

        String id = QueryUtilities.passJSONString(currentObject, "id");
        String name = QueryUtilities.passJSONString(currentObject, "stationName");
        String gegrLat = QueryUtilities.passJSONString(currentObject, "gegrLat");
        String gegrLon = QueryUtilities.passJSONString(currentObject, "gegrLon");
        String cityId;
        String cityName;

        try {
            if (currentObject.getJSONObject("city") != null) {
                JSONObject currentCity = currentObject.getJSONObject("city");
                cityId = QueryUtilities.passJSONString(currentCity, "id");
                cityName = QueryUtilities.passJSONString(currentCity, "name");
            } else {
                cityId = "not specified";
                cityName = "not specified";
            }
        } catch (JSONException e) {
            cityId = "not specified";
            cityName = "not specified";
        }
        return new Station(id, name, gegrLat, gegrLon, cityId, cityName);
    }

    public void sortStationsByDistance(Context context, Location location) {
        stations = fetchStationDataFromSharedPreferences(context);

        if (location != null) {
            double userLatitude = 0;
            double userLongitude = 0;
            try {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
                Log.v(LOG_TAG, "user latitude: " + userLatitude + "user longitude: " + userLongitude);
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Null pointer exception" + e);
            }
            for (Station station : stations) {
                station.setDistanceFromUser(userLatitude, userLongitude);
            }
            Collections.sort(stations);
            JSONArray jsonArray = passStationListToJSONArray(stations);
            saveStationsToSharedPreferences(jsonArray.toString(), context);
        } else {
            Toaster.toast(context.getString(R.string.no_location_permission));
        }
    }

    private void saveStationsToSharedPreferences(String stations, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("STATIONS", stations);
        editor.apply();
    }

    private static JSONArray passStationListToJSONArray(List<Station> stations) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < stations.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", stations.get(i).getId());
                jsonObject.put("stationName", stations.get(i).getName());
                jsonObject.put("gegrLat", stations.get(i).getGegrLat());
                jsonObject.put("gegrLon", stations.get(i).getGegrLon());
                JSONObject city = new JSONObject();
                city.put("id", stations.get(i).getCityId());
                city.put("name", stations.get(i).getCityName());
                jsonObject.put("city", city);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException" + e);
            }
        }
        return jsonArray;
    }
}
