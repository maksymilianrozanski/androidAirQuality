package com.example.android.airquality.utility;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.airquality.dataholders.Sensor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import xdroid.toaster.Toaster;

import static com.example.android.airquality.utility.QueryStationsList.createUrl;
import static com.example.android.airquality.utility.QueryStationsList.makeHttpRequest;
import static com.example.android.airquality.utility.QueryStationsList.passJSONString;

/**
 * Created by Max on 18.08.2017.
 */

public class QueryStationSensors {

    //Tag for log messages
    private static final String LOG_TAG = QueryStationSensors.class.getSimpleName();

    //beginning of url to query - need to add station id at the end
    private static final String beginningOfUrl = "http://api.gios.gov.pl/pjp-api/rest/station/sensors/";

    //private constructor
    private QueryStationSensors() {
    }

    /**
     * @param stationId id of station from which we request list of sensors
     * @return return list of sensors on station with entered id
     */
    public static List<Sensor> fetchSensorData(int stationId) {
        URL url = createUrl(beginningOfUrl + stationId);

        //perform http request and receive JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        //extract list of sensors from JSON response
        List<Sensor> sensors = extractListOfSensorsFromJson(jsonResponse);

        //return list of sensors
        return sensors;
    }

    /**
     * Transform JSON String response to list of sensors, set data of each sensor
     *
     * @param jsonResponse jsonResponse from server
     * @return List of sensors on one station
     */
    private static List<Sensor> extractListOfSensorsFromJson(String jsonResponse) {
        //if JSON string is empty or null, then return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        //create empty ArrayList where it's possible to add sensors
        List<Sensor> sensors = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //Enter JSON response as an Array - response is already in array form
            JSONArray sensorsArray = new JSONArray(jsonResponse);

            //for each sensor create a single Sensor object
            for (int i = 0; i < sensorsArray.length(); i++) {
                //get "i" element of sensorsArray and cast to JSONObject
                JSONObject currentObject = (JSONObject) sensorsArray.get(i);

                //declaration of sensor's properties, pass data from JSONObject
                int sensorsId = Integer.parseInt(passJSONString(currentObject, "id"));

                //get "param" object, containing "paramFormula"
                JSONObject sensorsParamJSON = currentObject.getJSONObject("param");
                //param measured by sensor/sensor type
                String sensorsParam = passJSONString(sensorsParamJSON, "paramFormula");

                //create new Sensor object
                Sensor sensor = new Sensor(sensorsId, sensorsParam);
                //add created Sensor object to sensors List
                sensors.add(sensor);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception, and show a toast to the user.
            Log.e("QueryStationsList", "Problem parsing the JSON results", e);
            Toaster.toast("An error occured.");
        }return sensors;
    }

}
