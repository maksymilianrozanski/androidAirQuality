package io.github.maksymilianrozanski.dataholders;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

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

        assertTrue(station.getId().equals(stationCreatedFromParcel.getId()));
        assertTrue(station.getName().equals(stationCreatedFromParcel.getName()));
        assertTrue(station.getGegrLat().equals(stationCreatedFromParcel.getGegrLat()));
        assertTrue(station.getGegrLon().equals(stationCreatedFromParcel.getGegrLon()));
        assertTrue(station.getCityId().equals(stationCreatedFromParcel.getCityId()));
        assertTrue(station.getCityName().equals(stationCreatedFromParcel.getCityName()));
        assertTrue(station.getDistanceFromUser() == stationCreatedFromParcel.getDistanceFromUser());
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
