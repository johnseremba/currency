package com.johnseremba.currency.ui.convert

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.johnseremba.currency.R
import com.johnseremba.currency.databinding.FragmentCurrencyConversionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrencyConversionFragment : Fragment() {
    private var _binding: FragmentCurrencyConversionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConversionViewModel by viewModels()

    private lateinit var currenciesAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyConversionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initStateObservers()
    }

    private fun initUi() {
        currenciesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item)

        with(binding) {
            spinnerFrom.adapter = currenciesAdapter
            spinnerTo.adapter = currenciesAdapter

            tvFrom.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) convertCurrency()
            }

            btnConvert.setOnClickListener {
                convertCurrency()
            }

            btnSwap.setOnClickListener {
                viewModel.swap()
            }

            btnDetails.setOnClickListener {
                val action = CurrencyConversionFragmentDirections.actionConversionFragmentToDetailFragment(viewModel.getDetailNavigationData())
                findNavController().navigate(action)
            }

            spinnerFrom.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = currenciesAdapter.getItem(position)
                    Log.v("Selected", "from: " + selected.orEmpty())
                    viewModel.setBaseCurrency(selected.orEmpty())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            spinnerTo.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = currenciesAdapter.getItem(position)
                    Log.v("Selected", "to: " + selected.orEmpty())
                    viewModel.setTargetCurrency(selected.orEmpty())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun convertCurrency() {
        val amount = binding.tvFrom.text.toString().ifEmpty { "1" }
        viewModel.convert(amount)
    }

    private fun initStateObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleProgressbar(state.isLoading)
                    when {
                        state.isError -> handleError(state.errorMsg)
                        else -> {
                            handleCurrencyState(state.currencies, state.baseCurrency, state.targetCurrency)
                            setConversionValues(state.fromAmount, state.convertedAmount)
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

    private fun handleCurrencyState(currencies: List<String>, baseCurrency: String, targetCurrency: String) {
        currenciesAdapter.clear()
        currenciesAdapter.addAll(currencies)
        if (currencies.isEmpty()) return

        with(binding) {
            val baseCurrencyIndex = if (baseCurrency.isNotEmpty()) currencies.indexOf(baseCurrency) else 0
            if (baseCurrencyIndex >= 0)
                spinnerFrom.setSelection(baseCurrencyIndex)

            val targetCurrencyIndex = if (targetCurrency.isNotEmpty()) currencies.indexOf(targetCurrency) else 0
            if (targetCurrencyIndex >= 0)
                spinnerTo.setSelection(targetCurrencyIndex)
        }
    }

    private fun setConversionValues(fromAmount: String, convertedAmount: String) = with(binding) {
        tvFrom.setText(fromAmount)
        tvTo.setText(convertedAmount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
