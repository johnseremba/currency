package com.johnseremba.currency.ui.convert

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnseremba.currency.domain.SupportedCurrenciesUseCase
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
    private val supportedCurrenciesUseCase: SupportedCurrenciesUseCase
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

    private fun updateState(reducer: ConversionUiState.() -> ConversionUiState) {
        _uiState.update {
            it.reducer()
        }
    }

    private companion object {
        const val TAG = "ConversionViewModel"
    }
}
