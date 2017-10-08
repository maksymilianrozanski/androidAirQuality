package com.example.android.airquality.main;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.utility.NearestStationFinder;
import com.example.android.airquality.utility.QueryStationsList;
import com.example.android.airquality.vieweditors.StationAdapter;
import com.example.android.airquality.vieweditors.StationLoader;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Station>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    //url for data - list of stations
    private static final String URL_QUERY = "http://api.gios.gov.pl/pjp-api/rest/station/findAll";

    //id of loader, only matter when multiple loaders
    private static final int STATION_LOADER_ID = 1;

    private StationAdapter stationAdapter;

    LoaderManager loaderManager = getLoaderManager();

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView stationListView = (ListView) findViewById(R.id.list);

        stationAdapter = new StationAdapter(this, new ArrayList<Station>());

        //set the adapter, the list can be populated in the user's interface
        stationListView.setAdapter(stationAdapter);

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(STATION_LOADER_ID, null, this);

        //OnClickListener - redirects to SingleStationActivity
        stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //find station that was clicked
                Station station = stationAdapter.getItem(position);

                int currentStationId;
                try {
                    //get id of the station that was clicked
                    currentStationId = Integer.parseInt(station.getId());
                } catch (NullPointerException e) {
                    currentStationId = 0;
                    Log.e(LOG_TAG, "Error when getting station.getId", e);
                }
                String currentStationName;
                try {
                    //get name of the station that was clicked
                    currentStationName = station.getName();
                } catch (NullPointerException e) {
                    currentStationName = "";
                    Log.e(LOG_TAG, "Error when getting station.getName", e);
                }

                //create new intent, add current StationId and currentStationName as extra,
                // start new activity
                Intent intent = new Intent(getApplicationContext(), SingleStationActivity.class);
                intent.putExtra("StationId", currentStationId);
                intent.putExtra("StationName", currentStationName);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<List<Station>> onCreateLoader(int id, Bundle args) {
        //create a new loader for given URL
        return new StationLoader(this, URL_QUERY);
    }

    @Override
    public void onLoadFinished(Loader<List<Station>> loader, List<Station> data) {
        Log.v("Info", "Inside onLoaderFinished - start");
        stationAdapter.clear();
        Log.v("Info", "Inside onLoaderFinished - after .clear");
        //If there is valid list of stations add them to adapter's data set.
        // This will trigger the ListView to update
        if (data != null && !data.isEmpty()) {
            stationAdapter.addAll(data);
            Log.v("Info", "Inside onLoaderFinished - after .addAll(data)");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Station>> loader) {
        Log.v("Info", "Inside onLoaderReset - before .clear");
        stationAdapter.clear();
        Log.v("Info", "Inside onLoaderReset - after .clear");
    }

    //for checking is device connected to the Internet
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    //show three dot menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //handle menu items clicks  //TODO: add menu item "sort stations"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                reloadStations();
                return true;
            case R.id.findNearestStation:
                goToNearestStation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reloadStations() {
        if (isConnected(getApplicationContext())) {
            Log.v("Info", "Connected to the internet");
            loaderManager.restartLoader(STATION_LOADER_ID, null, this);
        } else {
            Log.v("info", "No Internet connection");
            Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToNearestStation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        int MY_PERMISSION_REQUEST = 0;

        askForLocationPermissionIfNoPermission(MY_PERMISSION_REQUEST);

        //if app have permission - print location in log
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.v(LOG_TAG, "Location: " + location.toString());
                                // Logic to handle location object
                                Log.v(LOG_TAG, "location.getLatitude(): " + location.getLatitude()
                                        + "location.getLongitude(): " + location.getLongitude());
                                List<Station> stations = QueryStationsList.fetchStationDataFromSharedPreferences(getApplicationContext());
                                Integer nearestStationId = NearestStationFinder.findNearestStation(location.getLatitude(), location.getLongitude(), stations);
                                Log.v(LOG_TAG, "the nearest station id: " + nearestStationId);

                                //create new intent to pass stationId and stationName to SingleStationActivity
                                Intent intent = new Intent(getApplicationContext(), SingleStationActivity.class);
                                intent.putExtra("StationId", nearestStationId);
                                for (Station currentStation : stations) {
                                    if (Integer.parseInt(currentStation.getId()) == nearestStationId) {
                                        intent.putExtra("StationName", currentStation.getName());
                                        break;
                                    }
                                }
                                startActivity(intent);
                            } else {
                                Log.v(LOG_TAG, "location == null");
                            }
                        }
                    });
        }
    }

    private boolean askForLocationPermissionIfNoPermission(int permissionNumber){
        //if app doesn't have permission - requestPermission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.v(LOG_TAG, "No permission, asking for permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, permissionNumber);
            return false;
        }
        return true;
    }

    private void sortStationsByDistance(){

    }
}