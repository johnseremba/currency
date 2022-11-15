package com.johnseremba.currency.data.api.model

import com.google.gson.annotations.SerializedName

data class CurrencySymbolsApiModel(
    @SerializedName("symbols") val symbols: Map<String, String>
)
