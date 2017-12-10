package com.example.android.airquality.dataholders;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.airquality.R;
import com.example.android.airquality.utility.QueryStationSensors;
import com.example.android.airquality.utility.QueryUtilities;
import com.example.android.airquality.utility.StationsRestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Collator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import xdroid.toaster.Toaster;

public class StationList {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public static String STATIONS_BASE_URL = "http://api.gios.gov.pl/";
    private static boolean requestedUpdate = false;
    private static final String LOG_TAG = StationList.class.getSimpleName();
    private List<Station> stations;
    private static StationList instance = null;
    private StationsRestService stationsRestService;
    private Retrofit retrofit;
    @VisibleForTesting
    public Calendar calendar;

    private StationList(Context context) {
        fetchStationDataFromSharedPreferences(context);
        if (stations == null) {
            fetchStationDataFromWeb(context);
        }
    }

    public static StationList getStationListInstance(Context context) {
        if (instance == null) {
            instance = new StationList(context);
        }
        if (requestedUpdate) {
            instance.stations = instance.fetchStationDataFromWeb(context);
            requestedUpdate = false;
        }
        return instance;
    }

    public Station getStation(int index) {
        return stations.get(index);
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Station> getStationsSortedByCityName() {
        List<Station> stationsSortedByCityName = new ArrayList<>(getStations());
        Collator collator = Collator.getInstance(new Locale("pl", "PL"));

        Collections.sort(stationsSortedByCityName, (Station station0, Station station1) -> {
            if (collator.compare(station0.getCityName(), station1.getCityName()) > 0) {
                return 1;
            } else if (collator.compare(station0.getCityName(), station1.getCityName()) < 0) {
                return -1;
            } else return 0;
        });
        return stationsSortedByCityName;
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

    private List<Station> fetchStationDataFromWeb(Context context) {
        //trying to get correct response from server up to 5 times
        for (int j = 1; j < 6; ) {
            try {
                String jsonResponse = getHttpResponseRetrofit();
                stations = extractFeatureFromJson(jsonResponse, context);
                saveStationsToSharedPreferences(jsonResponse, context);
                return stations;
            } catch (JSONException | IOException | NullPointerException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request", e);
                j++;
            }
        }
        Toaster.toast(R.string.could_not_connect_to_server);
        if (stations != null) {
            return stations;
        }
        stations = new ArrayList<>();
        return stations;
    }

    public static void setRequestForUpdatingStations() {
        requestedUpdate = true;
    }

    private String getHttpResponseRetrofit() throws IOException {
        OkHttpClient client = new OkHttpClient();

        retrofit = new Retrofit.Builder()
                .baseUrl(STATIONS_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        stationsRestService = retrofit.create(StationsRestService.class);

        retrofit2.Call<ResponseBody> call = stationsRestService.getAllStations();
        try {
            return call.execute().body().string();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            throw e;
        }
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

    private static void deleteStationsFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.android.airquality", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("STATIONS", "");
        editor.apply();
    }

    private Station createStation(JSONArray stations, int indexOfStation) throws JSONException {
        JSONObject currentObject = (JSONObject) stations.get(indexOfStation);

        String id = QueryUtilities.getStringFromJSONObject(currentObject, "id");
        String name = QueryUtilities.getStringFromJSONObject(currentObject, "stationName");
        String gegrLat = QueryUtilities.getStringFromJSONObject(currentObject, "gegrLat");
        String gegrLon = QueryUtilities.getStringFromJSONObject(currentObject, "gegrLon");
        String cityId;
        String cityName;

        try {
            if (currentObject.getJSONObject("city") != null) {
                JSONObject currentCity = currentObject.getJSONObject("city");
                cityId = QueryUtilities.getStringFromJSONObject(currentCity, "id");
                cityName = QueryUtilities.getStringFromJSONObject(currentCity, "name");
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

    public void sortStationsByDistance(Context context, Location location) throws NullPointerException {
        stations = fetchStationDataFromSharedPreferences(context);

        if (location != null) {
            double userLatitude;
            double userLongitude;
            try {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Null pointer exception" + e);
                throw e;
            }
            for (Station station : stations) {
                station.setDistanceFromUser(userLatitude, userLongitude);
            }
            Collections.sort(stations);
            JSONArray jsonArray = passStationListToJSONArray(stations);
            saveStationsToSharedPreferences(jsonArray.toString(), context);
        } else {
            Toaster.toast(context.getString(R.string.no_location_access));
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

    public Sensor findSensorWithHighestPercentValue(int stationId) throws IOException {
        List<Sensor> sensors = QueryStationSensors.fetchSensorData(stationId);
        return getSensorWithHighestValue(sensors);
    }

    public Sensor findSensorWithHighestPercentValue(int stationId, int ignoreOlderThanHours) throws IOException {
        List<Sensor> sensors = QueryStationSensors.fetchSensorData(stationId);
        sensors = removeSensorsWhereValueOlderThan(sensors, ignoreOlderThanHours);
        return getSensorWithHighestValue(sensors);
    }

    public List<Sensor> removeSensorsWhereValueOlderThan(List<Sensor> sensors, int timeInHours) {
        calendar = new GregorianCalendar();
        for (int i = 0; i < sensors.size(); i++) {
            try {
                if (sensors.get(i).getTimeInMillis() < (calendar.getTimeInMillis() - (timeInHours * 3600000))) {
                    sensors.remove(i);
                    i = i - 1;
                }
            } catch (ParseException e) {
                sensors.remove(i);
                i = i - 1;
            }
        }
        return sensors;
    }

    private Sensor getSensorWithHighestValue(List<Sensor> sensors) {
        if (sensors.size() == 1) return sensors.get(0);
        double highestValue = Double.MIN_VALUE;
        Sensor sensorHighestCalculatedValue = sensors.get(0);
        for (int i = 1; i < sensors.size(); i++) {
            double calculatedValue = sensors.get(i).percentOfMaxValue();
            if (calculatedValue > highestValue) {
                highestValue = calculatedValue;
                sensorHighestCalculatedValue = sensors.get(i);
            }
        }
        return sensorHighestCalculatedValue;
    }

    public String findStationName(int stationId) throws IOException {
        return findStationWithId(stationId).getName();
    }

    private Station findStationWithId(int stationId) throws IOException {
        for (Station currentStation : stations) {
            if (Integer.parseInt(currentStation.getId()) == stationId) {
                return currentStation;
            }
        }
        throw new IOException("Station with this id does not exist, requested id = " + stationId);
    }
}
