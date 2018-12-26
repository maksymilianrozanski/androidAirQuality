package io.github.maksymilianrozanski.widget.model

import android.content.Context
import io.github.maksymilianrozanski.widget.ConnectionCheck
import io.github.maksymilianrozanski.widget.MultipleStationWidgetContract
import io.github.maksymilianrozanski.widget.MyLocationProvider

interface ModelProvider {

    fun getModelComponent(context: Context,
                          locationProvider: MyLocationProvider,
                          connectionCheck: ConnectionCheck)
            : MultipleStationWidgetContract.Model
}