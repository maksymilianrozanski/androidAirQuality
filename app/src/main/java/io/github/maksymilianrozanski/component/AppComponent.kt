package io.github.maksymilianrozanski.component

import android.app.Application
import android.content.Context
import dagger.Component
import io.github.maksymilianrozanski.MyApp
import io.github.maksymilianrozanski.module.AppModule
import io.github.maksymilianrozanski.widget.model.ModelProvider
import io.github.maksymilianrozanski.widget.model.WidgetModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, WidgetModelModule::class])
interface AppComponent {

    fun inject(myApp: MyApp)

    fun getContext(): Context

    fun getApplication(): Application

    fun getWidgetModel(): ModelProvider
}