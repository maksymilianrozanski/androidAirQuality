package io.github.maksymilianrozanski.main;

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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Station;
import io.github.maksymilianrozanski.dataholders.StationList;
import io.github.maksymilianrozanski.utility.LocationSaver;
import io.github.maksymilianrozanski.utility.NearestStationFinder;
import io.github.maksymilianrozanski.vieweditors.StationAdapter;
import io.github.maksymilianrozanski.vieweditors.StationLoader;
import xdroid.toaster.Toaster;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Station>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    //id of loader, only matter when multiple loaders
    private static final int STATION_LOADER_ID = 1;
    private static final int MY_PERMISSION_REQUEST = 0;

    private StationAdapter stationAdapter;

    LoaderManager loaderManager = getLoaderManager();

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView stationListView = (ListView) findViewById(R.id.list);

        stationAdapter = new StationAdapter(this, new ArrayList<>());

        stationListView.setAdapter(stationAdapter);

        loaderManager.initLoader(STATION_LOADER_ID, null, this);

        //OnClickListener - redirects to SingleStationActivity
        stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Station station = stationAdapter.getItem(position);

                int currentStationId;
                try {
                    currentStationId = Integer.parseInt(station.getId());
                } catch (NullPointerException e) {
                    currentStationId = 0;
                    Log.e(LOG_TAG, "Error when getting station.getId", e);
                }
                String currentStationName;
                try {
                    currentStationName = station.getName();
                } catch (NullPointerException e) {
                    currentStationName = "";
                    Log.e(LOG_TAG, "Error when getting station.getName", e);
                }

                Intent intent = new Intent(getApplicationContext(), SingleStationActivity.class);
                intent.putExtra("StationId", currentStationId);
                intent.putExtra("StationName", currentStationName);
                startActivity(intent);
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshMainActivity);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            reloadStations();
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public Loader<List<Station>> onCreateLoader(int id, Bundle args) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshMainActivity);
        swipeRefreshLayout.setRefreshing(true);
        return new StationLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Station>> loader, List<Station> data) {
        stationAdapter.clear();
        if (data != null && !data.isEmpty()) {
            stationAdapter.addAll(data);
        }
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshMainActivity);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Station>> loader) {
        stationAdapter.clear();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork;
        try {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "NullPointerException: " + e);
            return false;
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                reloadStations();
                return true;
            case R.id.findNearestStation:
                goToNearestStation();
                return true;
            case R.id.sortStations:
                sortStationsByDistance();
                return true;
            case R.id.sortStationsByCityName:
                sortStationsByCityName();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reloadStations() {
        if (isConnected(getApplicationContext())) {
            StationList.setRequestForUpdatingStations();
            loaderManager.restartLoader(STATION_LOADER_ID, null, this);
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void goToNearestStation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        NearestStationFinder.askForLocationPermissionIfNoPermission(this, MY_PERMISSION_REQUEST);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        StationList stations = StationList.getStationListInstance(getApplicationContext());
                        LocationSaver locationSaver = new LocationSaver(getApplicationContext());
                        Location location;
                        if (task.isSuccessful() && task.getResult() != null) {
                            location = task.getResult();
                            locationSaver.saveLocation(location);
                        } else {
                            location = locationSaver.getLocation();
                        }
                        Integer nearestStationId = NearestStationFinder.findNearestStation
                                (location.getLatitude(), location.getLongitude(), stations.getStations());

                        Intent intent = new Intent(getApplicationContext(), SingleStationActivity.class);
                        intent.putExtra("StationId", nearestStationId);
                        for (Station currentStation : stations.getStations()) {
                            if (Integer.parseInt(currentStation.getId()) == nearestStationId) {
                                intent.putExtra("StationName", currentStation.getName());
                                break;
                            }
                        }
                        locationSaver.saveLocation(location);
                        startActivity(intent);
                    });
        } else {
            Toaster.toast(R.string.no_location_access);
        }
    }

    private void sortStationsByDistance() {
        StationList.getStationListInstance(this).sortByDistanceAndUpdateAdapter(
                stationAdapter,
                fusedLocationProviderClient,
                this,
                MY_PERMISSION_REQUEST);
    }

    private void sortStationsByCityName() {
        StationList stationListInstance = StationList.getStationListInstance(getApplicationContext());
        stationAdapter.clear();
        stationAdapter.addAll(stationListInstance.getStationsSortedByCityName());
    }
}