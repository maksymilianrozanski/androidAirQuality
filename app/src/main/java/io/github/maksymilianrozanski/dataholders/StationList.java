package io.github.maksymilianrozanski.dataholders;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.utility.LocationSaver;
import io.github.maksymilianrozanski.utility.NearestStationFinder;
import io.github.maksymilianrozanski.utility.QueryUtilities;
import io.github.maksymilianrozanski.utility.ServiceGenerator;
import io.github.maksymilianrozanski.utility.StationsRestService;
import okhttp3.ResponseBody;
import xdroid.toaster.Toaster;

public class StationList {

    @VisibleForTesting
    public static String STATIONS_BASE_URL = "http://api.gios.gov.pl/";
    private static boolean requestedUpdate = false;
    private static final String LOG_TAG = StationList.class.getSimpleName();
    private List<Station> stations;
    private static StationList instance = null;
    private StationsRestService stationsRestService;

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
        if (stationsRestService == null) {
            stationsRestService = ServiceGenerator.createService(StationsRestService.class);
        }
        retrofit2.Call<ResponseBody> call = stationsRestService.getAllStations();
        return ServiceGenerator.getResponseBody(call);
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

    public void sortByDistanceAndUpdateAdapter(ArrayAdapter<Station> stationAdapter, Activity activity, int permissionRequest){
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(activity);
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnCompleteListener(task -> {
                LocationSaver locationSaver = new LocationSaver(activity);
                Location location;
                if (task.isSuccessful() && task.getResult() != null) {
                    location = task.getResult();
                    locationSaver.saveLocation(location);
                } else {
                    location = locationSaver.getLocation();
                }
                this.sortStationsByDistance(activity, location);
                stationAdapter.clear();
                stationAdapter.addAll(this.getStations());
            });
        } else
            NearestStationFinder.askForLocationPermissionIfNoPermission(activity, permissionRequest);
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

    public String findStationName(int stationId) throws IOException {
        return findStationWithId(stationId).getName();
    }

    public Station findStationWithId(int stationId) throws IOException {
        for (Station currentStation : stations) {
            if (Integer.parseInt(currentStation.getId()) == stationId) {
                return currentStation;
            }
        }
        throw new IOException("Station with this id does not exist, requested id = " + stationId);
    }
}
