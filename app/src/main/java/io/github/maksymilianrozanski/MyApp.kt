package io.github.maksymilianrozanski

//import io.github.maksymilianrozanski.component.DaggerAppComponent
import android.app.Application
import android.content.Context
import io.github.maksymilianrozanski.component.AppComponent
import io.github.maksymilianrozanski.component.DaggerAppComponent
import io.github.maksymilianrozanski.module.AppModule
import io.github.maksymilianrozanski.widget.model.WidgetModelModule

class MyApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .widgetModelModule(WidgetModelModule())
                .build()
    }

    companion object {
        fun get(context: Context): MyApp {
            return context.applicationContext as MyApp
        }
    }
}