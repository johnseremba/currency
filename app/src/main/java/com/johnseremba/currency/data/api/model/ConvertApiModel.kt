package com.johnseremba.currency.data.api.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

data class ConvertApiModel(
    @SerializedName("query") val query: QueryApiModel,
    @SerializedName("info") val info: ConversionInfoApiModel,
    @SerializedName("result") val convertedAmount: BigDecimal,
    @SerializedName("date") val date: Date
)

data class QueryApiModel(
    @SerializedName("from") val baseCurrency: String,
    @SerializedName("to") val targetCurrency: String,
    @SerializedName("amount") val amount: BigDecimal,
)

data class ConversionInfoApiModel(
    @SerializedName("rate") val rate: BigDecimal
)
