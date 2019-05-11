package io.github.maksymilianrozanski.widget.service;

import android.content.Context;
import android.content.Intent;
import androidx.core.app.JobIntentService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.maksymilianrozanski.widget.MultipleStationWidgetContract;
import io.github.maksymilianrozanski.widget.WidgetItem;

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
