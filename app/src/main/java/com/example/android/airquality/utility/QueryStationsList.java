package com.example.android.airquality.utility;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Station;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xdroid.toaster.Toaster;

public class QueryStationsList {

    private static final String LOG_TAG = QueryStationsList.class.getSimpleName();

    private QueryStationsList() {
    }

    public static List<Station> fetchStationData(String requestUrl, Context context) {
        List<Station> stations = null;
        URL url = createUrl(requestUrl);
        String jsonResponse;

        //trying to get correct response from server up to 5 times
        for (int j = 1; j < 6; ) {
            try {
                jsonResponse = retryMakingHttpRequestIfException(url);
                stations = extractFeatureFromJson(jsonResponse, context);
                saveStationsToSharedPreferences(jsonResponse, context);
                break;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request", e);
                j++;
            }
        }
        return stations;
    }

    public static List<Station> fetchStationDataFromSharedPreferences(Context context) {
        String jsonResponse;
        List<Station> stations = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        jsonResponse = sharedPreferences.getString("STATIONS", null);

        try {
            stations = extractFeatureFromJson(jsonResponse, context);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Corrupted data loaded from SharedPreferences", e);
        }
        return stations;
    }

    static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    static String retryMakingHttpRequestIfException(URL url) {
        String jsonResponse;
        for (int i = 0; i < 5; ) {
            try {
                jsonResponse = makeHttpRequest(url);
                return jsonResponse;
            } catch (IOException e) {
                i++;
            }
        }
        return null;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
            throw e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public static void saveStationsToSharedPreferences(String stations, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("STATIONS", stations);
        editor.apply();
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Station> extractFeatureFromJson(String stationJSON, Context context) throws JSONException {
        if (TextUtils.isEmpty(stationJSON)) {
            return null;
        }

        List<Station> stations = new ArrayList<>();

        try {
            JSONArray stationsArray = new JSONArray(stationJSON);

            for (int i = 0; i < stationsArray.length(); i++) {
                stations.add(createStation(stationsArray, i));
            }
        } catch (JSONException e) {
            Log.e("QueryStationsList", "Problem parsing the JSON results", e);
            deleteStationsFromSharedPreferences(context);
            Toaster.toast(R.string.error_occurred);
            throw e;
        }
        return stations;
    }

    private static Station createStation(JSONArray stations, int indexOfStation) throws JSONException{
        JSONObject currentObject = (JSONObject) stations.get(indexOfStation);

        String id = passJSONString(currentObject, "id");
        String name = passJSONString(currentObject, "stationName");
        String gegrLat = passJSONString(currentObject, "gegrLat");
        String gegrLon = passJSONString(currentObject, "gegrLon");
        String cityId;
        String cityName;

        try {
            if (currentObject.getJSONObject("city") != null) {
                JSONObject currentCity = currentObject.getJSONObject("city");
                cityId = passJSONString(currentCity, "id");
                cityName = passJSONString(currentCity, "name");
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

    private static void deleteStationsFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("STATIONS", "");
        editor.apply();
    }

    static String passJSONString(JSONObject jsonObject, String jsonKey) {
        String stringToReturn;
        try {
            if (jsonObject.getString(jsonKey) != null) {
                stringToReturn = jsonObject.getString(jsonKey);
            } else {
                stringToReturn = "not specified";
            }
        } catch (JSONException e) {
            Log.v("passJSONString", "JSONException when passing string");
            stringToReturn = "not specified";
            return stringToReturn;
        }
        return stringToReturn;
    }

    public static JSONArray passStationListToJSONArray(List<Station> stations) {
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

    static void sortStationsByDistance(Context context) {
        List<Station> stations = QueryStationsList.fetchStationDataFromSharedPreferences(context);
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
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
                    }
                }
            });
        } else {
            Toaster.toast(context.getString(R.string.no_location_permission));
        }
    }

}
