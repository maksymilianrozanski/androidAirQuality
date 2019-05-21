package io.github.maksymilianrozanski.vieweditors

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.maksymilianrozanski.R
import io.github.maksymilianrozanski.dataholders.Station

abstract class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var stationName = itemView.findViewById(R.id.stationname) as TextView
    var cityName = itemView.findViewById(R.id.cityname) as TextView

    fun bindViews(station: Station) {
        stationName.text = station.name ?: "not specified"
        cityName.text = station.cityName ?: "not specified"
        itemView.setOnClickListener(this)
    }

    abstract fun abstractOnClick(v: View?)

    override fun onClick(v: View?) {
        abstractOnClick(v)
    }
}