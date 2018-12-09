package io.github.maksymilianrozanski.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class ScrollableWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext()));
    }
}

