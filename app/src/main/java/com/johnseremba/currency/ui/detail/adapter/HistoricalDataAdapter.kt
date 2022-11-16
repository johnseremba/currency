package com.johnseremba.currency.ui.detail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.johnseremba.currency.databinding.ListItemBinding
import com.johnseremba.currency.domain.helpers.CurrencyFormatHelper
import java.math.BigDecimal

private val diffCallback = object : DiffUtil.ItemCallback<Pair<String, BigDecimal>>() {
    override fun areItemsTheSame(oldItem: Pair<String, BigDecimal>, newItem: Pair<String, BigDecimal>): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Pair<String, BigDecimal>, newItem: Pair<String, BigDecimal>): Boolean {
        return oldItem.first == newItem.first && oldItem.second == newItem.second
    }
}

class HistoricalDataAdapter : ListAdapter<Pair<String, BigDecimal>, ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return HistoricalDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        (holder as HistoricalDataViewHolder).bind(item)
    }

    inner class HistoricalDataViewHolder(private val binding: ListItemBinding) : ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Pair<String, BigDecimal>) {
            with(binding) {
                tvTitle.text = data.first
                tvSubtitle.text = CurrencyFormatHelper.formatAmount(data.second)
            }
        }
    }
}
