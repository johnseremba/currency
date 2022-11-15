package com.johnseremba.currency.data.api

import com.johnseremba.currency.data.api.model.ConvertApiModel
import com.johnseremba.currency.data.api.model.CurrencySymbolsApiModel
import com.johnseremba.currency.data.api.model.TimeSeriesApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {

    @GET("/fixer/symbols")
    suspend fun getSupportedCurrencies(): CurrencySymbolsApiModel

    @GET("/fixer/convert")
    suspend fun convert(
        @Query("from") baseCurrency: String,
        @Query("to") targetCurrency: String,
        @Query("amount") amount: Double
    ): ConvertApiModel

    @GET("/fixer/timeseries")
    suspend fun getTimeSeries(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") baseCurrency: String,
        @Query("symbols") targetCurrency: String
    ): TimeSeriesApiModel
}
