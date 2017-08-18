package com.example.android.airquality.utility;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.airquality.dataholders.Station;

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
import java.util.List;

import xdroid.toaster.Toaster;

/**
 * Created by Max on 16.08.2017.
 */

public class QueryStationsList {

    //Tag for log messages
    private static final String LOG_TAG = QueryStationsList.class.getSimpleName();

    //private constructor
    private QueryStationsList() {
    }

    public static List<Station> fetchStationData(String requestUrl) {

        //create URL object
        URL url = createUrl(requestUrl);

        //perform http request and receive JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        //extract fields from JSON response and create a list of Station objects
        List<Station> stations = extractFeatureFromJson(jsonResponse);

        //return the list of stations
        return stations;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
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
            Toaster.toast("An error occured.");
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

    private static List<Station> extractFeatureFromJson(String stationJSON) {
        //if JSON string is empty or null, then return early
        if (TextUtils.isEmpty(stationJSON)) {
            return null;
        }

        //create empty ArrayList where it's possible to add stations
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
                //get a single station at position i
                JSONObject currentStation = stationsArray.getJSONObject(i);

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
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryStationsList", "Problem parsing the JSON results", e);
            Toaster.toast("An error occured.");
        }
        return stations;
    }

    /**
     * Check for null values and JSONException
     *
     * @param jsonObject JSONObject from which String value is taken
     * @param jsonKey    Key in JSONObject - name of the value taken
     * @return String acquired form JSONObject, or "not specified" value if exception or null
     */
    private static String passJSONString(JSONObject jsonObject, String jsonKey) {
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
}
