package io.github.maksymilianrozanski.widget

import android.location.Location

interface MyLocationProvider {

    fun getLocation(onFinishedListener: OnFinishedListener,
                    onFinishedToPass:MultipleStationWidgetContract.Model.OnFinishedListener)

    interface OnFinishedListener {
        fun onLocationReceived(location:Location, onFinishedToPass:MultipleStationWidgetContract.Model.OnFinishedListener)
        fun onLocationFailure(throwable: Throwable, onFinishedToPass:MultipleStationWidgetContract.Model.OnFinishedListener)
    }
}