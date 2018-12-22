package io.github.maksymilianrozanski.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.maksymilianrozanski.MyApp

@Module
class AppModule(var myApp: MyApp){

    @Provides
    fun provideContext(): Context {
        return myApp
    }

    @Provides
    fun provideApplication(): Application {
        return myApp
    }
}