package com.johnseremba.currency.data.api

import com.johnseremba.currency.data.api.model.CurrencySymbolsApiModel
import retrofit2.http.GET

interface CurrencyService {

    @GET("/fixer/symbols")
    suspend fun getSupportedCurrencies(): CurrencySymbolsApiModel
}
