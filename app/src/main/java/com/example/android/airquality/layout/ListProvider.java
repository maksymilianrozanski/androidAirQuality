package com.example.android.airquality.layout;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.airquality.R;
import com.example.android.airquality.vieweditors.SensorAdapter;

import java.util.ArrayList;

class ListProvider implements
        RemoteViewsService.RemoteViewsFactory {

    private ArrayList<WidgetItem> listItemList = new ArrayList<>();
    private Context context = null;

    ListProvider(Context context, ArrayList<WidgetItem> listItems) {
        this.context = context;
        if (listItems!=null){
            this.listItemList = (ArrayList<WidgetItem>) listItems.clone();
        }
    }

    @Override
    public int getCount () {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Given the position (index) of a WidgetItem in the array, use the item's text value in
    // combination with the app widget item XML file to construct a RemoteViews object.
    @Override
    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.

        // Construct a remote views item based on the app widget item XML file,
        // and set the text based on the position.
        final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.multiple_station_widget_list_item);
        WidgetItem widgetItem = listItemList.get(position);

        rv.setTextViewText(R.id.widgetStationName, widgetItem.getStationName());
        rv.setTextViewText(R.id.widgetNameAndValueOfParam, widgetItem.getNameAndValueOfParam());

        int colorOfValueBackground = SensorAdapter.chooseColorOfBackground
                (cutStringToDoublePercentValue(widgetItem.getNameAndValueOfParam()), context);
        rv.setInt(R.id.widgetNameAndValueOfParam, "setBackgroundColor", colorOfValueBackground);
        rv.setTextViewText(R.id.widgetUpdateDate, widgetItem.getUpdateDate());


        return rv;
//        // Next, set a fill-intent, which will be used to fill in the pending intent template
//        // that is set on the collection view in StackWidgetProvider.
//        Bundle extras = new Bundle();
//        extras.putInt(MultipleStationWidgetProvider.EXTRA_ITEM, position);
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtras(extras);
//        // Make it possible to distinguish the individual on-click
//        // action of a given item
//        rv.setOnClickFillInIntent(R.id.multiple_station_list_item, fillInIntent);

    }

    private double cutStringToDoublePercentValue(String nameAndValueOfParam) {
        String parts[] = nameAndValueOfParam.split(" ");
        String secondPart = parts[1];
        String percentValue = secondPart.substring(0, secondPart.length() - 1);
        return Double.parseDouble(percentValue);
    }

    // Initialize the data set.
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

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
