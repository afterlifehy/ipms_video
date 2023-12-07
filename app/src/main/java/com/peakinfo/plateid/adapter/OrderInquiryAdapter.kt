package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.blankj.utilcode.util.SizeUtils
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.OrderBean
import com.peakinfo.base.ext.i18n
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.BigDecimalManager
import com.peakinfo.plateid.databinding.ItemOrderBinding
import com.zrq.spanbuilder.TextStyle

class OrderInquiryAdapter(data: MutableList<OrderBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<OrderBean, ItemOrderBinding>(data) {
    val colorsBlue = intArrayOf(
        com.peakinfo.base.R.color.color_ff0371f4,
        com.peakinfo.base.R.color.color_ff0371f4,
        com.peakinfo.base.R.color.color_ff0371f4
    )
    val colorsRed = intArrayOf(
        com.peakinfo.base.R.color.color_ffe92404,
        com.peakinfo.base.R.color.color_ffe92404,
        com.peakinfo.base.R.color.color_ffe92404
    )
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    val colors2 = intArrayOf(com.peakinfo.base.R.color.color_ff666666, com.peakinfo.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)

    override fun convert(holder: VBViewHolder<ItemOrderBinding>, item: OrderBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.carLicense
        if (item.paidAmount.toDouble() > 0.0) {
            val strings = arrayOf(
                i18n(com.peakinfo.base.R.string.已付),
                AppUtil.keepNDecimals(item.paidAmount, 2),
                i18n(com.peakinfo.base.R.string.元)
            )
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colorsBlue, styles)
        } else if (item.paidAmount.toDouble() == 0.0 && item.amount.toDouble() == 0.0) {
            val strings = arrayOf(
                i18n(com.peakinfo.base.R.string.已付),
                AppUtil.keepNDecimals(item.paidAmount, 2),
                i18n(com.peakinfo.base.R.string.元)
            )
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colorsBlue, styles)
        } else {
            val strings = arrayOf(
                i18n(com.peakinfo.base.R.string.欠),
                AppUtil.keepNDecimals(BigDecimalManager.subtractionDoubleToString(item.amount.toDouble(), item.paidAmount.toDouble()), 2),
                i18n(com.peakinfo.base.R.string.元)
            )
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colorsRed, styles)
        }

        val strings2 = arrayOf(i18n(com.peakinfo.base.R.string.入场) + ":", item.startTime)
        holder.vb.tvStartTime.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18n(com.peakinfo.base.R.string.出场) + ":", item.endTime)
        holder.vb.tvEndTime.text = AppUtil.getSpan(strings3, sizes2, colors2)
        holder.vb.tvNo.text = item.parkingNo

        holder.vb.rllOrder.tag = item
        holder.vb.rllOrder.setOnClickListener(onClickListener)
    }


    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemOrderBinding {
//        val lp = ViewGroup.MarginLayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        lp.bottomMargin = SizeUtils.dp2px(20f)
        val binding = ItemOrderBinding.inflate(inflater)
//        binding.rllOrder.layoutParams = lp
        return binding
    }
}