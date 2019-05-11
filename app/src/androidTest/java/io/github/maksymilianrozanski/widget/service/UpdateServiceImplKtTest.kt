package io.github.maksymilianrozanski.widget.service

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import io.github.maksymilianrozanski.widget.MultipleStationWidgetContract
import io.github.maksymilianrozanski.widget.WidgetItem
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class UpdateServiceImplKtTest {

    private val widgetItems = ArrayList<WidgetItem>()

    @Before
    fun setup() {
        val widgetItem1 = WidgetItem()
        widgetItem1.stationName = "Name 1"
        widgetItem1.nameAndValueOfParam = "PM10: 75%"
        widgetItem1.updateDate = "2018-01-01 01:00"
        widgetItem1.stationId = 1
        widgetItem1.isUpToDate = true
        widgetItems.add(widgetItem1)

        val widgetItem2 = WidgetItem()
        widgetItem2.stationName = "Name 2"
        widgetItem2.nameAndValueOfParam = "PM10: 130%"
        widgetItem2.updateDate = "2018-01-01 01:00"
        widgetItem2.stationId = 2
        widgetItem2.isUpToDate = true
        widgetItems.add(widgetItem2)

        val widgetItem3 = WidgetItem()
        widgetItem3.stationName = "Name 3"
        widgetItem3.nameAndValueOfParam = "PM10: 200%"
        widgetItem3.updateDate = "2018-01-01 01:00"
        widgetItem3.stationId = 3
        widgetItem3.isUpToDate = true
        widgetItems.add(widgetItem3)

        val widgetItem4 = WidgetItem()
        widgetItem4.stationName = "Name 4"
        widgetItem4.nameAndValueOfParam = "PM10: 290%"
        widgetItem4.updateDate = "2018-01-01 01:00"
        widgetItem4.stationId = 4
        widgetItem4.isUpToDate = true
        widgetItems.add(widgetItem4)

        val widgetItem5 = WidgetItem()
        widgetItem5.stationName = "Name 5"
        widgetItem5.nameAndValueOfParam = "PM10: 750%"
        widgetItem5.updateDate = "2018-01-01 01:00"
        widgetItem5.stationId = 5
        widgetItem5.isUpToDate = true
        widgetItems.add(widgetItem5)
    }

    @Test
    fun getWidgetItemListFromSharedPreferencesTest() {
        val contextMock = Mockito.mock(Context::class.java)
        val sharedPrefsMock = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(contextMock.getSharedPreferences(any(), any())).thenReturn(sharedPrefsMock)
        Mockito.`when`(sharedPrefsMock.getString(any(), any())).thenReturn("[{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 75%\",\"stationId\":1,\"stationName\":\"Name 1\",\"updateDate\":\"2018-01-01 01:00\"},{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 130%\",\"stationId\":2,\"stationName\":\"Name 2\",\"updateDate\":\"2018-01-01 01:00\"},{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 200%\",\"stationId\":3,\"stationName\":\"Name 3\",\"updateDate\":\"2018-01-01 01:00\"},{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 290%\",\"stationId\":4,\"stationName\":\"Name 4\",\"updateDate\":\"2018-01-01 01:00\"},{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 750%\",\"stationId\":5,\"stationName\":\"Name 5\",\"updateDate\":\"2018-01-01 01:00\"}]")

        val obtainedList = getWidgetItemListFromSharedPreferences(contextMock)
        assertTrue(obtainedList == widgetItems)
    }

    @Test
    fun getWidgetItemListFromSharedPreferencesNothingSavedTest() {
        val contextMock = Mockito.mock(Context::class.java)
        val sharedPrefsMock = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(contextMock.getSharedPreferences(any(), any())).thenReturn(sharedPrefsMock)
        Mockito.`when`(sharedPrefsMock.getString(any(), any())).thenReturn("no value saved")

        assertFailsWith(Exception::class) {
            getWidgetItemListFromSharedPreferences(contextMock)
        }
    }

    @Test
    fun savingWidgetItemsTest() {
        val contextMock = Mockito.mock(Context::class.java)
        val sharedPrefsMock = Mockito.mock(SharedPreferences::class.java)
        val editorMock = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(contextMock.getSharedPreferences(any(), any())).thenReturn(sharedPrefsMock)
        Mockito.`when`(sharedPrefsMock.edit()).thenReturn(editorMock)
        val onFinishedListenerMock = Mockito.mock(MultipleStationWidgetContract.Model.OnFinishedListener::class.java)

        val updateService = UpdateServiceImpl(contextMock, onFinishedListenerMock)

        updateService.saveWidgetItemList(widgetItems)
        Mockito.verify(editorMock).remove(UpdateServiceImpl.listTag)
        Mockito.verify(editorMock, times(2)).apply()
        Mockito.verify(editorMock).putString(UpdateServiceImpl.listTag, "[{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 75%\",\"stationId\":1,\"stationName\":\"Name 1\",\"updateDate\":\"2018-01-01 01:00\"},{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 130%\",\"stationId\":2,\"stationName\":\"Name 2\",\"updateDate\":\"2018-01-01 01:00\"},{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 200%\",\"stationId\":3,\"stationName\":\"Name 3\",\"updateDate\":\"2018-01-01 01:00\"},{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 290%\",\"stationId\":4,\"stationName\":\"Name 4\",\"updateDate\":\"2018-01-01 01:00\"},{\"isUpToDate\":true,\"nameAndValueOfParam\":\"PM10: 750%\",\"stationId\":5,\"stationName\":\"Name 5\",\"updateDate\":\"2018-01-01 01:00\"}]")
    }
}