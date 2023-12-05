package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.Summary
import com.peakinfo.plateid.databinding.ItemSummaryBinding

class TodaySummaryAdapter(data: MutableList<Summary>? = null) : BaseBindingAdapter<Summary, ItemSummaryBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemSummaryBinding>, item: Summary) {
        holder.vb.tvStreetName.text = item.streetName
        holder.vb.tvTradeNum.text = item.number.toString()+"笔"
        holder.vb.tvTradeAmount.text = item.amount+"元"
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemSummaryBinding {
        val binding = ItemSummaryBinding.inflate(inflater)
        val lp = binding.rllTrade.layoutParams as LinearLayout.LayoutParams
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT
        binding.rllTrade.layoutParams = lp
        return binding
    }
}