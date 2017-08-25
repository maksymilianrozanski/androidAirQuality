package com.example.android.airquality.utility;

import android.content.Context;
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

    //beginning of url to query - need to add station id at the end - return list of sensors
    private static final String BEGINNING_OF_URL_SENSORS_LIST = "http://api.gios.gov.pl/pjp-api/rest/station/sensors/";

    //beginning of url to query - need to add sensor id at the end
    //return type of param and array of dates + values
    private static final String BEGINNING_OF_URL_SENSOR_DATA = "http://api.gios.gov.pl/pjp-api/rest/data/getData/";

    //private constructor
    private QueryStationSensors() {
    }

    /**
     * @param stationId id of station from which we request list of sensors
     * @return return list of sensors on station with entered id
     */
    public static List<Sensor> fetchSensorData(int stationId, Context context) {
        URL url = createUrl(BEGINNING_OF_URL_SENSORS_LIST + stationId);

        //perform http request and receive JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url, false, context);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        //extract list of sensors from JSON response
        List<Sensor> sensors = extractListOfSensorsFromJson(jsonResponse);
        //add param value and last measurement date
        sensors = addDataToSensorList(sensors, context);

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
            Toaster.toast("An error occurred.");
        }
        return sensors;
    }

    /**
     * Adds new values from sensors to the List
     *
     * @param inputList list of sensors
     * @return list of sensors with new measurement value and date
     */
    private static List<Sensor> addDataToSensorList(List<Sensor> inputList, Context context) {
        for (int i = 0; i < inputList.size(); i++) {
            Sensor currentSensor = inputList.get(i);
            //get currentSensorId
            int currentSensorId = currentSensor.getId();
            //create url to query based on sensor's id
            URL url = createUrl(BEGINNING_OF_URL_SENSOR_DATA + currentSensorId);
            //fetch data based on currentSensorId
            String jsonResponse = null;
            //trying to get correct response from server up to 5 times
            for (int j = 1; j < 6; j = j) {
                try {
                    Log.v(LOG_TAG, "Trying to make http request: " + j + " time...");
                    jsonResponse = makeHttpRequest(url, false, context);
                    //if no exception is thrown, break inner "for" loop
                    break;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Problem making the HTTP request.", e);
                    //add 1 to for loop counter if exception is thrown by makeHttpRequest method
                    j = j + 1;
                }
            }
            //extract fields from jsonResponse and add data to Sensor object
            currentSensor = addValueAndDate(currentSensor, jsonResponse);
            inputList.set(i, currentSensor);
        }
        return inputList;
    }

    /**
     * Add or refresh value of measured param, and data of last measurement
     *
     * @param inputSensor  - Sensor object
     * @param jsonResponse - jsonData about input sensor
     * @return sensor with new values, or unchanged sensor object if JSONException
     */
    private static Sensor addValueAndDate(Sensor inputSensor, String jsonResponse) {
        //if JSON string is empty or null, then return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            String value = "no data to display";
            String date = "no data to display";
            JSONObject jsonObject = new JSONObject(jsonResponse);
            //get an array of JSONObjects
            JSONArray jsonDataValueArray = jsonObject.getJSONArray("values");

            //get element of an array with most recent data, and check if "value" of measured param
            //is not null
            for (int i = 0; i < jsonDataValueArray.length(); i++) {
                //get "i" element of an array
                JSONObject recentData = jsonDataValueArray.getJSONObject(i);
                //get value of measured param
                value = recentData.getString("value");
                Log.v(LOG_TAG, "value: " + value);
                //if value is not null, get date and break the loop
                if (!value.equals("null")) {
                    date = recentData.getString("date");
                    Log.v(LOG_TAG, "date: " + date);
                    break;
                }
            }
            //add new date and value to Sensor object
            inputSensor.setLastDate(date);
            //convert String "value" to double
            inputSensor.setValue(Double.parseDouble(value));
        } catch (JSONException | NumberFormatException e) {
            Log.e(LOG_TAG, "Error occurred", e);
        }

        return inputSensor;
    }
}
