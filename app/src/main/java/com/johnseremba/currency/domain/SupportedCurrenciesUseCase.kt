package com.johnseremba.currency.domain

import com.johnseremba.currency.data.repository.Repository
import javax.inject.Inject

class SupportedCurrenciesUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getSupportedCurrencies()
    }
}
