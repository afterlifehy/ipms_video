package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.TransactionBean
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.ext.show
import com.peakinfo.common.util.AppUtil
import com.peakinfo.plateid.databinding.ItemTransactionQueryBinding
import com.zrq.spanbuilder.TextStyle

class TransactionQueryAdapter(data: MutableList<TransactionBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<TransactionBean, ItemTransactionQueryBinding>(data) {
    val colors = intArrayOf(
        com.peakinfo.base.R.color.color_ff0371f4,
        com.peakinfo.base.R.color.color_ff0371f4,
        com.peakinfo.base.R.color.color_ff0371f4
    )
    val colors2 = intArrayOf(
        com.peakinfo.base.R.color.color_ffe92404,
        com.peakinfo.base.R.color.color_ffe92404,
        com.peakinfo.base.R.color.color_ffe92404
    )
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)

    override fun convert(holder: VBViewHolder<ItemTransactionQueryBinding>, item: TransactionBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.carLicense
        holder.vb.tvStartTime.text = item.startTime
        holder.vb.tvEndTime.text = item.endTime
        holder.vb.tvNo.text = item.parkingNo
        if (item.hasPayed == "1") {
            val strings = arrayOf("已付：", item.payedAmount, "元")
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
            holder.vb.flNotification.show()
            holder.vb.flPaymentInquiry.gone()
            holder.vb.flNotification.tag = item
            holder.vb.flNotification.setOnClickListener(onClickListener)
        } else {
            val strings = arrayOf("未付：", item.oweMoney, "元")
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors2, styles)
            holder.vb.flNotification.gone()
            holder.vb.flPaymentInquiry.show()
            holder.vb.flPaymentInquiry.tag = item
            holder.vb.flPaymentInquiry.setOnClickListener(onClickListener)
        }
        if (item.orderType == "2") {
            holder.vb.rtvOrderType.text = i18n(com.peakinfo.base.R.string.场内支付)
            holder.vb.rtvOrderType.delegate.setTextColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.peakinfo.base.R.color.color_ff49b8d7
                )
            )
            holder.vb.rtvOrderType.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.peakinfo.base.R.color.color_ffe5f6f7
                )
            )
        } else {
            holder.vb.rtvOrderType.text = i18n(com.peakinfo.base.R.string.欠费追缴)
            holder.vb.rtvOrderType.delegate.setTextColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.peakinfo.base.R.color.color_ffd6b25a
                )
            )
            holder.vb.rtvOrderType.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.peakinfo.base.R.color.color_fffef3d5
                )
            )
        }
        holder.vb.rtvOrderType.delegate.init()
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTransactionQueryBinding {
        return ItemTransactionQueryBinding.inflate(inflater)
    }
}