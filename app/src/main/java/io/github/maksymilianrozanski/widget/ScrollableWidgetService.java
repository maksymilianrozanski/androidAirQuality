package io.github.maksymilianrozanski.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;


public class ScrollableWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        List<WidgetItem> widgetItems = MultipleStationWidgetUpdateService
                .getWidgetItemListFromSharedPreferences(getApplicationContext());
        return (new ListProvider(this.getApplicationContext(), (ArrayList<WidgetItem>) widgetItems));
    }
}

