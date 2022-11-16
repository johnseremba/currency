package com.johnseremba.currency.ui.convert

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnseremba.currency.domain.ConvertCurrencyUseCase
import com.johnseremba.currency.domain.SupportedCurrenciesUseCase
import com.johnseremba.currency.domain.helpers.CurrencyFormatHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

@HiltViewModel
class ConversionViewModel @Inject constructor(
    private val supportedCurrenciesUseCase: SupportedCurrenciesUseCase,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchSupportedCurrencies()
    }

    private fun fetchSupportedCurrencies() {
        supportedCurrenciesUseCase()
            .onStart { setLoadingState(true) }
            .onCompletion { setLoadingState(false) }
            .onEach { response ->
                when {
                    response.isSuccess -> updateState { copy(currencies = response.getOrElse { emptyList() }) }
                    response.isFailure -> {
                        Log.e(TAG, "Failed to fetch currencies list : ${response.exceptionOrNull()}")
                        response.exceptionOrNull()?.printStackTrace()
                        updateState { copy(isError = true, errorMsg = response.exceptionOrNull()?.message.orEmpty()) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun setLoadingState(isLoading: Boolean) {
        updateState { copy(isLoading = isLoading) }
    }

    fun convert(amount: String) {
        if (uiState.value.isLoading) {
            Log.v(TAG, "Ignoring conversion request, Ui still Loading")
            return
        }

        val baseCurrency = uiState.value.baseCurrency
        val targetCurrency = uiState.value.targetCurrency
        val amountToConvert = CurrencyFormatHelper.toBigDecimal(amount)

        Log.v(TAG, "Converting from :$baseCurrency, to: $targetCurrency, amount: $amountToConvert")

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) return

        convertCurrencyUseCase(baseCurrency, targetCurrency, amountToConvert)
            .onStart {
                updateState {
                    copy(
                        isLoading = true,
                        fromAmount = CurrencyFormatHelper.formatAmount(amountToConvert)
                    )
                }
            }
            .onCompletion { setLoadingState(false) }
            .onEach { response ->
                when {
                    response.isSuccess -> {
                        val formattedAmount = CurrencyFormatHelper.formatAmount(response.getOrNull())
                        updateState {
                            copy(
                                convertedAmount = formattedAmount
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun swap() {
        val from = uiState.value.baseCurrency
        val to = uiState.value.targetCurrency
        val fromAmount = uiState.value.fromAmount
        val converted = uiState.value.convertedAmount

        updateState {
            copy(
                baseCurrency = to,
                targetCurrency = from,
                fromAmount = converted,
                convertedAmount = fromAmount
            )
        }

        convert(converted)
    }

    fun setBaseCurrency(currency: String) {
        updateState { copy(baseCurrency = currency) }
    }

    fun setTargetCurrency(currency: String) {
        updateState { copy(targetCurrency = currency) }
    }

    private fun updateState(reducer: ConversionUiState.() -> ConversionUiState) {
        _uiState.update {
            it.reducer()
        }
    }

    private companion object {
        const val TAG = "ConversionViewModel"
    }
}
