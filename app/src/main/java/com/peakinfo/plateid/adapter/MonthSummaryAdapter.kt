package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.Summary
import com.peakinfo.plateid.databinding.ItemMonthSummaryBinding
import com.peakinfo.plateid.databinding.ItemTodaySummaryBinding

class MonthSummaryAdapter(data: MutableList<Summary>? = null) : BaseBindingAdapter<Summary, ItemMonthSummaryBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemMonthSummaryBinding>, item: Summary) {
        holder.vb.tvStreetName.text = item.streetName
        holder.vb.tvTradeNum.text = item.number.toString()+"笔"
        holder.vb.tvTradeAmount.text = item.amount+"元"
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemMonthSummaryBinding {
        return ItemMonthSummaryBinding.inflate(inflater)
    }
}