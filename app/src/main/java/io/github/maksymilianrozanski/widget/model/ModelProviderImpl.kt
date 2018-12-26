package io.github.maksymilianrozanski.widget.model

import android.content.Context
import io.github.maksymilianrozanski.widget.ConnectionCheck
import io.github.maksymilianrozanski.widget.MultipleStationWidgetContract
import io.github.maksymilianrozanski.widget.MyLocationProvider

class ModelProviderImpl : ModelProvider {

    override fun getModelComponent(context: Context,
                                   locationProvider: MyLocationProvider,
                                   connectionCheck: ConnectionCheck)
            : MultipleStationWidgetContract.Model {
        return MultipleStationWidgetModelImpl(context, locationProvider, connectionCheck)
    }
}