package com.example.android.airquality.utility;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.airquality.layout.NewAppWidget;

/**
 * Created by Max on 23.09.2017.
 */

public class WidgetUpdateService extends IntentService {

    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";
    public static final String ACTION_DATA_UPDATED = "com.example.android.airquality.app.ACTION_DATA_UPDATED";

    public WidgetUpdateService() {
        super(WidgetUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.v("LOg", "Inside onHandleIntent");
        String msg = intent.getStringExtra(PARAM_IN_MSG);
        SystemClock.sleep(2000);
        Log.v("LOg", "After sleep");
        String resultText = msg + "is returned test result...";

        Intent intentSendBackToWidget = new Intent(this.getApplicationContext(), NewAppWidget.class);
        intentSendBackToWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        intentSendBackToWidget.putExtra(PARAM_OUT_MSG, resultText);

        sendBroadcast(intentSendBackToWidget);
    }
}
