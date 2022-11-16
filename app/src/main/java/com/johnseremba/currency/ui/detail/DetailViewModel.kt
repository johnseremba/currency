package com.johnseremba.currency.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnseremba.currency.domain.HistoricalDataUseCase
import com.johnseremba.currency.domain.PopularCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val historicalDataUseCase: HistoricalDataUseCase,
    private val popularCurrenciesUseCase: PopularCurrenciesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchHistoricalData(baseCurrency: String, targetCurrency: String) {
        if (uiState.value.historicalData.isNotEmpty()) {
            Log.v(TAG, "Historical data already fetched, ignoring!")
            return
        }

        historicalDataUseCase(baseCurrency, targetCurrency)
            .onStart { updateState { copy(isLoading = true) } }
            .onCompletion { updateState { copy(isLoading = false) } }
            .onEach { result ->
                when {
                    result.isSuccess -> {
                        val dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")
                        val convertedResult = result.getOrNull()?.map {
                            val date = it.first
                            val stringDate = date.format(dateFormat)
                            Pair(stringDate, it.second)
                        } ?: emptyList()

                        updateState { copy(historicalData = convertedResult) }
                    }
                    result.isFailure -> {
                        handleError(result.exceptionOrNull())
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchPopularCurrencies(baseCurrency: String) {
        if (uiState.value.popularCurrencies.isNotEmpty()) {
            Log.v(TAG, "Popular currency data already fetched, ignoring!")
            return
        }

        popularCurrenciesUseCase.invoke(baseCurrency)
            .onStart { updateState { copy(isLoading = true) } }
            .onCompletion { updateState { copy(isLoading = false) } }
            .onEach { result ->
                when {
                    result.isSuccess -> {
                        updateState { copy(popularCurrencies = result.getOrElse { emptyList() }) }
                    }
                    result.isFailure -> {
                        handleError(result.exceptionOrNull())
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun handleError(throwable: Throwable?) {
        Log.d(TAG, "Failed to fetch data : $throwable")
        throwable?.printStackTrace()
        updateState { copy(isError = true, errorMsg = throwable?.message.orEmpty()) }
    }

    private fun updateState(reducer: DetailUiState.() -> DetailUiState) {
        _uiState.update {
            it.reducer()
        }
    }

    companion object {
        private const val TAG = "DetailViewModel"
    }
}
