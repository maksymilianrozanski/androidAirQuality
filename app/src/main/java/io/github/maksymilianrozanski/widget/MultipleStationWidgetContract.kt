package io.github.maksymilianrozanski.widget

import android.content.Intent

interface MultipleStationWidgetContract {

    interface Model {
        interface OnFinishedListener {
            fun onFinished(stations: List<WidgetItem>)
            fun onFailure(throwable: Throwable)
        }

        fun fetchData(onFinishedListener: OnFinishedListener)
    }

    interface UpdateService {
        fun onHandleWork(intent: Intent?)
        fun onFinished(stations: List<WidgetItem>)
        fun onFailure(throwable: Throwable)
    }
}