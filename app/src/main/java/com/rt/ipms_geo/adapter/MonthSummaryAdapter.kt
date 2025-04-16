package com.rt.ipms_geo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.bean.Summary
import com.rt.ipms_geo.databinding.ItemSummaryBinding

class MonthSummaryAdapter(data: MutableList<Summary>? = null) : BaseBindingAdapter<Summary, ItemSummaryBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemSummaryBinding>, item: Summary) {
        holder.vb.tvStreetName.text = item.streetName
        holder.vb.tvTradeNum.text = item.number.toString()+"笔"
        holder.vb.tvTradeAmount.text = item.amount+"元"
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemSummaryBinding {
        return ItemSummaryBinding.inflate(inflater)
    }
}