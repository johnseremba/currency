package com.johnseremba.currency.domain

import com.johnseremba.currency.data.repository.Repository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupportedCurrenciesUseCase @Inject constructor(private val repository: Repository) {
    operator fun invoke(): Flow<Result<List<String>>> = flow {
        val result = repository.getSupportedCurrencies()
        emit(result)
    }
}
