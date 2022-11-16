package com.johnseremba.currency.domain

import com.johnseremba.currency.data.repository.Repository
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PopularCurrenciesUseCase @Inject constructor(private val repository: Repository) {
    operator fun invoke(baseCurrency: String): Flow<Result<List<Pair<String, BigDecimal>>>> = flow {
        val result = repository.getRatesForPopularCurrencies(baseCurrency)

        if (result.isSuccess && result.getOrNull() != null) {
            val formattedResult = result.getOrNull()!!.map {
                val currency = "$baseCurrency -> ${it.first}"
                Pair(currency, it.second)
            }
            emit(Result.success(formattedResult))
        } else {
            emit(result)
        }
    }
}
