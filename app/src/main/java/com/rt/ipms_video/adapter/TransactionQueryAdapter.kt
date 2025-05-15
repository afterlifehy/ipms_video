package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.rt.base.BaseApplication
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.bean.TransactionBean
import com.rt.base.ext.gone
import com.rt.base.ext.i18n
import com.rt.base.ext.show
import com.rt.common.util.AppUtil
import com.rt.ipms_video.databinding.ItemTransactionQueryBinding
import com.zrq.spanbuilder.TextStyle

class TransactionQueryAdapter(data: MutableList<TransactionBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<TransactionBean, ItemTransactionQueryBinding>(data) {
    val colors = intArrayOf(
        com.rt.base.R.color.color_ff0371f4,
        com.rt.base.R.color.color_ff0371f4,
        com.rt.base.R.color.color_ff0371f4
    )
    val colors2 = intArrayOf(
        com.rt.base.R.color.color_ffe92404,
        com.rt.base.R.color.color_ffe92404,
        com.rt.base.R.color.color_ffe92404
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
        if (item.orderType == "1") {
            holder.vb.rtvOrderType.show()
            holder.vb.rtvOrderType.text = i18n(com.rt.base.R.string.预支付)
            holder.vb.rtvOrderType.delegate.setTextColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.rt.base.R.color.color_ff49b8d7
                )
            )
            holder.vb.rtvOrderType.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.rt.base.R.color.color_ffe5f6f7
                )
            )
        } else if (item.orderType == "2") {
            holder.vb.rtvOrderType.show()
            holder.vb.rtvOrderType.text = i18n(com.rt.base.R.string.场内支付)
            holder.vb.rtvOrderType.delegate.setTextColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.rt.base.R.color.color_ff49b8d7
                )
            )
            holder.vb.rtvOrderType.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.rt.base.R.color.color_ffe5f6f7
                )
            )
        } else {
            holder.vb.rtvOrderType.show()
            holder.vb.rtvOrderType.text = i18n(com.rt.base.R.string.欠费追缴)
            holder.vb.rtvOrderType.delegate.setTextColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.rt.base.R.color.color_ffd6b25a
                )
            )
            holder.vb.rtvOrderType.delegate.setBackgroundColor(
                ContextCompat.getColor(
                    BaseApplication.instance(),
                    com.rt.base.R.color.color_fffef3d5
                )
            )
        }
        holder.vb.rtvOrderType.delegate.init()
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTransactionQueryBinding {
        return ItemTransactionQueryBinding.inflate(inflater)
    }
}