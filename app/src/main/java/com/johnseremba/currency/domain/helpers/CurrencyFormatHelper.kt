package com.johnseremba.currency.domain.helpers

import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

object CurrencyFormatHelper {
    private const val DECIMAL_PLACES = 6

    fun formatAmount(amount: BigDecimal): String {
        return amount.getScaledAmount().toString()
    }

    fun toBigDecimal(amount: String): BigDecimal {
        return if (amount.isEmpty() || amount.isBlank()) {
            BigDecimal(0).getScaledAmount()
        } else {
            BigDecimal(amount).getScaledAmount()
        }
    }

    private fun BigDecimal.getScaledAmount(): BigDecimal {
        return setScale(DECIMAL_PLACES, HALF_EVEN)
    }
}
