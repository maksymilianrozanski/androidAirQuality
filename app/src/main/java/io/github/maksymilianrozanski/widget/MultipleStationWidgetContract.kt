package io.github.maksymilianrozanski.widget

interface MultipleStationWidgetContract {

    interface Presenter {
        fun onDestroy()
        fun refreshClicked()
        fun stationClicked(stationId: Int, stationName: String)
    }

    interface MainView {
        fun showProgress()
        fun hideProgress()
        fun displayError()
        fun onResponseFailure(throwable: Throwable)
        fun displayData(stations: List<WidgetItem>)
        fun startSingleStationActivity(stationId: Int, stationName: String)
    }

    interface Model {
        interface OnFinishedListener {
            fun onFinished(stations: List<WidgetItem>)
            fun onFailure(throwable: Throwable)
        }

        fun fetchData(onFinishedListener: OnFinishedListener)
    }
}