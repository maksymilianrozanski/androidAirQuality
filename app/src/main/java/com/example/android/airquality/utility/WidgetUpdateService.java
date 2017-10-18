package com.example.android.airquality.utility;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import layout.NewAppWidget;

/**
 * Created by Max on 23.09.2017.
 */

public class WidgetUpdateService extends IntentService {

    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";

    public WidgetUpdateService() {
        super(WidgetUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.v("LOg", "Inside onHandleIntent");
        String msg = intent.getStringExtra(PARAM_IN_MSG);
        SystemClock.sleep(5000);
        String resultText = msg + "is returned test result...";

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NewAppWidget.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, resultText);
        sendBroadcast(broadcastIntent);
    }
}
