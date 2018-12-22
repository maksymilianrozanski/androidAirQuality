package io.github.maksymilianrozanski.widget;

import android.app.IntentService;
import android.content.Intent;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultipleStationWidgetUpdateIntentService extends IntentService
        implements MultipleStationWidgetContract.Model.OnFinishedListener {

    private MultipleStationWidgetContract.UpdateService updateService = new UpdateServiceImpl(this, this);

    public MultipleStationWidgetUpdateIntentService() {
        super("MultipleStationWidgetUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
