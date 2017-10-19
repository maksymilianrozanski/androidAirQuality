package com.example.android.airquality.vieweditors;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;
import com.example.android.airquality.main.MainActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.airquality.R.id.sensorType;

/**
 * Created by Max on 19.08.2017.
 */

public class SensorAdapter extends ArrayAdapter<Sensor> {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final Map<String, Integer> MAX_CONCENTRATIONS;

    public SensorAdapter(@NonNull Context context, List<Sensor> sensors) {
        super(context, 0, sensors);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.sensor_list_item, parent, false);
        }

        Sensor currentSensor = getItem(position);

        TextView sensorTypeView = (TextView) listItemView.findViewById(sensorType);
        String sensorType;

        try {
            sensorType = currentSensor.getParam();
        } catch (NullPointerException e) {
            sensorType = "not specified";
        }
        sensorTypeView.setText(sensorType);

        TextView paramValueView = (TextView) listItemView.findViewById(R.id.paramValue);
        setParamValueViewText(paramValueView, currentSensor);

        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        String date;
        try {
            date = currentSensor.getLastDate();
        } catch (NullPointerException e) {
            date = "error occurred";
        }
        dateView.setText(date);

        TextView percentView = (TextView) listItemView.findViewById(R.id.percentValue);
        try {
            double calculationResult = percentOfMaxValue(currentSensor);
            percentView.setText(String.format("%.0f", calculationResult) + "%");

            //set color of percentView background, if exception above is thrown,
            // this part is skipped and background remains grey
            GradientDrawable percentViewBackground = (GradientDrawable) percentView.getBackground();
            int chosenColor = chooseColorOfBackground(calculationResult, getContext());
            percentViewBackground.setColor(chosenColor);

        } catch (NullPointerException | NumberFormatException e) {
            percentView.setText("-");
        }
        return listItemView;
    }

    private void setParamValueViewText(TextView paramValueViewText, Sensor sensor){
        double paramValue;
        try {
            paramValue = sensor.getValue();
        } catch (NullPointerException e) {
            paramValue = 0;
        }
        String paramValueString = String.format("%.2f", paramValue);
        String sensorType = sensor.getParam();
        if (!sensorType.equals("not specified")) {
            String textToAdd = paramValueString;
            Integer maxValue = MAX_CONCENTRATIONS.get(sensorType);
            textToAdd = textToAdd + "/" + maxValue + " μg/m³";
            paramValueViewText.setText(textToAdd);
        }
    }

    public static double percentOfMaxValue(Sensor sensor) {
        double percentOfMaxValue;
        try {
            percentOfMaxValue = (sensor.getValue() / MAX_CONCENTRATIONS.get(sensor.getParam())*100);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Number format exception " + e);
            throw e;
        }
        return percentOfMaxValue;
    }

    public static int chooseColorOfBackground(double percentValue, Context context) {
        int percentValueInt = Integer.parseInt(String.format("%.0f", percentValue));
        if (percentValueInt <= 25) {
            return ContextCompat.getColor(context, R.color.qualityColor1);
        } else if (percentValueInt <= 50) {
            return ContextCompat.getColor(context, R.color.qualityColor2);
        } else if (percentValueInt <= 75) {
            return ContextCompat.getColor(context, R.color.qualityColor3);
        } else if (percentValueInt <= 100) {
            return ContextCompat.getColor(context, R.color.qualityColor4);
        } else if (percentValueInt <= 150) {
            return ContextCompat.getColor(context, R.color.qualityColor5);
        } else if (percentValueInt <= 300) {
            return ContextCompat.getColor(context, R.color.qualityColor6);
        } else if (percentValueInt <= 500) {
            return ContextCompat.getColor(context, R.color.qualityColor7);
        } else return ContextCompat.getColor(context, R.color.qualityColor8);
    }


    //set maximum acceptable values
    //http://powietrze.gios.gov.pl/pjp/content/annual_assessment_air_acceptable_level
    static {
        Map<String, Integer> maxConcentrationsMap = new HashMap<String, Integer>();
        maxConcentrationsMap.put("C6H6", 5);
        maxConcentrationsMap.put("NO2", 200);
        maxConcentrationsMap.put("SO2", 125);
        maxConcentrationsMap.put("PM10", 50);
        maxConcentrationsMap.put("CO", 10000);
        maxConcentrationsMap.put("PM2.5", 25);
        maxConcentrationsMap.put("O3", 120);
        MAX_CONCENTRATIONS = Collections.unmodifiableMap(maxConcentrationsMap);
    }
}
