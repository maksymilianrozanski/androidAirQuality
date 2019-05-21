package io.github.maksymilianrozanski.vieweditors

import android.app.Activity
import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.maksymilianrozanski.R
import io.github.maksymilianrozanski.dataholders.Station
import io.github.maksymilianrozanski.layout.SingleStationWidgetConfigActivity
import io.github.maksymilianrozanski.main.SingleStationActivity
import io.github.maksymilianrozanski.utility.SingleStationWidgetUpdateService
import java.text.ParseException

open class StationAdapter(private val activity: Activity, var list: MutableList<Station>, var viewHolderClass: Int) : RecyclerView.Adapter<StationViewHolder>() {

    var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    lateinit var application: Application

    constructor(application: Application,
                activity: Activity,
                list: MutableList<Station>,
                viewHolderInt: Int,
                appWidgetId: Int
    ) : this(activity, list, viewHolderInt) {
        this.appWidgetId = appWidgetId
        this.application = application
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.station_list_item, parent, false)
        return when (viewHolderClass) {
            mainActivityStationViewHolder -> MainActivityStationViewHolder(view)
            singleStationWidgetConfigViewHolder -> SingleStationWidgetConfigViewHolder(view)
            else -> throw Exception("Unknown class$viewHolderClass")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        holder.bindViews(list[position])
    }

    fun setData(newList: MutableList<Station>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class MainActivityStationViewHolder(itemView: View) : StationViewHolder(itemView) {

        override fun abstractOnClick(v: View?) {
            val station = list[adapterPosition]
            val stationId = station.id ?: "0"
            var stationIdInt = 0
            try {
                stationIdInt = Integer.parseInt(stationId)
            } catch (e: ParseException) {

            }
            val stationNameString = station.name ?: "not specified"

            val intent = Intent(activity, SingleStationActivity::class.java)
            intent.putExtra("StationId", stationIdInt)
            intent.putExtra("StationName", stationNameString)
            activity.startActivity(intent)
        }
    }

    inner class SingleStationWidgetConfigViewHolder(itemView: View) : StationViewHolder(itemView) {

        override fun abstractOnClick(v: View?) {
            val station = list[adapterPosition]
            val stationId = station.id ?: "0"
            var stationIdInt = 0
            try {
                stationIdInt = Integer.parseInt(stationId)
            } catch (e: ParseException) {

            }
            saveWidgetIdAndStationId(appWidgetId, stationIdInt)

            val intent = Intent()
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            activity.setResult(Activity.RESULT_OK, intent)

            val intentSendToService = Intent(activity, SingleStationWidgetUpdateService::class.java)
            intentSendToService.putExtra(SingleStationWidgetUpdateService.APP_WIDGET_ID_TO_UPDATE, stationIdInt)
            activity.sendBroadcast(intentSendToService)
            activity.finish()
        }

        private fun saveWidgetIdAndStationId(appWidgetId: Int, stationId: Int) {
            val sharedPreferences = application.getSharedPreferences(SingleStationWidgetConfigActivity.SHARED_PREF_KEY_WIDGET, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(SingleStationWidgetUpdateService.APP_WIDGET_ID_KEY + appWidgetId, stationId)
            editor.apply()
        }
    }

    companion object {
        const val mainActivityStationViewHolder: Int = 1
        const val singleStationWidgetConfigViewHolder: Int = 2
    }
}