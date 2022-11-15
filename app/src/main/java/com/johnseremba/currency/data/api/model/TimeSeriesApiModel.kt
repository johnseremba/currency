package com.johnseremba.currency.data.api.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class TimeSeriesApiModel(
    @SerializedName("rates") val rates: Map<String, Map<String, BigDecimal>>
)
