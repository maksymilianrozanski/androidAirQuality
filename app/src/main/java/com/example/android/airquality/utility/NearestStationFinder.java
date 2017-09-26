package com.example.android.airquality.utility;

import com.example.android.airquality.dataholders.Station;

import java.util.List;

/**
 * Created by Max on 21.09.2017.
 */

public class NearestStationFinder {

    /**
     * @param stations List of stations with location data (latitude and longitude)
     * @return id of station which is located the closest to user's location
     */
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
     * @param userLatitude     - user's position latitude
     * @param userLongitude    - user's position longitude
     * @param stationLatitude  - station's latitude
     * @param stationLongitude - station's longitude
     * @return distance in meters
     */
    private static double calculateDistance(double userLatitude, double userLongitude,
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
}
