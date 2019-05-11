package io.github.maksymilianrozanski.widget.service

import dagger.Component
import io.github.maksymilianrozanski.layout.WidgetUITest
import org.junit.Ignore

@Ignore
@Component(modules = [WidgetUITest.TestWidgetModelModule::class])
interface TestWidgetModelComponent : WidgetModelComponent