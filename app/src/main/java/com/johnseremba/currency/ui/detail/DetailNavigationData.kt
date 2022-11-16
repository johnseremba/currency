package com.johnseremba.currency.ui.detail

import java.io.Serializable

data class DetailNavigationData(
    val baseCurrency: String = "",
    val targetCurrency: String = "",
) : Serializable
