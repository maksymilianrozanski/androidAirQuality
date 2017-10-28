package com.example.android.airquality.utility;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

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

/**
 * Created by Max on 16.08.2017.
 */

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

        //extract fields from JSON response and create a list of Station objects
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

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     *
     * @param url url to query data from
     * @return String given from server
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
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
        Log.v(LOG_TAG, "Saved jsonResponse to SharedPreferences");
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //Create a JSONArray from the JSON response string
            //String is already in Array form
            JSONArray stationsArray = new JSONArray(stationJSON);

            //for each station create a single Station object
            for (int i = 0; i < stationsArray.length(); i++) {
                //get "i" element of stationsArray and cast to JSONObject
                JSONObject currentObject = (JSONObject) stationsArray.get(i);

                //declaration of station properties, pass data from JSONObject
                String id = passJSONString(currentObject, "id");
                String name = passJSONString(currentObject, "stationName");
                String gegrLat = passJSONString(currentObject, "gegrLat");
                String gegrLon = passJSONString(currentObject, "gegrLon");
                String cityId;
                String cityName;

                //get city - JSONObject, if city object is null, pass "not specified" as data
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
                //create new Station object
                Station station = new Station(id, name, gegrLat, gegrLon, cityId, cityName);
                //add created Station object to stations List
                stations.add(station);
            }
        } catch (JSONException e) {
            Log.e("QueryStationsList", "Problem parsing the JSON results", e);
            deleteStationsFromSharedPreferences(context);
            Toaster.toast("An error occurred.");
            throw e;
        }
        return stations;
    }

    private static void deleteStationsFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("STATIONS", "");
        editor.apply();
    }

    /**
     * Check for null values and JSONException
     *
     * @param jsonObject JSONObject from which String value is taken
     * @param jsonKey    Key in JSONObject - name of the value taken
     * @return String acquired form JSONObject, or "not specified" value if exception or null
     */
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

    public static void sortStationsByDistance(Context context) {
        List<Station> stations = QueryStationsList.fetchStationDataFromSharedPreferences(context);
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Location lastLocation = location;
                        double userLatitude = 0;
                        double userLongitude = 0;
                        try {
                            userLatitude = lastLocation.getLatitude();
                            userLongitude = lastLocation.getLongitude();
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
            Toaster.toast("No location permission");
        }
    }

}
