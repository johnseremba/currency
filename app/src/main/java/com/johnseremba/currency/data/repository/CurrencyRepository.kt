package com.johnseremba.currency.data.repository

import com.johnseremba.currency.data.api.CurrencyService
import com.johnseremba.currency.data.api.model.ConvertApiModel
import com.johnseremba.currency.data.api.model.CurrencySymbolsApiModel
import com.johnseremba.currency.data.api.model.TimeSeriesApiModel
import com.johnseremba.currency.di.CoroutineDispatcherProvider
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.withContext

class CurrencyRepository @Inject constructor(
    private val networkDataSource: CurrencyService,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : Repository {

    override suspend fun getSupportedCurrencies(): Result<List<String>> {

        return withContext(dispatcherProvider.io) {
            try {
                val apiResponse: CurrencySymbolsApiModel = networkDataSource.getSupportedCurrencies()
                val currencies = apiResponse.symbols.keys.toList().sorted()
                Result.success(currencies)
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }
    }

    override suspend fun convertCurrency(
        baseCurrency: String,
        targetCurrency: String,
        amount: BigDecimal
    ): Result<BigDecimal> {

        return withContext(dispatcherProvider.io) {
            try {
                val apiResponse: ConvertApiModel = networkDataSource.convert(baseCurrency, targetCurrency, amount.toDouble())
                Result.success(apiResponse.convertedAmount)
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }
    }

    override suspend fun getHistoricalData(
        startDate: LocalDate,
        endDate: LocalDate,
        baseCurrency: String,
        targetCurrency: String
    ): Result<List<Pair<LocalDate, BigDecimal>>> {

        return withContext(dispatcherProvider.io) {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            try {
                val startDateString = dateFormatter.format(startDate)
                val endDateString = dateFormatter.format(endDate)

                val apiResponse: TimeSeriesApiModel = networkDataSource.getTimeSeries(
                    startDateString,
                    endDateString,
                    baseCurrency,
                    targetCurrency
                )

                val result: List<Pair<LocalDate, BigDecimal>> = apiResponse.rates.map {
                    val date = LocalDate.parse(it.key)
                    val rate = it.value[targetCurrency] ?: BigDecimal(0)
                    Pair(date, rate)
                }

                Result.success(result)
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }
    }

    override suspend fun getRatesForPopularCurrencies(
        baseCurrency: String
    ): Result<List<Pair<String, BigDecimal>>> {

        return withContext(dispatcherProvider.io) {
            try {
                val apiResponse = networkDataSource.getLatestRates(
                    baseCurrency, getPopularCurrencies().joinToString(",")
                )

                val result = apiResponse.rates.map {
                    Pair(it.key, it.value)
                }
                Result.success(result)
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }
    }

    private fun getPopularCurrencies(): List<String> {
        return listOf("GBP", "JPY", "EUR", "USD", "AUD", "CAD", "CHF", "CNY", "HKD", "NZD")
    }
}
