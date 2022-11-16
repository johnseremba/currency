package com.johnseremba.currency.ui.detail

import java.math.BigDecimal

data class DetailUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMsg: String = "",
    val historicalData: List<Pair<String, BigDecimal>> = emptyList()
)
