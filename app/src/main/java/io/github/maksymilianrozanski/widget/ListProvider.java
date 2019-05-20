package io.github.maksymilianrozanski.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;
import java.util.Map;

import io.github.maksymilianrozanski.R;
import io.github.maksymilianrozanski.vieweditors.SensorAdapter;

import static io.github.maksymilianrozanski.widget.service.UpdateServiceImplKt.getWidgetItemListFromSharedPreferences;

class ListProvider implements
        RemoteViewsService.RemoteViewsFactory {

    private List<WidgetItem> listItemList;
    private Context context;
    private BroadcastReceiver intentListener;

    ListProvider(Context context) {
        this.context = context;
        if (listItemList == null) try {
            listItemList = getWidgetItemListFromSharedPreferences(context);
        } catch (Exception e) {
            if (!e.getMessage().equals("no value saved")) {
                throw e;
            }
        }
        setupIntentListener();
    }

    private void setupIntentListener() {
        if (intentListener == null) {
            intentListener = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    listItemList = intent.getParcelableArrayListExtra(MultipleStationWidgetProvider.INTENT_KEY_PARCELABLE_ARRAY_LIST_EXTRA);
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(MultipleStationWidgetProvider.INTENT_ACTION_FOR_LIST_PROVIDER);
            context.registerReceiver(intentListener, filter);
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

//        if (widgetItem.isUpToDate()) {  //TODO: adjust to the dark theme
//            rv.setInt(R.id.widgetStationNameListItem, "setBackgroundColor", ContextCompat.getColor(context, R.color.white));
//        } else {
//            rv.setInt(R.id.widgetStationNameListItem, "setBackgroundColor", ContextCompat.getColor(context, R.color.noData));
//        }

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("StationId", widgetItem.getStationId());
        fillInIntent.putExtra("StationName", widgetItem.getStationName());
        rv.setOnClickFillInIntent(R.id.multiple_station_list_item, fillInIntent);

        return rv;
    }

    private double cutStringToDoublePercentValue(String nameAndValueOfParam) {
        try {
            String[] parts = nameAndValueOfParam.split(" ");
            String secondPart = parts[1];
            String percentValue = secondPart.substring(0, secondPart.length() - 1);
            return Double.parseDouble(percentValue);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return -1;
        }
    }

    @Override
    public void onCreate() {
        requestUpdate();
    }

    @Override
    public void onDataSetChanged() {
        if (listItemList == null) {
            requestUpdate();
        }
    }

    private void requestUpdate() {
        SharedPreferences preferences = context.getSharedPreferences(MultipleStationWidgetProvider.SHARED_PREFERENCES_VISIBILITY_KEY, Context.MODE_PRIVATE);
        Map<String, ?> allWidgetIds = preferences.getAll();

        for (Map.Entry<String, ?> entry : allWidgetIds.entrySet()) {
            try {
                MultipleStationWidgetProvider.sendIntentToUpdatingService(context, Integer.parseInt(entry.getKey()));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    @Override
    public void onDestroy() {
        teardownIntentListener();
    }

    private void teardownIntentListener() {
        if (intentListener != null) {
            context.unregisterReceiver(intentListener);
            intentListener = null;
        }
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
