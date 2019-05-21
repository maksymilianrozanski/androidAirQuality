package io.github.maksymilianrozanski.vieweditors

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.maksymilianrozanski.R
import io.github.maksymilianrozanski.dataholders.Station
import io.github.maksymilianrozanski.layout.SingleStationWidgetConfigActivity.SHARED_PREF_KEY_WIDGET
import io.github.maksymilianrozanski.utility.SingleStationWidgetUpdateService
import java.text.ParseException

class StationAdapterRecyclerWidgetConfig(private val activity: Activity, private val context: Context, val appWidgetId: Int, var list: MutableList<Station>) : RecyclerView.Adapter<StationAdapterRecyclerWidgetConfig.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.station_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(list[position])
    }

    fun setData(newList: MutableList<Station>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var stationName = itemView.findViewById(R.id.stationname) as TextView
        var cityName = itemView.findViewById(R.id.cityname) as TextView

        fun bindViews(station: Station) {
            stationName.text = station.name ?: "not specified"
            cityName.text = station.cityName ?: "not specified"
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
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

            val intentSendToService = Intent(context, SingleStationWidgetUpdateService::class.java)
            intentSendToService.putExtra(SingleStationWidgetUpdateService.APP_WIDGET_ID_TO_UPDATE, stationIdInt)
            activity.sendBroadcast(intentSendToService)
            activity.finish()
        }

        private fun saveWidgetIdAndStationId(appWidgetId: Int, stationId: Int) {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREF_KEY_WIDGET, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(SingleStationWidgetUpdateService.APP_WIDGET_ID_KEY + appWidgetId, stationId)
            editor.apply()
        }
    }
}