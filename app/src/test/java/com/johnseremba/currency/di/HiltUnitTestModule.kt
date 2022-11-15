package com.johnseremba.currency.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HiltNetworkModule::class]
)
object HiltUnitTestModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}
