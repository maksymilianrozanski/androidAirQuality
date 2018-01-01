package io.github.maksymilianrozanski.utility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;

import io.github.maksymilianrozanski.dataholders.Station;


public class NearestStationFinder {

    public static Integer findNearestStation(double userLatitude, double userLongitude, List<Station> stations) {
        double userDistance = Double.MAX_VALUE;
        Integer closestStationId = null;
        for (Station station : stations) {
            double stationLatitude = Double.parseDouble(station.getGegrLat());
            double stationLongitude = Double.parseDouble(station.getGegrLon());
            double currentDistance = calculateDistance(userLatitude, userLongitude, stationLatitude, stationLongitude);
            if (currentDistance < userDistance) {
                userDistance = currentDistance;
                closestStationId = Integer.parseInt(station.getId());
            }
        }
        return closestStationId;
    }

    /**
     * Calculate distance between user and station using Haversine method, height is omitted
     * @return distance in meters
     */
    public static double calculateDistance(double userLatitude, double userLongitude,
                                            double stationLatitude, double stationLongitude) {
        final int R = 6371000; // Radius of the earth in meters
        double userLatitudeRadian = Math.toRadians(userLatitude);
        double userLongitudeRadian = Math.toRadians(userLongitude);
        double stationLatitudeRadian = Math.toRadians(stationLatitude);
        double stationLongitudeRadian = Math.toRadians(stationLongitude);

        double distance = (2 * R) * (Math.asin(Math.sqrt(
                Math.pow(Math.sin((stationLatitudeRadian - userLatitudeRadian) / 2), 2)
                        + Math.cos(userLatitudeRadian) * Math.cos(stationLatitudeRadian) *
                        Math.pow(Math.sin((stationLongitudeRadian - userLongitudeRadian) / 2), 2)
        )));
        return distance;
    }

    public static boolean askForLocationPermissionIfNoPermission(Activity activity, int permissionRequest ) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequest);
            return false;
        }
        return true;
    }
}
