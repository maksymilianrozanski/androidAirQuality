package com.example.android.airquality.dataholders;

import android.support.annotation.NonNull;

import com.example.android.airquality.utility.NearestStationFinder;

/**
 * Created by Max on 16.08.2017.
 */

public class Station implements Comparable<Station>{

    private String id;
    private String name;
    private String gegrLat;
    private String gegrLon;
    private String cityId;
    private String cityName;
    private double distanceFromUser;

    public Station() {
    }

    public Station(String id, String name, String gegrLat, String gegrLon, String cityId, String cityName) {
        this.id = id;
        this.name = name;
        this.gegrLat = gegrLat;
        this.gegrLon = gegrLon;
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGegrLat() {
        return gegrLat;
    }

    public void setGegrLat(String gegrLat) {
        this.gegrLat = gegrLat;
    }

    public String getGegrLon() {
        return gegrLon;
    }

    public void setGegrLon(String gegrLon) {
        this.gegrLon = gegrLon;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(double userLatitude, double userLongitude) {
        this.distanceFromUser = NearestStationFinder.calculateDistance(userLatitude, userLongitude,
                Double.parseDouble(gegrLat), Double.parseDouble(gegrLon));
    }

    @Override
    public int compareTo(@NonNull Station station) {
        if (station.getDistanceFromUser() < this.getDistanceFromUser()){
            return 1;
        }else return -1;
    }
}
