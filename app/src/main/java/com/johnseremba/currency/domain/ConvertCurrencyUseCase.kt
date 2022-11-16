package com.johnseremba.currency.domain

import com.johnseremba.currency.data.repository.Repository
import java.math.BigDecimal
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(
        baseCurrency: String,
        targetCurrency: String,
        amount: BigDecimal
    ): Result<BigDecimal> {
        return repository.convertCurrency(baseCurrency, targetCurrency, amount)
    }
}
