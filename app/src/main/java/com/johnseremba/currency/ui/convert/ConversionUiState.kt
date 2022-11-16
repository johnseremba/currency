package com.johnseremba.currency.ui.convert

data class ConversionUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMsg: String = "",
    val baseCurrency: String = "",
    val targetCurrency: String = "",
    val fromAmount: String = "1",
    val convertedAmount: String = "0.00",
    val currencies: List<String> = emptyList()
)
