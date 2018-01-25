package io.github.maksymilianrozanski.vieweditors;

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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Sensor;
import io.github.maksymilianrozanski.main.MainActivity;

import static io.github.maksymilianrozanski.R.id.sensorType;

public class SensorAdapter extends ArrayAdapter<Sensor> {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final Map<String, Integer> MAX_CONCENTRATIONS;
    private static int acceptableDelayInHours = 5;

    public SensorAdapter(@NonNull Context context, List<Sensor> sensors) {
        super(context, 0, sensors);
    }

    public static Map<String, Integer> getMaxConcentrations() {
        return MAX_CONCENTRATIONS;
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

        setSensorTypeViewText(listItemView, currentSensor);
        setParamValueViewText(listItemView, currentSensor);
        setDateViewText(listItemView, currentSensor);
        setDateViewBackgroundColor(listItemView, currentSensor);
        setPercentViewText(listItemView, currentSensor);

        return listItemView;
    }

    private void setSensorTypeViewText(View listItemView, Sensor sensor) {
        TextView sensorTypeView = (TextView) listItemView.findViewById(sensorType);
        String sensorType;
        try {
            sensorType = sensor.getParam();
        } catch (NullPointerException e) {
            sensorType = "not specified";
        }
        sensorTypeView.setText(sensorType);
    }

    private void setParamValueViewText(View listItemView, Sensor sensor) {
        TextView paramValueView = (TextView) listItemView.findViewById(R.id.paramValue);
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
            paramValueView.setText(textToAdd);
        }
    }

    private void setDateViewText(View listItemView, Sensor sensor) {
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        String date;
        try {
            date = sensor.getLastDate();
        } catch (NullPointerException e) {
            date = "error occurred";
        }
        dateView.setText(date);
    }

    private void setDateViewBackgroundColor(View listItemView, Sensor sensor) {
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        setDateTextViewBackgroundColor(dateView, sensor);
    }

    private void setDateTextViewBackgroundColor(TextView dateView, Sensor sensor) {
        Calendar calendar = new GregorianCalendar();
        long oldestAcceptableTime = (calendar.getTimeInMillis() - (acceptableDelayInHours * 3600000L));

        long sensorTime;
        try {
            sensorTime = sensor.getTimeInMillis();
        } catch (ParseException e) {
            sensorTime = 1;
        }
        if (sensorTime <= oldestAcceptableTime) {
            int greyColor = ContextCompat.getColor(getContext(), R.color.noData);
            Log.d(LOG_TAG, "setting background to grey: " + greyColor);
            dateView.setBackgroundColor(greyColor);
        } else {
            int whiteColor = ContextCompat.getColor(getContext(), R.color.white);
            Log.d(LOG_TAG, "setting background to white: " + whiteColor);
            dateView.setBackgroundColor(whiteColor);
        }
    }

    private void setPercentViewText(View listItemView, Sensor sensor) {
        TextView percentView = (TextView) listItemView.findViewById(R.id.percentValue);
        try {
            double calculationResult = sensor.percentOfMaxValue();
            percentView.setText(String.format("%.0f", calculationResult) + "%");
            GradientDrawable percentViewBackground = (GradientDrawable) percentView.getBackground();
            int chosenColor = chooseColorOfBackground(calculationResult, getContext());
            percentViewBackground.setColor(chosenColor);
        } catch (NullPointerException | NumberFormatException e) {
            percentView.setText("-");
        }
    }

    public static int chooseColorOfBackground(double percentValue, Context context) {
        int percentValueInt = Integer.parseInt(String.format("%.0f", percentValue));
        if (percentValueInt < 0) {
            return ContextCompat.getColor(context, R.color.noData);
        } else if (percentValueInt <= 25) {
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
