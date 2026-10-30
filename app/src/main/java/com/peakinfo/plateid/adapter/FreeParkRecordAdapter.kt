package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.TimeUtils
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.ca.FreeParkDetailBean
import com.peakinfo.common.util.AppUtil
import com.peakinfo.plateid.databinding.ItemFreeParkDetailBinding
import com.zrq.spanbuilder.TextStyle

class FreeParkRecordAdapter(data: MutableList<FreeParkDetailBean>? = null, val onClickListener: View.OnClickListener) :
    BaseBindingAdapter<FreeParkDetailBean, ItemFreeParkDetailBinding>(data) {
    val colors = intArrayOf(com.peakinfo.base.R.color.color_ffe92404, com.peakinfo.base.R.color.color_ffe92404, com.peakinfo.base.R.color.color_ffe92404)
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(com.peakinfo.base.R.color.color_ff666666, com.peakinfo.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)

    override fun convert(holder: VBViewHolder<ItemFreeParkDetailBinding>, item: FreeParkDetailBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.plateId
        val strings2 = arrayOf("入场：", TimeUtils.millis2String(item.arrivedTime,"yyyy-MM-dd HH:mm:ss"))
        holder.vb.tvStartTime.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf("出场：", TimeUtils.millis2String(item.leftTime, "yyyy-MM-dd HH:mm:ss"))
        holder.vb.tvEndTime.text = AppUtil.getSpan(strings3, sizes2, colors2)
        holder.vb.tvAddress.text = item.roadName
        holder.vb.tvNo.text = item.berthId

        holder.vb.rrlDebtCollection.tag = item
        holder.vb.rrlDebtCollection.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFreeParkDetailBinding {
        return ItemFreeParkDetailBinding.inflate(inflater)
    }

}