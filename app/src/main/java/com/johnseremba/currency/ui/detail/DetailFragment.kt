package com.johnseremba.currency.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.johnseremba.currency.R
import com.johnseremba.currency.databinding.FragmentDetailBinding
import com.johnseremba.currency.ui.detail.adapter.HistoricalDataAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()
    private val historicalDataAdapter = HistoricalDataAdapter()
    private val popularCurrenciesAdapter = HistoricalDataAdapter()

    private val currencyData: DetailNavigationData by lazy {
        navArgs<DetailFragmentArgs>().value.currencyInfo
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initStateObservers()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchHistoricalData(currencyData.baseCurrency, currencyData.targetCurrency)
        viewModel.fetchPopularCurrencies(currencyData.baseCurrency)
    }

    private fun initUi() = with(binding) {
        labelHistorical.text = getString(R.string.label_historical_data, "${currencyData.baseCurrency} -> ${currencyData.targetCurrency}")
        rvHistorical.adapter = historicalDataAdapter
        binding.rvPopular.adapter = popularCurrenciesAdapter
    }

    private fun initStateObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleProgressbar(state.isLoading)
                    when {
                        state.isError -> handleError(state.errorMsg)
                        else -> {
                            handleHistoricalData(state.historicalData)
                            handlePopularCurrencyData(state.popularCurrencies)
                        }
                    }
                }
            }
        }
    }

    private fun handleProgressbar(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun handleError(errorMsg: String) {
        Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG).show()
    }

    private fun handleHistoricalData(historicalData: List<Pair<String, BigDecimal>>) {
        historicalDataAdapter.submitList(historicalData)
    }

    private fun handlePopularCurrencyData(popularCurrencies: List<Pair<String, BigDecimal>>) {
        popularCurrenciesAdapter.submitList(popularCurrencies)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
