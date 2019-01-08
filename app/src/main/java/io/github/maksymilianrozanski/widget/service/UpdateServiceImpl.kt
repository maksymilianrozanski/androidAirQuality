package io.github.maksymilianrozanski.widget.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Parcelable
import android.support.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.maksymilianrozanski.R
import io.github.maksymilianrozanski.widget.*
import io.github.maksymilianrozanski.widget.model.WidgetModelModule
import xdroid.toaster.Toaster
import java.lang.reflect.Type
import java.util.*

class UpdateServiceImpl(var context: Context,
                        var onFinishedListener: MultipleStationWidgetContract.Model.OnFinishedListener)
    : MultipleStationWidgetContract.UpdateService {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID


    override fun onHandleWork(intent: Intent?) {
        val locationProvider = MyLocationProviderImpl(context)
        val connectionCheck = ConnectionCheckImpl(context)
        val model = widgetModelComponent.getWidgetModelProvider().getModel(context, locationProvider, connectionCheck)
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
        saveWidgetItemList(stations)
        context.sendBroadcast(widgetUpdateIntent)
    }

    override fun onFailure(throwable: Throwable) {
        if (throwable.message == no_internet_connection_exception) {
            Toaster.toast(R.string.no_internet_connection)
        } else if (throwable.message == access_to_location_not_granted) {
            Toaster.toast(R.string.no_location_access)
        }
    }

    @VisibleForTesting
    fun saveWidgetItemList(stations: List<WidgetItem>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(multipleStationSharedPrefsKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gSon = Gson()
        val widgetItemsAsJson = gSon.toJson(stations)
        editor.remove(listTag)
        editor.apply()
        editor.putString(listTag, widgetItemsAsJson)
        editor.apply()
    }

    companion object {
        val multipleStationSharedPrefsKey = "multiple_station_widget_shared_prefs_key"
        val listTag = "io.github.maksymilianrozanski.widgetItemList"

        var widgetModelComponent: WidgetModelComponent = DaggerWidgetModelComponent.builder()
                .widgetModelModule(WidgetModelModule()).build()
    }
}

fun getWidgetItemListFromSharedPreferences(context: Context): List<WidgetItem> {
    val sharedPreferences = context.getSharedPreferences(UpdateServiceImpl.multipleStationSharedPrefsKey, Context.MODE_PRIVATE)
    val gSon = Gson()
    val jsonString = sharedPreferences.getString(UpdateServiceImpl.listTag, "no value saved")
    val listType: Type = object : TypeToken<ArrayList<WidgetItem>>() {}.type
    if (jsonString != "no value saved") {
        return gSon.fromJson(jsonString, listType)
    } else {
        throw Exception("no value saved")
    }
}