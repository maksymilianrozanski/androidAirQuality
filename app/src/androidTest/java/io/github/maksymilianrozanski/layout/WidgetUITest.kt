package io.github.maksymilianrozanski.layout

import android.content.Context
import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.R
import io.github.maksymilianrozanski.main.MainActivity
import io.github.maksymilianrozanski.widget.ConnectionCheck
import io.github.maksymilianrozanski.widget.MultipleStationWidgetContract
import io.github.maksymilianrozanski.widget.MyLocationProvider
import io.github.maksymilianrozanski.widget.WidgetItem
import io.github.maksymilianrozanski.widget.model.ModelProvider
import io.github.maksymilianrozanski.widget.service.DaggerTestWidgetModelComponent
import io.github.maksymilianrozanski.widget.service.TestWidgetModelComponent
import io.github.maksymilianrozanski.widget.service.UpdateServiceImpl
import org.hamcrest.core.IsNull.notNullValue
import org.junit.*
import org.junit.Assert.assertThat
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class WidgetUITest {

    private var device: UiDevice? = null

    private lateinit var testComponent: TestWidgetModelComponent

    @JvmField
    @Rule
    var mainActivityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun before() {
        testComponent = DaggerTestWidgetModelComponent.builder()
                .testWidgetModelModule(TestWidgetModelModule()).build()

        UpdateServiceImpl.widgetModelComponent = testComponent

        device = UiDevice.getInstance(getInstrumentation())

        assertThat<UiDevice>(device, notNullValue())

        // Start from the home screen
        device!!.pressHome()
    }

    @Test
    fun uITest() {
        mainActivityRule.launchActivity(Intent())

        device!!.pressHome()

        val refreshButtonText = getInstrumentation().targetContext.getString(R.string.refresh).toUpperCase()
        val refreshButton = device!!.findObject(By.text(refreshButtonText))

        refreshButton.click()

        val expectedZeroStationName = "Name 1"
        val stationIndexZeroNameObject = device!!.findObject(UiSelector().text(expectedZeroStationName))
        Assert.assertEquals(stationIndexZeroNameObject.text, expectedZeroStationName)

        val expectedZeroStationValue = "PM10: 75%"
        val stationIndexZeroObjectValue = device!!.findObject(UiSelector().text(expectedZeroStationValue))
        Assert.assertEquals(stationIndexZeroObjectValue.text, expectedZeroStationValue)

        val expectedThirdStationName = "Name 4"
        val stationIndexThreeNameObject = device!!.findObject(UiSelector().text(expectedThirdStationName))
        Assert.assertEquals(stationIndexThreeNameObject.text, expectedThirdStationName)

        val expectedThirdStationValue = "PM10: 290%"
        val stationIndexThreeObjectValue = device!!.findObject(UiSelector().text(expectedThirdStationValue))
        Assert.assertEquals(stationIndexThreeObjectValue.text, expectedThirdStationValue)
    }

    @Ignore
    @Module
    class TestWidgetModelModule {

        @Ignore
        @Provides
        fun provideModelProvider(): ModelProvider {
            return ModelProviderMock()
        }
    }

    @Ignore
    private class ModelProviderMock : ModelProvider {

        @Ignore
        override fun getModel(context: Context, locationProvider: MyLocationProvider, connectionCheck: ConnectionCheck): MultipleStationWidgetContract.Model {
            return ModelMock()
        }
    }

    @Ignore
    private class ModelMock : MultipleStationWidgetContract.Model {

        @Ignore
        override fun fetchData(onFinishedListener: MultipleStationWidgetContract.Model.OnFinishedListener) {
            val widgetItems = ArrayList<WidgetItem>()

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

            onFinishedListener.onFinished(widgetItems)
        }
    }
}