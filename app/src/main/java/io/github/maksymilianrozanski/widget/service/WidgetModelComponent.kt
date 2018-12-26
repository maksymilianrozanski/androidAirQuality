package io.github.maksymilianrozanski.widget.service

import dagger.Component
import io.github.maksymilianrozanski.widget.model.ModelProvider
import io.github.maksymilianrozanski.widget.model.WidgetModelModule

@Component(modules = [WidgetModelModule::class])
interface WidgetModelComponent {
    fun getWidgetModel(): ModelProvider
}