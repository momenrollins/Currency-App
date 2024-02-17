package com.moamen.currency.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moamen.currency.databinding.ItemConversionRateBinding


class ConversionRateAdapter(
    private val fromCurrency: String,
    private val rates: Map<String, Double>
) :
    RecyclerView.Adapter<ConversionRateAdapter.ConversionRateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionRateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemConversionRateBinding.inflate(inflater, parent, false)
        return ConversionRateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversionRateViewHolder, position: Int) {
        val currencyCodes = rates.keys.toList()
        val toCurrency = currencyCodes[position]
        val toValue = rates[toCurrency]

        holder.bind(toValue, toCurrency)
    }

    override fun getItemCount(): Int = rates.size


    inner class ConversionRateViewHolder(private val binding: ItemConversionRateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(toValue: Double?, toCurrency: String) {
            binding.textViewFromValue.text = "1 $fromCurrency"
            binding.textViewToValue.text = "${calculateValue(toValue ?: 0.0)} $toCurrency"
        }
    }

    private fun calculateValue(
        toValue: Double
    ): String {
        val fromValue: Double = rates[fromCurrency] ?: 0.0
        val conversionRate = toValue.div(fromValue)
        return String.format("%.2f", conversionRate)
    }
}

