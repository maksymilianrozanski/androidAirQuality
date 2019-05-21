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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Station;
import io.github.maksymilianrozanski.dataholders.StationList;
import io.github.maksymilianrozanski.utility.LocationSaver;
import io.github.maksymilianrozanski.utility.NearestStationFinder;
import io.github.maksymilianrozanski.vieweditors.StationAdapterRecycler;
import io.github.maksymilianrozanski.vieweditors.StationLoader;
import xdroid.toaster.Toaster;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Station>> {

    //id of loader, only matter when multiple loaders
    private static final int STATION_LOADER_ID = 1;
    private static final int LOCATION_PERMISSION_CALLED_BY_FIND_NEAREST_STATION = 0;
    private static final int LOCATION_PERMISSION_CALLED_BY_SORT_STATIONS = 1;

    private StationAdapterRecycler stationAdapterRecycler;
    private AtomicBoolean isStationListLoaded = new AtomicBoolean(false);

    LoaderManager loaderManager = getLoaderManager();

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView.LayoutManager stationsLayoutManager = new LinearLayoutManager(this);
        stationAdapterRecycler = new StationAdapterRecycler(this, new ArrayList<>());
        RecyclerView recyclerView = findViewById(R.id.stationsRecyclerView);
        recyclerView.setLayoutManager(stationsLayoutManager);
        recyclerView.setAdapter(stationAdapterRecycler);

        loaderManager.initLoader(STATION_LOADER_ID, null, this);

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
        isStationListLoaded.set(false);
        return new StationLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Station>> loader, List<Station> data) {
        if (data != null && !data.isEmpty()) {
            stationAdapterRecycler.setData(data);
            isStationListLoaded.set(true);
        }
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshMainActivity);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Station>> loader) {
        isStationListLoaded.set(false);
        stationAdapterRecycler.setData(new ArrayList<>());
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork;
        try {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        } catch (NullPointerException e) {

            return false;
        }
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    //show three dot menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.findNearestStation).setEnabled(isStationListLoaded.get());
        menu.findItem(R.id.sortStations).setEnabled(isStationListLoaded.get());
        menu.findItem(R.id.sortStationsByCityName).setEnabled(isStationListLoaded.get());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.findNearestStation:
                goToNearestStation();
                return true;
            case R.id.sortStations:
                sortStationsByDistance();
                return true;
            case R.id.sortStationsByCityName:
                sortStationsByCityName();
                return true;
            case R.id.reload:
                reloadStations();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CALLED_BY_FIND_NEAREST_STATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToNearestStation();
                } else {
                    Toaster.toast(R.string.no_location_access);
                }

            case LOCATION_PERMISSION_CALLED_BY_SORT_STATIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sortStationsByDistance();
                } else {
                    Toaster.toast(R.string.no_location_access);
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @VisibleForTesting
    public void goToNearestStation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
            NearestStationFinder.askForLocationPermissionIfNoPermission(this, LOCATION_PERMISSION_CALLED_BY_FIND_NEAREST_STATION);
        }
    }

    private void sortStationsByDistance() {
        StationList.getStationListInstance(this).sortByDistanceAndUpdateAdapter(
                stationAdapterRecycler,
                this,
                LOCATION_PERMISSION_CALLED_BY_SORT_STATIONS);
    }

    private void sortStationsByCityName() {
        StationList stationListInstance = StationList.getStationListInstance(getApplicationContext());
        stationAdapterRecycler.setData(stationListInstance.getStationsSortedByCityName());
    }
}