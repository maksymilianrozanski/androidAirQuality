package com.example.android.airquality.utility;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

import com.example.android.airquality.dataholders.Sensor;

import java.util.List;

/**
 * Created by Max on 23.09.2017.
 */

public class WidgetLoader implements LoaderManager.LoaderCallbacks<List<Sensor>>{



    @Override
    public Loader<List<Sensor>> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Sensor>> loader, List<Sensor> sensors) {

    }

    @Override
    public void onLoaderReset(Loader<List<Sensor>> loader) {

    }
}
