package io.github.maksymilianrozanski.dataholders;

import android.os.Parcel;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class StationTestInstrumented {

    @Test
    public void parcelableImplementationTest(){
        Station station = new Station(
                "100", "Warszawa - centrum",
                "52.229676", "21.012229", "123", "Warszawa");
        station.setDistanceFromUser(5000.35);

        Parcel parcel = Parcel.obtain();

        station.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Station stationCreatedFromParcel = (Station) Station.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(station.getId(), stationCreatedFromParcel.getId());
        Assert.assertEquals(station.getName(), stationCreatedFromParcel.getName());
        Assert.assertEquals(station.getGegrLat(), stationCreatedFromParcel.getGegrLat());
        Assert.assertEquals(station.getGegrLon(), stationCreatedFromParcel.getGegrLon());
        Assert.assertEquals(station.getCityId(), stationCreatedFromParcel.getCityId());
        Assert.assertEquals(station.getCityName(), stationCreatedFromParcel.getCityName());
        Assert.assertEquals(station.getDistanceFromUser(), stationCreatedFromParcel.getDistanceFromUser(), 0.0);
    }

    @Test
    public void comparableImplTest() {
        Station stationCloser = new Station(
                "100", "Warszawa - centrum",
                "52.229676", "21.012229", "123", "Warszawa");
        stationCloser.setDistanceFromUser(5000.35);

        Station stationFarther = new Station(
                "100", "Warszawa - centrum",
                "52.229676", "21.012229", "123", "Warszawa");
        stationFarther.setDistanceFromUser(7500.35);

        assertEquals(-1, stationCloser.compareTo(stationFarther));
        assertEquals(1, stationFarther.compareTo(stationCloser));
    }

}
