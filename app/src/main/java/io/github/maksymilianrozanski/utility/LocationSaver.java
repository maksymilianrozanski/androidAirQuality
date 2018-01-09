package io.github.maksymilianrozanski.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.VisibleForTesting;

public class LocationSaver {

    private Context context;
    @VisibleForTesting
    static final String sharedPreferencesString = "io.github.maksymilianrozanski";
    @VisibleForTesting
    static final String latitudeKey = "LatitudeKey";
    @VisibleForTesting
    static final String longitudeKey = "LongitudeKey";
    private static final String defaultLatitude = "0";
    private static final String defaultLongitude = "0";


    public LocationSaver(Context context) {
        this.context = context;
    }

    public void saveLocation(Location location){
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesString, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(latitudeKey, String.valueOf(location.getLatitude()));
        editor.putString(longitudeKey, String.valueOf(location.getLongitude()));
        editor.commit();
    }

    public Location getLocation(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesString, Context.MODE_PRIVATE);
        String latitude = sharedPreferences.getString(latitudeKey, defaultLatitude);
        String longitude = sharedPreferences.getString(longitudeKey,defaultLongitude);

        Location location = new Location("savedLocation");
        location.setLatitude(Double.parseDouble(latitude));
        location.setLongitude(Double.parseDouble(longitude));
        return location;
    }
}
