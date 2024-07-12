package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aries.ui.view.radius.RadiusTextView
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
        when (item.orderType) {
            "1" -> {
                showOrderType(
                    holder.vb.rtvOrderType,
                    "预付费",
                    com.peakinfo.base.R.color.color_ffd6b25a,
                    com.peakinfo.base.R.color.color_fffef3d5
                )
            }

            "2" -> {
                showOrderType(
                    holder.vb.rtvOrderType,
                    "场内支付",
                    com.peakinfo.base.R.color.color_ff49b8d7,
                    com.peakinfo.base.R.color.color_ffe5f6f7
                )
            }

            "3" -> {
                showOrderType(
                    holder.vb.rtvOrderType,
                    "欠费追缴",
                    com.peakinfo.base.R.color.color_ffd6b25a,
                    com.peakinfo.base.R.color.color_fffef3d5
                )
            }

            else -> {
                holder.vb.rtvOrderType.gone()
            }
        }
        holder.vb.rtvOrderType.delegate.init()
    }

    fun showOrderType(rtvOrderType: RadiusTextView, content: String, color1: Int, color2: Int) {
        rtvOrderType.show()
        rtvOrderType.text = content
        rtvOrderType.delegate.setTextColor(
            ContextCompat.getColor(
                BaseApplication.instance(),
                color1
            )
        )
        rtvOrderType.delegate.setBackgroundColor(
            ContextCompat.getColor(
                BaseApplication.instance(),
                color2
            )
        )
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTransactionQueryBinding {
        return ItemTransactionQueryBinding.inflate(inflater)
    }
}