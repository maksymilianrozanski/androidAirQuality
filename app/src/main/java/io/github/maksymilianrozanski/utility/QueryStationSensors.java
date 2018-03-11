package io.github.maksymilianrozanski.utility;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Sensor;
import okhttp3.ResponseBody;
import xdroid.toaster.Toaster;

import static io.github.maksymilianrozanski.utility.QueryUtilities.getStringFromJSONObject;


public class QueryStationSensors {

    private static final String LOG_TAG = QueryStationSensors.class.getSimpleName();

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
                Log.e(LOG_TAG, "Problem making the HTTP request");
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
        return ServiceGenerator.getResponseBody(call);
    }

    private String getResponseSensorData(int sensorId) throws IOException {
        if (stationsRestService == null) {
            stationsRestService = ServiceGenerator.createService(StationsRestService.class);
        }

        retrofit2.Call<ResponseBody> call = stationsRestService.getSensorValues(sensorId);
        return ServiceGenerator.getResponseBody(call);
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
            Log.e("QueryUtilities", "Problem parsing the JSON results");
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

    private List<Sensor> addDataToSensorList(List<Sensor> sensorList) throws IOException {
        for (int i = 0; i < sensorList.size(); i++) {
            Sensor currentSensor = sensorList.get(i);
            int currentSensorId = currentSensor.getId();

            String jsonResponse = null;
            for (int j = 1; j < 6; ) {
                try {
                    jsonResponse = getResponseSensorData(currentSensorId);
                    break;
                } catch (IOException | NullPointerException e) {
                    Log.e(LOG_TAG, "Problem making the HTTP request");
                    j++;
                }
            }

            if (jsonResponse == null) {
                throw new IOException(LOG_TAG + "couldn't fetch sensor data");
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

            for (int i = 0; i < jsonDataValueArray.length(); i++) {
                JSONObject recentData = jsonDataValueArray.getJSONObject(i);
                value = recentData.getString("value");
                if (!value.equals("null")) {
                    date = recentData.getString("date");
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
            Log.e(LOG_TAG, "Error occurred");
        }
        return inputSensor;
    }
}
