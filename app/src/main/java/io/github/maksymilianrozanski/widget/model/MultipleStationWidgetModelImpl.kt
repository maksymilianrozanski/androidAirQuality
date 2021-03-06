package io.github.maksymilianrozanski.widget.model

import android.content.Context
import android.location.Location
import android.util.Log
import io.github.maksymilianrozanski.dataholders.StationList
import io.github.maksymilianrozanski.widget.*
import java.util.*

class MultipleStationWidgetModelImpl(var context: Context,
                                     var myLocationProvider: MyLocationProvider,
                                     var connectionCheck: ConnectionCheck
) : MultipleStationWidgetContract.Model,
        MyLocationProvider.OnFinishedListener {

    override fun fetchData(onFinishedListener: MultipleStationWidgetContract.Model.OnFinishedListener) {
        myLocationProvider.getLocation(this, onFinishedListener)
    }

    override fun onLocationReceived(location: Location, onFinishedToPass: MultipleStationWidgetContract.Model.OnFinishedListener) {
        val stationListInstance = StationList.getStationListInstance(context)
        stationListInstance.sortStationsByDistance(context, location)
        fetchDataFromWeb(onFinishedToPass)
    }

    override fun onLocationFailure(throwable: Throwable, onFinishedToPass: MultipleStationWidgetContract.Model.OnFinishedListener) {
        onFinishedToPass.onFailure(throwable)
    }

    private fun fetchDataFromWeb(onFinishedListener: MultipleStationWidgetContract.Model.OnFinishedListener) {
        if (connectionCheck.isConnected()) {
            val widgetItemList = createWidgetItemListWithStationNamesAndIds(5)
            fetchSensorDataForWidgetItems(onFinishedListener, widgetItemList)

            onFinishedListener.onFinished(widgetItemList)
        } else {
            onFinishedListener.onFailure(Exception(no_internet_connection_exception))
        }
    }

    private fun fetchSensorDataForWidgetItems(onFinishedListener: MultipleStationWidgetContract.Model.OnFinishedListener, widgetItemList: ArrayList<WidgetItem>) {
        val threads = arrayOfNulls<FetchWidgetItem>(widgetItemList.size)

        for (i in widgetItemList.indices) {
            threads[i] = FetchWidgetItem(i, context, widgetItemList)
            threads[i]?.start()
        }

        for (thread in threads) {
            try {
                thread?.join()
            } catch (e: InterruptedException) {
                Log.e("Log", "Exception: $e")
                onFinishedListener.onFailure(e)
            }
        }
    }

    private fun createWidgetItemListWithStationNamesAndIds(numberOfStations: Int): ArrayList<WidgetItem> {
        val widgetItemList = ArrayList<WidgetItem>()
        for (i in 0 until numberOfStations) {
            val widgetItem = WidgetItem()
            val currentStation = StationList.getStationListInstance(context).getStation(i)
            widgetItem.stationId = Integer.parseInt(currentStation.id)
            widgetItem.stationName = currentStation.name
            widgetItemList.add(widgetItem)
        }
        return widgetItemList
    }
}