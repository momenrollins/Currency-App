package com.moamen.currency.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.moamen.currency.databinding.FragmentDetailsBinding
import com.moamen.currency.ui.adapters.ConversionRateAdapter
import com.moamen.currency.ui.adapters.CurrencyHistoryAdapter
import com.moamen.currency.util.UiState
import com.moamen.currency.viewmodels.CurrencyViewModel

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var historyAdapter: CurrencyHistoryAdapter
    private lateinit var conversionRateAdapter: ConversionRateAdapter
    private val viewModel: CurrencyViewModel by activityViewModels()
    private val args by navArgs<DetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeHistory()
//        fetchHistoricalData()
    }

    private fun setupViews() {
        historyAdapter = CurrencyHistoryAdapter(emptyList())
        val sortedRates = viewModel.latestRates?.rates?.toList()?.sortedByDescending { it.second }
        val popularRates = sortedRates?.take(10)?.toMap()
        conversionRateAdapter = if (popularRates != null) {
            ConversionRateAdapter(args.fromCurrency, popularRates)
        } else
            ConversionRateAdapter(args.fromCurrency, viewModel.latestRates!!.rates)
        binding.historyRecyclerView.adapter = historyAdapter
        binding.conversionRateRecyclerView.adapter = conversionRateAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeHistory() {
        viewModel.historyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val historicalData = state.data
                    historyAdapter.items = historicalData
                    historyAdapter.notifyDataSetChanged()
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchHistoricalData() {
        viewModel.fetchHistoricalData("${args.fromCurrency},${args.toCurrency}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}