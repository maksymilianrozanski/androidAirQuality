package io.github.maksymilianrozanski.layout;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import io.github.maksymilianrozanski.utility.MultipleStationWidgetUpdateService;


public class ScrollableWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ArrayList<WidgetItem> widgetItems = MultipleStationWidgetUpdateService
                .getWidgetItemListFromSharedPreferences(getApplicationContext());
        return (new ListProvider(this.getApplicationContext(), widgetItems));
    }
}

