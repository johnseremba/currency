package com.johnseremba.currency.data.api

import com.johnseremba.currency.data.api.model.ConvertApiModel
import com.johnseremba.currency.data.api.model.CurrencySymbolsApiModel
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
}
