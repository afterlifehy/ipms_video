package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.ca.UrgeMonthBean
import com.peakinfo.common.util.AppUtil
import com.peakinfo.plateid.databinding.ItemUrgeMonthBinding

class UrgeMonthAdapter(data: MutableList<UrgeMonthBean>? = null, val onclick: (order: UrgeMonthBean) -> Unit) :
    BaseBindingAdapter<UrgeMonthBean, ItemUrgeMonthBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemUrgeMonthBinding>, item: UrgeMonthBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvMonth.text = "月结月份：${item.month}"
        holder.vb.tvMonthPayId.text = item.monthPayId
        holder.vb.tvOweAmount.text = "欠费：${AppUtil.keepNDecimal(item.oweAmount / 100.0, 2)}"
        holder.vb.rtvPay.setOnClickListener { onclick(item) }

    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemUrgeMonthBinding {
        return ItemUrgeMonthBinding.inflate(inflater)
    }
}