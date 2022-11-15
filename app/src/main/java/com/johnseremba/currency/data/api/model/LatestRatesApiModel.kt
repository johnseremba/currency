package com.johnseremba.currency.data.api.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class LatestRatesApiModel(
    @SerializedName("base") val baseCurrency: String,
    @SerializedName("rates") val rates: Map<String, BigDecimal>
)
