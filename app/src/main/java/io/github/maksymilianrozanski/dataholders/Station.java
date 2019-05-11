package io.github.maksymilianrozanski.dataholders;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import android.util.Log;

import io.github.maksymilianrozanski.utility.NearestStationFinder;

public class Station implements Comparable<Station>, Parcelable {

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

    public void setDistanceFromUser(double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public void setDistanceFromUser(double userLatitude, double userLongitude) {
        try {
            this.distanceFromUser = NearestStationFinder.calculateDistance(userLatitude, userLongitude,
                    Double.parseDouble(gegrLat), Double.parseDouble(gegrLon));
        }catch (NumberFormatException e){
            Log.e("Log", "NumberFormatException: " + e + ", Station name: " + this.name + ", Station id: " + this.getId());
            this.distanceFromUser = Double.MAX_VALUE;
        }
    }

    @Override
    public int compareTo(@NonNull Station station) {
        if (station.getDistanceFromUser() < this.getDistanceFromUser()) {
            return 1;
        } else return -1;
    }

    private Station(Parcel in) {
        String[] data = new String[7];
        in.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
        this.gegrLat = data[2];
        this.gegrLon = data[3];
        this.cityId = data[4];
        this.cityName = data[5];
        this.distanceFromUser = Double.parseDouble(data[6]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{
                this.id,
                this.name,
                this.gegrLat,
                this.gegrLon,
                this.cityId,
                this.cityName,
                Double.toString(this.distanceFromUser)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new Station(parcel);
        }

        @Override
        public Object[] newArray(int size) {
            return new Station[size];
        }
    };
}
