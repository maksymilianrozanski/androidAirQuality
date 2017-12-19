package com.example.android.airquality.utility;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import xdroid.toaster.Toaster;

import static com.example.android.airquality.utility.QueryUtilities.getStringFromJSONObject;
import static com.example.android.airquality.utility.QueryUtilities.retryMakingHttpRequestIfException;

public class QueryStationSensors {

    private static final String LOG_TAG = QueryStationSensors.class.getSimpleName();

    //beginning of url to query - need to add sensor id at the end
    //return type of param and array of dates + values
    @VisibleForTesting
    public static String BEGINNING_OF_URL_SENSOR_DATA = "http://api.gios.gov.pl/pjp-api/rest/data/getData/";

    private StationsRestService stationsRestService;

    public QueryStationSensors() {
    }

    public List<Sensor> fetchSensorData(int stationId) throws IOException {
        String jsonResponse = null;
        for (int j = 1; j < 6; ) {
            try {
                jsonResponse = getResponseListOfSensors(stationId);
                break;
            } catch (IOException | NullPointerException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request", e);
                j++;
            }
        }
        if (jsonResponse == null) {
            throw new IOException(LOG_TAG + "couldn't fetch sensor data");
        }

        List<Sensor> sensors = extractListOfSensorsFromJson(jsonResponse);
        sensors = addDataToSensorList(sensors);

        return sensors;
    }

    private String getResponseListOfSensors(int stationId) throws IOException {
        if (stationsRestService == null) {
            stationsRestService = ServiceGenerator.createService(StationsRestService.class);
        }

        retrofit2.Call<ResponseBody> call = stationsRestService.getListOfSensors(stationId);
        try {
            return call.execute().body().string();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static List<Sensor> extractListOfSensorsFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Sensor> sensors = new ArrayList<>();

        try {
            //Enter JSON response as an Array - response is already in array form
            JSONArray sensorArray = new JSONArray(jsonResponse);

            for (int i = 0; i < sensorArray.length(); i++) {
                sensors.add(createSensor(sensorArray, i));
            }
        } catch (JSONException e) {
            Log.e("QueryUtilities", "Problem parsing the JSON results", e);
            Toaster.toast(R.string.error_occurred);
        }
        return sensors;
    }

    private static Sensor createSensor(JSONArray sensorArray, int indexOfSensor) throws JSONException {
        JSONObject currentObject = (JSONObject) sensorArray.get(indexOfSensor);
        int sensorsId = Integer.parseInt(getStringFromJSONObject(currentObject, "id"));
        JSONObject sensorsParamJSON = currentObject.getJSONObject("param");
        String sensorsParam = getStringFromJSONObject(sensorsParamJSON, "paramFormula");
        return new Sensor(sensorsId, sensorsParam);
    }

    private static List<Sensor> addDataToSensorList(List<Sensor> sensorList) {
        for (int i = 0; i < sensorList.size(); i++) {
            Sensor currentSensor = sensorList.get(i);
            int currentSensorId = currentSensor.getId();
            URL url;

            try {
                url = new URL(BEGINNING_OF_URL_SENSOR_DATA + currentSensorId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return sensorList;
            }

            String jsonResponse;
            try {
                jsonResponse = retryMakingHttpRequestIfException(url);
            } catch (IOException e) {
                continue;
            }
            currentSensor = addValueAndDate(currentSensor, jsonResponse);
            sensorList.set(i, currentSensor);
        }
        return sensorList;
    }

    private static Sensor addValueAndDate(Sensor inputSensor, String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
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
//                Log.v(LOG_TAG, "value: " + value);
                //if value is not null, get date and break the loop
                if (!value.equals("null")) {
                    date = recentData.getString("date");
//                    Log.v(LOG_TAG, "date: " + date);
                    break;
                }
            }
            inputSensor.setLastDate(date);
            try {
                inputSensor.setValue(Double.parseDouble(value));
            } catch (NumberFormatException f) {
                inputSensor.setValue(0);
            }
        } catch (JSONException | NumberFormatException e) {
            Log.e(LOG_TAG, "Error occurred", e);
        }
        return inputSensor;
    }
}
