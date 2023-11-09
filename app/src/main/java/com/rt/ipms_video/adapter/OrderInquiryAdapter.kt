package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.common.util.AppUtil
import com.rt.ipms_video.databinding.ItemOrderBinding
import com.zrq.spanbuilder.TextStyle

class OrderInquiryAdapter(data: MutableList<Int>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<Int, ItemOrderBinding>(data) {
    val colorsBlue = intArrayOf(com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4)
    val colorsRed = intArrayOf(com.rt.base.R.color.color_ffe92404, com.rt.base.R.color.color_ffe92404, com.rt.base.R.color.color_ffe92404)
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(com.rt.base.R.color.color_ff666666, com.rt.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)

    override fun convert(holder: VBViewHolder<ItemOrderBinding>, item: Int) {
        holder.vb.tvNum.text = AppUtil.fillZero(item.toString())
        holder.vb.tvLicensePlate.text = "浙AF128S "
        val strings = arrayOf("已付:", "165.00", "元")
        holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colorsBlue, styles)
        val strings2 = arrayOf("入场：", "2023-06-25 08:01:43")
        holder.vb.tvStartTime.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf("出场：", "2023-06-26 08:01:43")
        holder.vb.tvEndTime.text = AppUtil.getSpan(strings3, sizes2, colors2)
        holder.vb.tvNo.text = "CN-007-026"

        holder.vb.rllOrder.tag = item
        holder.vb.rllOrder.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemOrderBinding {
        return ItemOrderBinding.inflate(inflater)
    }
}