package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.TimeUtils
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.ca.UrgeOrderBean
import com.peakinfo.common.util.AppUtil
import com.peakinfo.plateid.databinding.ItemUrgeOrderBinding

class UrgeOrderAdapter(data: MutableList<UrgeOrderBean>? = null, val onclick: (order: UrgeOrderBean) -> Unit) :
    BaseBindingAdapter<UrgeOrderBean, ItemUrgeOrderBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemUrgeOrderBinding>, item: UrgeOrderBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvOrderNo.text = item.orderId
        holder.vb.tvStartTime.text = TimeUtils.millis2String(item.arrivedTime, "yyyy-MM-dd HH:mm:ss")
        holder.vb.tvEndTime.text = TimeUtils.millis2String(item.leftTime, "yyyy-MM-dd HH:mm:ss")
        holder.vb.tvOweAmount.text = "欠费：${AppUtil.keepNDecimal(item.oweMoney / 100.0, 2)}"
        holder.vb.rtvPay.setOnClickListener { onclick(item) }

    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemUrgeOrderBinding {
        return ItemUrgeOrderBinding.inflate(inflater)
    }
}