package io.github.maksymilianrozanski.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import io.github.maksymilianrozanski.R
import io.github.maksymilianrozanski.widget.model.MultipleStationWidgetModelImpl
import xdroid.toaster.Toaster
import java.util.*

class UpdateServiceImpl(var context: Context,
                        var onFinishedListener: MultipleStationWidgetContract.Model.OnFinishedListener)
    : MultipleStationWidgetContract.UpdateService {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onHandleWork(intent: Intent?) {
        val locationProvider = MyLocationProviderImpl(context)
        val connectionCheck = ConnectionCheckImpl(context)
        val model = MultipleStationWidgetModelImpl(context, locationProvider, connectionCheck)
        if (intent != null) {
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                appWidgetId = intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID)
            }
        }
        model.fetchData(onFinishedListener)
    }

    override fun onFinished(stations: List<WidgetItem>) {
        val widgetUpdateIntent = Intent()
        widgetUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        widgetUpdateIntent.putParcelableArrayListExtra(MultipleStationWidgetProvider.INTENT_KEY_PARCELABLE_ARRAY_LIST_EXTRA, stations as ArrayList<out Parcelable>)
        context.sendBroadcast(widgetUpdateIntent)
    }

    override fun onFailure(throwable: Throwable) {
        if (throwable.message == no_internet_connection_exception) {
            Toaster.toast(R.string.no_internet_connection)
        } else if (throwable.message == access_to_location_not_granted) {
            Toaster.toast(R.string.no_location_access)
        }
    }
}