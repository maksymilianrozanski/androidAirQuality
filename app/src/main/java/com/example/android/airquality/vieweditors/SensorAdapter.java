package com.example.android.airquality.vieweditors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.airquality.R;
import com.example.android.airquality.dataholders.Sensor;

import java.util.List;

/**
 * Created by Max on 19.08.2017.
 */

public class SensorAdapter extends ArrayAdapter<Sensor> {

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

        //find sensor at position on the list
        Sensor currentSensor = getItem(position);

        //find TextView that display param that sensor measure
        TextView sensorTypeView = (TextView) listItemView.findViewById(R.id.sensorType);
        String sensorType;

        //check if sensorType in not null, add sensorType to TextView
        try {
            sensorType = currentSensor.getParam();
        } catch (NullPointerException e) {
            sensorType = "not specified";
        }
        sensorTypeView.setText(sensorType);

        //find TextView that display value of measured param
        TextView paramValueView = (TextView) listItemView.findViewById(R.id.paramValue);
        double paramValue;

        //check if paramValue is not null, add value to TextView
        try {
            paramValue = currentSensor.getValue();
        } catch (NullPointerException e) {
            paramValue = 0;
        }
        String paramValueString = String.format("%.2f", paramValue);
        paramValueView.setText(paramValueString);

        //find TextView that display date of measurement
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        String date;

        //check if date is not null, add value to TextView
        try {
            date = currentSensor.getLastDate();
        }catch (NullPointerException e){
            date = "error occurred";
        }
        dateView.setText(date);
        return listItemView;
    }
}
