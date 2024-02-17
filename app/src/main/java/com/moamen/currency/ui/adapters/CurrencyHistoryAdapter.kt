package com.moamen.currency.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moamen.currency.databinding.ItemCurrencyHistoryBinding
import com.moamen.currency.model.ConvertedHistoryModel

class CurrencyHistoryAdapter(var items: List<ConvertedHistoryModel>) :
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

        fun bind(item: ConvertedHistoryModel) {
            binding.convertedHistory = item
            binding.executePendingBindings()
        }
    }
}
