package com.johnseremba.currency.domain

import com.johnseremba.currency.data.repository.Repository
import java.math.BigDecimal
import javax.inject.Inject

class PopularCurrenciesUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(baseCurrency: String): Result<List<Pair<String, BigDecimal>>> {
        return repository.getRatesForPopularCurrencies(baseCurrency)
    }
}
