package com.moamen.currency.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.moamen.currency.databinding.FragmentCurrencyConverterBinding
import com.moamen.currency.network.utils.NetworkUtils
import com.moamen.currency.util.UiState
import com.moamen.currency.viewmodels.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CurrencyConverterFragment : Fragment() {

    private lateinit var binding: FragmentCurrencyConverterBinding
    private val viewModel: CurrencyViewModel by activityViewModels()
    private var rates: Map<String, Double>? = null
    private var isUpdatingEditText = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized)
            binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeLatestRates()
        fetchLatestRates()
    }

    private fun setupSpinners(currencies: List<String>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.fromCurrencySpinner.adapter = adapter
        binding.toCurrencySpinner.adapter = adapter
        binding.toCurrencySpinner.setSelection(1)
        binding.fromCurrencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateToValue()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        binding.toCurrencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateToValue()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun setupClickListeners() {
        binding.swapImageButton.setOnClickListener {
            val fromPosition = binding.fromCurrencySpinner.selectedItemPosition
            val toPosition = binding.toCurrencySpinner.selectedItemPosition
            binding.fromCurrencySpinner.setSelection(toPosition)
            binding.toCurrencySpinner.setSelection(fromPosition)
            updateToValue()
        }
        binding.detailsButton.setOnClickListener {
            val fromCurrency = binding.fromCurrencySpinner.selectedItem.toString()
            val toCurrency = binding.toCurrencySpinner.selectedItem.toString()
            if (fromCurrency == toCurrency)
                Toast.makeText(
                    requireContext(),
                    "Please select different currencies",
                    Toast.LENGTH_SHORT
                ).show()
            else findNavController().navigate(
                CurrencyConverterFragmentDirections.actionCurrencyConverterFragmentToDetailsFragment(
                    fromCurrency,
                    toCurrency
                )
            )
        }
        binding.fromEditText.addTextChangedListener {
            if (!isUpdatingEditText)
                updateToValue()
        }
        binding.toEditText.addTextChangedListener {
            if (!isUpdatingEditText)
                updateFromValue()
        }
        binding.swipeToRefresh.setOnRefreshListener {
//            fetchLatestRates()
        }
        binding.swipeToRefresh.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    private fun observeLatestRates() {
        viewModel.latestRatesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.swipeToRefresh.isRefreshing = true
                }

                is UiState.Success -> {
                    binding.swipeToRefresh.isRefreshing = false
                    binding.detailsButton.isEnabled = true
                    binding.lastUpdateTextView.text =
                        "Last update: " + state.data.timestamp.toDate()
                    rates = state.data.rates

                    // Get the list of currencies from the rates map
                    val currencies = rates?.keys?.toList()
                    if (currencies != null) {
                        setupSpinners(currencies)
                    }

                    updateToValue()
                }

                is UiState.Error -> {
                    binding.swipeToRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun Long.toDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date = Date(this * 1000)
        return sdf.format(date)
    }

    private fun fetchLatestRates() {
        if (!NetworkUtils.isNetworkAvailable())
            Toast.makeText(
                requireContext(),
                "Check your network and try again!",
                Toast.LENGTH_SHORT
            ).show()
        else
            viewModel.fetchLatestRates()
    }

    private fun updateToValue() {
        try {
            val fromCurrency = binding.fromCurrencySpinner.selectedItem.toString()
            val toCurrency = binding.toCurrencySpinner.selectedItem.toString()
            val fromAmountText = binding.fromTextInputLayout.editText?.text.toString()
            val fromAmount = fromAmountText.toDoubleOrNull() ?: 0.0

            // Get the conversion rate from the rates map
            val conversionRate = rates?.get(toCurrency)?.div(rates!![fromCurrency] ?: 1.0) ?: 0.0

            // Calculate the converted value
            val convertedValue = fromAmount * conversionRate

            // Format the converted value to display two decimal places
            val formattedValue = String.format("%.2f", convertedValue)

            // Set the converted value in the TO TextInputEditText
            isUpdatingEditText = true
            binding.toTextInputLayout.editText?.setText(formattedValue)
            isUpdatingEditText = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateFromValue() {
        try {
            val fromCurrency = binding.fromCurrencySpinner.selectedItem.toString()
            val toCurrency = binding.toCurrencySpinner.selectedItem.toString()

            // Get the value from the TO EditText
            val toAmountText = binding.toEditText.text.toString()
            val toAmount = toAmountText.toDoubleOrNull() ?: 0.0

            // Calculate the converted value
            val conversionRate = rates?.get(fromCurrency)?.div(rates!![toCurrency] ?: 1.0)
            val fromAmount = toAmount * conversionRate!!

            // Format the converted value to display two decimal places
            val formattedValue = String.format("%.2f", fromAmount)

            // Set the value in the FROM TextInputEditText
            isUpdatingEditText = true
            binding.fromEditText.setText(formattedValue)
            isUpdatingEditText = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

