package io.github.maksymilianrozanski.utility;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.github.maksymilianrozanski.dataholders.Station;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class QueryUtilitiesNearestStationFinderTestInstrumented  {
    private double userLatitude;
    private double userLongitude;
    private List<Station> stations;

    @Before
    public void setup() {
        stations = new ArrayList<>();
        Station station1 = new Station("236", "BiałaP-Orzechowa", "52.029194", "23.149389", "26", "Biała Podlaska");
        Station station2 = new Station("605", "Białystok-Miejska", "53.126689", "23.155869", "35", "Białystok");
        Station station3 = new Station("402", "Kraków, ul. Bulwarowa", "50.069308", "20.053492", "415", "Kraków");
        Station station4 = new Station("10435", "Kraków, ul. Telimeny", "50.019036", "20.016822", "415", "Kraków");
        Station station5 = new Station("531", "Warszawa-Podleśna", "52.280939", "20.962156", "1006", "Warszawa");

        stations.add(station1);
        stations.add(station2);
        stations.add(station3);
        stations.add(station4);
        stations.add(station5);
    }

    @Test
    public void findNearestStation1() {
        //coordinates for Kraków - Plac Centralny, nearest to station3
        userLatitude = 50.071879;
        userLongitude = 20.037260;
        int result = NearestStationFinder.findNearestStation(userLatitude, userLongitude, stations);
        int predictedResult = 402;
        assertEquals(predictedResult, result);
    }

    @Test
    public void findNearestStation2() {
        //coordinates for Warszawa - Pałac Kultury, nearest to station5
        userLatitude = 52.231474;
        userLongitude = 21.005868;
        int result = NearestStationFinder.findNearestStation(userLatitude, userLongitude, stations);
        int predictedResult = 531;
        assertEquals(predictedResult, result);
    }
}