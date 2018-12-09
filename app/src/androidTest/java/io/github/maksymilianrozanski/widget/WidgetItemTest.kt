package io.github.maksymilianrozanski.widget

import android.os.Parcel
import org.junit.Test

class WidgetItemTest {

    @Test
    fun parcelableTest(){
        val stationName = "Station name"
        val nameAndValueOfParam = "PM10 25%"
        val updateDate = "2016-01-01"
        val stationId = 45
        val isUpToDate = false

        val widgetItem = WidgetItem()
        widgetItem.stationName = stationName
        widgetItem.nameAndValueOfParam = nameAndValueOfParam
        widgetItem.updateDate = updateDate
        widgetItem.stationId = stationId
        widgetItem.isUpToDate = isUpToDate

        val parcel = Parcel.obtain()
        widgetItem.writeToParcel(parcel,0)

        parcel.setDataPosition(0)

        val widgetItemFromParcel = WidgetItem.createFromParcel(parcel)

        assert(widgetItem == widgetItemFromParcel)
    }

    @Test
    fun parcelableTest2(){
        val stationName = "Station name"
        val nameAndValueOfParam = "PM10 25%"
        val updateDate = "2016-01-01"
        val stationId = 45
        val isUpToDate = true

        val widgetItem = WidgetItem()
        widgetItem.stationName = stationName
        widgetItem.nameAndValueOfParam = nameAndValueOfParam
        widgetItem.updateDate = updateDate
        widgetItem.stationId = stationId
        widgetItem.isUpToDate = isUpToDate

        val parcel = Parcel.obtain()
        widgetItem.writeToParcel(parcel,0)

        parcel.setDataPosition(0)

        val widgetItemFromParcel = WidgetItem.createFromParcel(parcel)

        assert(widgetItem == widgetItemFromParcel)
    }
}