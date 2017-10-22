package com.example.android.airquality.layout;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.airquality.R;

import java.util.ArrayList;

class ListProvider implements
        RemoteViewsService.RemoteViewsFactory {

    private ArrayList<WidgetItem> listItemList = new ArrayList<WidgetItem>();
    private Context context = null;
    private int appWidgetId;

//    private static final int mCount = 10;
//    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();


    ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        populateListItem();
    }

    private void populateListItem() {
        for (int i = 0; i < 10; i++) {
            WidgetItem widgetListItem = new WidgetItem();
            widgetListItem.setStationName("Station name..." + i);
            widgetListItem.setNameAndValueOfParam("example param 100%");
            widgetListItem.setUpdateDate("2100-10-10");
            listItemList.add(widgetListItem);
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

    // Initialize the data set.
    @Override
    public void onCreate() {
//        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
//        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
//        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
//        for (int i = 0; i < mCount; i++) {
//            mWidgetItems.add(new WidgetItem(i + 1));
//        }
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
