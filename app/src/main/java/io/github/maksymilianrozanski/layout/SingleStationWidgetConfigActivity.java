package io.github.maksymilianrozanski.layout;

import android.app.Activity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Station;
import io.github.maksymilianrozanski.dataholders.StationList;
import io.github.maksymilianrozanski.vieweditors.StationAdapterRecyclerWidgetConfig;
import io.github.maksymilianrozanski.vieweditors.StationLoader;

public class SingleStationWidgetConfigActivity extends Activity implements LoaderManager.LoaderCallbacks<List<Station>>, View.OnClickListener {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int STATION_LOADER_ID = 1;
    public static final String SHARED_PREF_KEY_WIDGET = "io.github.maksymilianrozanski.singleStationWidget";

    private static final int MY_PERMISSION_REQUEST = 0;
    private StationAdapterRecyclerWidgetConfig stationAdapterRecycler;
    LoaderManager loaderManager = getLoaderManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_station_widget_config_activity);

        assignAppWidgetId();

        RecyclerView.LayoutManager stationsLayoutManager = new LinearLayoutManager(this);
        stationAdapterRecycler = new StationAdapterRecyclerWidgetConfig(this, getApplicationContext(), appWidgetId, new ArrayList<>());
        RecyclerView recyclerView = findViewById(R.id.station_list_widget_config);
        recyclerView.setLayoutManager(stationsLayoutManager);
        recyclerView.setAdapter(stationAdapterRecycler);

        loaderManager.initLoader(STATION_LOADER_ID, null, this);
        setButtons();
    }

    private void assignAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    @Override
    public Loader<List<Station>> onCreateLoader(int i, Bundle bundle) {
        return new StationLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Station>> loader, List<Station> data) {
        if (data != null && !data.isEmpty()) {
            stationAdapterRecycler.setData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Station>> loader) {
        stationAdapterRecycler.setData(new ArrayList<>());
    }

    private void setButtons() {
        Button sortByCityName = (Button) findViewById(R.id.sortStationsByCityNameSingleWidgetConfig);
        sortByCityName.setOnClickListener(this);
        Button sortByDistance = (Button) findViewById(R.id.sortStationsByDistanceSingleWidgetConfig);
        sortByDistance.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sortStationsByCityNameSingleWidgetConfig:
                sortByCityName();
                break;
            case R.id.sortStationsByDistanceSingleWidgetConfig:
                sortByDistance();
                break;
        }
    }

    private void sortByCityName() {
        StationList stationList = StationList.getStationListInstance(getApplicationContext());
        stationAdapterRecycler.setData(stationList.getStationsSortedByCityName());
    }

    private void sortByDistance() {
        StationList.getStationListInstance(this).sortByDistanceAndUpdateAdapter(
                stationAdapterRecycler,
                this,
                MY_PERMISSION_REQUEST
        );
    }
}
