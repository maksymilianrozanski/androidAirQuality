package com.example.android.airquality.dataholders;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StationTest {


    private Station stationCloser;
    private Station stationFarther;

    @Before
    public void initialize() {
        stationCloser = new Station(
                "100", "Warszawa - centrum",
                "52.229676", "21.012229", "123", "Warszawa");
        stationCloser.setDistanceFromUser(5000.35);

        stationFarther = new Station(
                "100", "Warszawa - centrum",
                "52.229676", "21.012229", "123", "Warszawa");
        stationFarther.setDistanceFromUser(7500.35);
    }

    @Test
    public void comparableImplTest(){
       assertEquals(-1, stationCloser.compareTo(stationFarther));
       assertEquals(1, stationFarther.compareTo(stationCloser));
    }

}
