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
        double userDistance = 9999999;
        Integer closestStationId = null;
        for (Station station : stations) {
            double stationLatitude = Double.parseDouble(station.getGegrLat());
            double stationLongitude = Double.parseDouble(station.getGegrLon());
            double currentDistance = calculateDistance(userLatitude, userLongitude, stationLatitude, stationLongitude);
            if (currentDistance < userDistance){
                closestStationId = Integer.parseInt(station.getId());
            }
        }
        return closestStationId;
    }

    /**
     * Calculate distance between user and station using Haversine method, height is omitted
     * @param userLatitude - user's position latitude
     * @param userLongitude - user's position longitude
     * @param stationLatitude - station's latitude
     * @param stationLongitude - station's longitude
     * @return  distance in meters
     */
    private static double calculateDistance(double userLatitude, double userLongitude,
                                            double stationLatitude, double stationLongitude) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(stationLatitude - userLatitude);
        double lonDistance = Math.toRadians(stationLongitude - userLongitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(stationLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance); //distance in meters
    }
}
