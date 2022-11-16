package com.johnseremba.currency.data.repository

import java.math.BigDecimal
import java.time.LocalDate

interface Repository {
    suspend fun getSupportedCurrencies(): Result<List<String>>

    suspend fun convertCurrency(
        baseCurrency: String,
        targetCurrency: String,
        amount: BigDecimal
    ): Result<BigDecimal>

    suspend fun getHistoricalData(
        startDate: LocalDate,
        endDate: LocalDate,
        baseCurrency: String,
        targetCurrency: String
    ): Result<List<Pair<LocalDate, BigDecimal>>>

    suspend fun getRatesForPopularCurrencies(
        baseCurrency: String,
    ): Result<List<Pair<String, BigDecimal>>>
}
