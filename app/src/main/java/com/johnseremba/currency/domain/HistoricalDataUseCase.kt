package com.johnseremba.currency.domain

import com.johnseremba.currency.data.repository.Repository
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class HistoricalDataUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(
        baseCurrency: String,
        targetCurrency: String
    ): Result<List<Pair<LocalDate, BigDecimal>>> {

        val endDate: LocalDate = LocalDate.now()
        val startDate: LocalDate = endDate.minusDays(3)

        return repository.getHistoricalData(startDate, endDate, baseCurrency, targetCurrency)
    }
}
