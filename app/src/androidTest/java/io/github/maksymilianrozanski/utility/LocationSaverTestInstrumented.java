package io.github.maksymilianrozanski.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;

@RunWith(AndroidJUnit4.class)
public class LocationSaverTestInstrumented  {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private Context context;

    @Before
    public void before() throws Exception {
        sharedPrefs = Mockito.mock(SharedPreferences.class);
        context = Mockito.mock(Context.class);
        editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(context.getSharedPreferences(matches(LocationSaver.sharedPreferencesString), anyInt())).thenReturn(sharedPrefs);
        Mockito.when(sharedPrefs.edit()).thenReturn(editor);
    }

    @Test
    public void saveLocationTest() throws Exception {
        double exampleLatitude = 30d;
        double exampleLongitude = 40d;

        Location exampleLocation = new Location("locationToSave");
        exampleLocation.setLatitude(exampleLatitude);
        exampleLocation.setLongitude(exampleLongitude);

        LocationSaver locationSaver = new LocationSaver(context);
        locationSaver.saveLocation(exampleLocation);
        Mockito.verify(editor, Mockito.times(1)).putString(LocationSaver.latitudeKey, String.valueOf(exampleLatitude));
        Mockito.verify(editor, Mockito.times(1)).putString(LocationSaver.longitudeKey, String.valueOf(exampleLongitude));
        Mockito.verify(editor, Mockito.times(1)).commit();
    }

    @Test
    public void getLocationTest() throws Exception {
        String exampleLatitude = "31.0";
        String exampleLongitude = "32.0";

        Mockito.when(sharedPrefs.getString(matches(LocationSaver.latitudeKey), anyString())).thenReturn(exampleLatitude);
        Mockito.when(sharedPrefs.getString(matches(LocationSaver.longitudeKey), anyString())).thenReturn(exampleLongitude);

        LocationSaver locationSaver = new LocationSaver(context);
        Location receivedLocation = locationSaver.getLocation();
        Assert.assertTrue(receivedLocation.getLatitude() == Double.parseDouble(exampleLatitude));
        Assert.assertTrue(receivedLocation.getLongitude() == Double.parseDouble(exampleLongitude));
    }
}
