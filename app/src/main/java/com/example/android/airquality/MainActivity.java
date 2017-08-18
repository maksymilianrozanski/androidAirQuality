package com.example.android.airquality;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Station>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    //url for data - list of stations
    private static String urlQuery = "http://api.gios.gov.pl/pjp-api/rest/station/findAll";

    //id of loader, only matter when multiple loaders
    private static final int STATION_LOADER_ID = 1;

    //adapter for list of stations
    private StationAdapter stationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find reference to ListView in the layout
        ListView stationListView = (ListView) findViewById(R.id.list);

        //create new adapter, that takes empty book list as input
        stationAdapter = new StationAdapter(this, new ArrayList<Station>());

        //set the adapter, the list can be populated in the user's interface
        stationListView.setAdapter(stationAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(STATION_LOADER_ID, null, this);

        //TODO: make toast when data isn't loaded properly
        // refresh button
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener((View v) -> {
            Log.v("Info", "Refresh button pressed");
            //remove default view showing start info
            TextView defaultItemTextView = (TextView) findViewById(R.id.on_start_text_view);
            defaultItemTextView.setVisibility(View.GONE);
            if (this.isConnected(getApplicationContext())) {
                Log.v("Info", "Connected to the internet");
                loaderManager.restartLoader(STATION_LOADER_ID, null, this);
            }else {
                Log.v("info", "No Internet connection");
                Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Loader<List<Station>> onCreateLoader(int id, Bundle args) {
        //create a new loader for given URL
        return new StationLoader(this, urlQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<Station>> loader, List<Station> data) {
        Log.v("Info", "Inside onLoaderFinished - start");
        //clear adapter of previous search data
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
        // Loader reset, so we can clear out our existing data.
        stationAdapter.clear();
        Log.v("Info", "Inside onLoaderReset - after .clear");
    }

    //for checking is device connected to the Internet
    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
