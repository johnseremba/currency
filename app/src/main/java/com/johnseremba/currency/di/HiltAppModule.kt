package com.johnseremba.currency.di

import com.johnseremba.currency.data.api.CurrencyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object HiltAppModule {

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit.Builder): CurrencyService {
        return retrofit.build().create(CurrencyService::class.java)
    }
}
