package io.github.maksymilianrozanski.component

import dagger.Component
import io.github.maksymilianrozanski.layout.WidgetUITest
import io.github.maksymilianrozanski.module.AppModule
import org.junit.Ignore

@Ignore
@Component(modules = [AppModule::class, WidgetUITest.TestWidgetModelModule::class])
interface TestAppComponent : AppComponent {
    fun inject(test: WidgetUITest)
}