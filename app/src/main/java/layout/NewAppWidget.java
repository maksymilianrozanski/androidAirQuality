package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.utility.WidgetUpdateService;
import com.example.android.airquality.vieweditors.SensorAdapter;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
//TODO: add Loader...

    private Loader cursorLoader;
    private int LOADER_ID = 0;
    public static final String ACTION_RESP = "com.mamlambo.intent.action.MESSAGE_PROCESSED";
    public static final String MY_ACTION = "myAction";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        List<Station> stationList = QueryStationsList.fetchStationDataFromSharedPreferences(context);
//        Station station = stationList.get(0);

//        List<Sensor> sensors = QueryStationSensors.fetchSensorData(Integer.parseInt(station.getId()), context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        Intent msgIntent = new Intent(context, NewAppWidget.class);
        msgIntent.setAction(MY_ACTION);
        msgIntent.putExtra(WidgetUpdateService.PARAM_IN_MSG, "Example text from widget");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, msgIntent, 0);
        views.setOnClickPendingIntent(R.id.widgetStationName, pendingIntent);


//        views.setTextViewText(R.id.widgetStationName, station.getName());


//        Sensor sensorWithHighestValue = getSensorWithHighestValue(sensors);
//        String measuredParam = sensorWithHighestValue.getParam();
//        double percentOfMaxValue = SensorAdapter.percentOfMaxValue(sensorWithHighestValue);
//        String percentOfMaxValueString = String.format("%.0f", percentOfMaxValue);
//        StringBuffer measuredParamAndItsPercentValue = new StringBuffer();
//        measuredParamAndItsPercentValue.append(measuredParam).append(": ").append(percentOfMaxValueString);
//
//        views.setTextViewText(R.id.widgetNameAndValueOfParam, measuredParamAndItsPercentValue);
//
//        String dateOfLastMeasurement = sensorWithHighestValue.getLastDate();
//        views.setTextViewText(R.id.widgetUpdateDate, dateOfLastMeasurement);

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
        Log.v("LOG", "Inside onUpdate....");

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

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(MY_ACTION)) {
            Log.v("LOG", "Inside onReceive");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            String textFromIntent = intent.getStringExtra(WidgetUpdateService.PARAM_OUT_MSG);
            views.setTextViewText(R.id.widgetStationName, textFromIntent);
            Log.v("LOG", "text from intent: " + textFromIntent);
        }
    }
}

