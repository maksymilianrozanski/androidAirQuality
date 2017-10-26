package com.example.android.airquality.layout;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.android.airquality.utility.MultipleStationWidgetUpdateService;

/**
 * Created by Max on 20.10.2017.
 */

public class ScrollableWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(), MultipleStationWidgetUpdateService.getWidgetItemList()));
    }
}

