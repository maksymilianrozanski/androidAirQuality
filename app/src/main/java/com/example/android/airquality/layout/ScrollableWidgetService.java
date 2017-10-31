package com.example.android.airquality.layout;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.android.airquality.utility.MultipleStationWidgetUpdateService;

import java.util.ArrayList;

public class ScrollableWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ArrayList<WidgetItem> widgetItems = MultipleStationWidgetUpdateService
                .getWidgetItemListFromSharedPreferences(getApplicationContext());
        return (new ListProvider(this.getApplicationContext(), widgetItems));
    }
}

