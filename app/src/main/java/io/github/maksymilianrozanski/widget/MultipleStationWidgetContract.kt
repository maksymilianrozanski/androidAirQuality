package io.github.maksymilianrozanski.widget

interface MultipleStationWidgetContract {

    interface Model {
        interface OnFinishedListener {
            fun onFinished(stations: List<WidgetItem>)
            fun onFailure(throwable: Throwable)
        }

        fun fetchData(onFinishedListener: OnFinishedListener)
    }
}