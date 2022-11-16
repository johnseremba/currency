package com.johnseremba.currency.domain

import com.johnseremba.currency.data.repository.Repository
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConvertCurrencyUseCase @Inject constructor(private val repository: Repository) {
    operator fun invoke(
        baseCurrency: String,
        targetCurrency: String,
        amount: BigDecimal
    ): Flow<Result<BigDecimal>> = flow {
        val result = repository.convertCurrency(baseCurrency, targetCurrency, amount)
        emit(result)
    }
}
