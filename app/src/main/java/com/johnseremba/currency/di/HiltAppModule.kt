package com.johnseremba.currency.di

import com.johnseremba.currency.data.api.CurrencyService
import com.johnseremba.currency.data.repository.CurrencyRepository
import com.johnseremba.currency.data.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface HiltAppModule {

    companion object {
        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit.Builder): CurrencyService {
            return retrofit.build().create(CurrencyService::class.java)
        }
    }

    @Binds
    @Singleton
    fun provideRepository(
        currencyRepository: CurrencyRepository
    ): Repository
}
