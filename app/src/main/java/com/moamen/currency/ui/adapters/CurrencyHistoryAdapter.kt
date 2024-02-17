package com.moamen.currency.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moamen.currency.databinding.ItemCurrencyHistoryBinding
import com.moamen.currency.model.CurrencyModel

class CurrencyHistoryAdapter(var items: List<CurrencyModel>) :
    RecyclerView.Adapter<CurrencyHistoryAdapter.CurrencyHistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCurrencyHistoryBinding.inflate(inflater, parent, false)
        return CurrencyHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CurrencyHistoryViewHolder(private val binding: ItemCurrencyHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CurrencyModel) {
            val fromCurrency: String = item.rates.keys.first()
            val toCurrency: String = item.rates.keys.last()
            binding.apply {
                textViewDate.text = item.date
                textViewFromCurrency.text = item.rates.keys.first()
                textViewFromValue.text = "1"
                textViewToCurrency.text = item.rates.keys.last()
                textViewToValue.text = calculateValue(fromCurrency, toCurrency, item.rates)
                item.rates.values.last().toString()
            }
        }
    }

    private fun calculateValue(
        fromCurrency: String,
        toCurrency: String,
        rates: Map<String, Double>
    ): String {
        val conversionRate = rates[toCurrency]?.div(rates[fromCurrency] ?: 1.0) ?: 0.0
        val convertedValue = 1 * conversionRate
        return String.format("%.2f", convertedValue)
    }
}
