package io.github.maksymilianrozanski.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.vieweditors.SensorAdapter;

class ListProvider implements
        RemoteViewsService.RemoteViewsFactory {

    private ArrayList<WidgetItem> listItemList = new ArrayList<>();
    private Context context;

    @SuppressWarnings("unchecked")
    ListProvider(Context context, ArrayList<WidgetItem> listItems) {
        this.context = context;
        if (listItems != null) {

            this.listItemList = (ArrayList<WidgetItem>) listItems.clone();
        }
    }

    @Override
    public int getCount() {
        if (listItemList != null) {
            return listItemList.size();
        } else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews rv = new RemoteViews
                (context.getPackageName(), R.layout.multiple_station_widget_list_item);
        WidgetItem widgetItem = listItemList.get(position);

        rv.setTextViewText(R.id.widgetStationNameListItem, widgetItem.getStationName());
        rv.setTextViewText(R.id.widgetNameAndValueOfParam, widgetItem.getNameAndValueOfParam());

        int colorOfValueBackground = SensorAdapter.chooseColorOfBackground
                (cutStringToDoublePercentValue(widgetItem.getNameAndValueOfParam()), context);
        rv.setInt(R.id.widgetNameAndValueOfParam, "setBackgroundColor", colorOfValueBackground);
        rv.setTextViewText(R.id.widgetUpdateDate, widgetItem.getUpdateDate());

        if (widgetItem.isUpToDate()) {
            rv.setInt(R.id.widgetStationNameListItem, "setBackgroundColor", ContextCompat.getColor(context, R.color.white));
        } else {
            rv.setInt(R.id.widgetStationNameListItem, "setBackgroundColor", ContextCompat.getColor(context, R.color.noData));
        }

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("StationId", widgetItem.getStationId());
        fillInIntent.putExtra("StationName", widgetItem.getStationName());
        rv.setOnClickFillInIntent(R.id.multiple_station_list_item, fillInIntent);

        return rv;
    }

    private double cutStringToDoublePercentValue(String nameAndValueOfParam) {
        try {
            String parts[] = nameAndValueOfParam.split(" ");
            String secondPart = parts[1];
            String percentValue = secondPart.substring(0, secondPart.length() - 1);
            return Double.parseDouble(percentValue);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return -1;
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        listItemList = MultipleStationWidgetUpdateService
                .getWidgetItemListFromSharedPreferences(context);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
