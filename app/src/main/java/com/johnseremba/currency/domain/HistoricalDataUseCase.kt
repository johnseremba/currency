package com.johnseremba.currency.domain

import com.johnseremba.currency.data.repository.Repository
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoricalDataUseCase @Inject constructor(private val repository: Repository) {
    operator fun invoke(
        baseCurrency: String,
        targetCurrency: String
    ): Flow<Result<List<Pair<LocalDate, BigDecimal>>>> = flow {

        val endDate: LocalDate = LocalDate.now()
        val startDate: LocalDate = endDate.minusDays(2)

        val result = repository.getHistoricalData(startDate, endDate, baseCurrency, targetCurrency)
        emit(result)
    }
}
