package io.github.maksymilianrozanski.vieweditors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.dataholders.Station;


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

        Station currentStation = getItem(position);
        setStationNameViewText(listItemView, currentStation);
        setCityNameViewText(listItemView, currentStation);
        return listItemView;
    }

    private void setStationNameViewText(View listItemView, Station station){
        TextView stationNameView = (TextView) listItemView.findViewById(R.id.stationname);
        String stationName;
        try {
            stationName = station.getName();
        }catch (NullPointerException e){
            stationName = "not specified";
        }
        stationNameView.setText(stationName);
    }

    private void setCityNameViewText(View listItemView, Station station){
        TextView cityNameView = (TextView) listItemView.findViewById(R.id.cityname);
        String cityName;
        try {
            cityName = station.getCityName();
        }catch (NullPointerException e){
            cityName = "not specified";
        }
        cityNameView.setText(cityName);
    }
}

