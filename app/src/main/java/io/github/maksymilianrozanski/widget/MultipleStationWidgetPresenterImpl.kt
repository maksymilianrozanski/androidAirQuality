package io.github.maksymilianrozanski.widget

class MultipleStationWidgetPresenterImpl(var mainView: MultipleStationWidgetContract.MainView,
                                         var model: MultipleStationWidgetContract.Model)
    : MultipleStationWidgetContract.Presenter, MultipleStationWidgetContract.Model.OnFinishedListener {

    override fun onDestroy() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshClicked() {
        mainView.showProgress()
        model.fetchData(this)
    }

    override fun stationClicked(stationId: Int, stationName: String) {
        mainView.startSingleStationActivity(stationId, stationName)
    }

    override fun onFinished(stations: List<WidgetItem>) {
        mainView.hideProgress()
        mainView.displayData(stations)
    }

    override fun onFailure(throwable: Throwable) {
        mainView.hideProgress()
        mainView.displayError()
    }
}