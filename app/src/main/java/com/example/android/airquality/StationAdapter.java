package com.example.android.airquality;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Max on 16.08.2017.
 */

public class StationAdapter extends ArrayAdapter<Station> {

    public StationAdapter(@NonNull Context context, List<Station> stations) {
        super(context, 0, stations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.station_list_item, parent, false);
        }

        //find station at position on the list
        Station currentStation = getItem(position);

        //set station name in TextView
        TextView stationNameView = (TextView) listItemView.findViewById(R.id.stationname);
        String stationName;
        //check if stationName is not null
        try {
            stationName = currentStation.getName();
        }catch (NullPointerException e){
            stationName = "not specified";
        }
        stationNameView.setText(stationName);

        //set name of the city in TextView
        TextView cityNameView = (TextView) listItemView.findViewById(R.id.cityname);
        String cityName = currentStation.getCityName();
        cityNameView.setText(cityName);

        return listItemView;
    }
}

