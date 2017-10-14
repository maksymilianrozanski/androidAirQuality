package layout;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.dataholders.Station;
import com.example.android.airquality.utility.QueryStationSensors;
import com.example.android.airquality.utility.QueryStationsList;
import com.example.android.airquality.vieweditors.SensorAdapter;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
//TODO: add Loader...
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        List<Station> stationList = QueryStationsList.fetchStationDataFromSharedPreferences(context);
        Station station = stationList.get(0);

        List<Sensor> sensors = QueryStationSensors.fetchSensorData(Integer.parseInt(station.getId()), context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.widgetStationName, station.getName());

        Sensor sensorWithHighestValue = getSensorWithHighestValue(sensors);
        String measuredParam = sensorWithHighestValue.getParam();
        double percentOfMaxValue = SensorAdapter.percentOfMaxValue(sensorWithHighestValue);
        String percentOfMaxValueString = String.format("%.0f", percentOfMaxValue);
        StringBuffer measuredParamAndItsPercentValue = new StringBuffer();
        measuredParamAndItsPercentValue.append(measuredParam).append(": ").append(percentOfMaxValueString);

        views.setTextViewText(R.id.widgetNameAndValueOfParam, measuredParamAndItsPercentValue);

        String dateOfLastMeasurement = sensorWithHighestValue.getLastDate();
        views.setTextViewText(R.id.widgetUpdateDate, dateOfLastMeasurement);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static Sensor getSensorWithHighestValue(List<Sensor> sensors){
        if (sensors.size()==1) return sensors.get(0);
        double highestValue = Double.MIN_VALUE;
        Sensor sensorHighestCalculatedValue = sensors.get(0);
        for (int i = 1 ; i < sensors.size(); i++){
            double calculatedValue = SensorAdapter.percentOfMaxValue(sensors.get(i));
            if (calculatedValue > highestValue){
                sensorHighestCalculatedValue = sensors.get(i);
            }
        }
        return sensorHighestCalculatedValue;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

