package io.github.maksymilianrozanski.utility;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.maksymilianrozanski.dataholders.Sensor;
import io.github.maksymilianrozanski.dataholders.SensorList;
import io.github.maksymilianrozanski.dataholders.Station;
import io.github.maksymilianrozanski.dataholders.StationList;
import io.github.maksymilianrozanski.layout.WidgetItem;

public class FetchWidgetItem extends Thread {

    private static final String LOG_TAG = FetchWidgetItem.class.getSimpleName();
    private int indexNumber;
    private Context context;
    private ArrayList<WidgetItem> widgetItems;

    @VisibleForTesting
    public static int timeInHours = 5;

    FetchWidgetItem(int indexNumber, Context context, ArrayList<WidgetItem> widgetItems) {
        this.indexNumber = indexNumber;
        this.context = context;
        this.widgetItems = widgetItems;
    }

    @Override
    public void run() {
        Sensor tempSensor;
        WidgetItem currentWidgetItem;
        AtomicBoolean isWidgetItemUpToDate = new AtomicBoolean(false);
        try {
            tempSensor = fetchSensorWithHighestPercentValue(context, indexNumber, isWidgetItemUpToDate);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fetching data not succeed.");
            return;
        }
        currentWidgetItem = widgetItems.get(indexNumber);
        String paramType = tempSensor.getParam();
        String percentValue = String.format("%.0f", tempSensor.percentOfMaxValue());
        currentWidgetItem.setNameAndValueOfParam(paramType + ": " + percentValue + "%");
        currentWidgetItem.setUpdateDate(removeSecondsFromDate(tempSensor.getLastDate()));
        currentWidgetItem.setUpToDate(isWidgetItemUpToDate);
    }

    private Sensor fetchSensorWithHighestPercentValue(Context context, int stationIndex, AtomicBoolean isUpToDate) throws IOException {
        int stationId = widgetItems.get(stationIndex).getStationId();
        StationList stationList = StationList.getStationListInstance(context);
        Station station = stationList.findStationWithId(stationId);
        QueryStationSensors queryStationSensors = new QueryStationSensors();
        List<Sensor> sensors = queryStationSensors.fetchSensorData(Integer.parseInt(station.getId()));
        SensorList sensorList = new SensorList(sensors);
        int sensorListSize = sensorList.getList().size();
        sensorList.removeSensorsWhereValueOlderThan(timeInHours);
        int sensorListSizeAfterRemovingObsoleteSensors = sensorList.getList().size();
        if (sensorListSize == sensorListSizeAfterRemovingObsoleteSensors) {
            isUpToDate.set(true);
        }
        return sensorList.getSensorWithHighestValue();
    }

    private String removeSecondsFromDate(String notFormattedDate) {
        int notFormattedDateLength = notFormattedDate.length();
        return notFormattedDate.substring(0, notFormattedDateLength - 3);
    }
}
