package io.github.maksymilianrozanski.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultipleStationWidgetUpdateService extends JobIntentService
        implements
        MultipleStationWidgetContract.Model.OnFinishedListener {

    private MultipleStationWidgetContract.UpdateService updateService = new UpdateServiceImpl(this, this);
    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MultipleStationWidgetUpdateService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NotNull Intent intent) {
        updateService.onHandleWork(intent);
    }

    @Override
    public void onFinished(@NotNull List<WidgetItem> stations) {
        updateService.onFinished(stations);
    }

    @Override
    public void onFailure(@NotNull Throwable throwable) {
        updateService.onFailure(throwable);
    }
}
