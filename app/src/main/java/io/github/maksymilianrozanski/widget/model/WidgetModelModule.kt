package io.github.maksymilianrozanski.widget.model

import dagger.Module
import dagger.Provides

@Module
class WidgetModelModule() {

    @Provides
    fun provideModelProvider(): ModelProvider {
        return ModelProviderImpl()
    }
}