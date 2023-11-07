package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.ext.gone
import com.rt.base.ext.show
import com.rt.common.util.AppUtil
import com.rt.ipms_video.databinding.ItemTransactionQueryBinding
import com.zrq.spanbuilder.TextStyle

class TransactionQueryAdapter(data: MutableList<Int>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<Int, ItemTransactionQueryBinding>(data) {
    val colors = intArrayOf(com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4)
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)
    override fun convert(holder: VBViewHolder<ItemTransactionQueryBinding>, item: Int) {
        holder.vb.tvNum.text = AppUtil.fillZero(item.toString())
        holder.vb.tvLicensePlate.text = "浙AF128S "
        val strings = arrayOf("已付:", "165.00", "元")
        holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
        holder.vb.tvStartTime.text = "2023-06-25 11:07:25"
        holder.vb.tvEndTime.text = "2023-06-25 11:07:25"
        holder.vb.tvNo.text = "CN-007-026"
        if (item == 0) {
            holder.vb.flNotification.show()
            holder.vb.flPaymentInquiry.gone()
            holder.vb.flNotification.tag = item
            holder.vb.flNotification.setOnClickListener(onClickListener)
        } else {
            holder.vb.flNotification.gone()
            holder.vb.flPaymentInquiry.show()
            holder.vb.flPaymentInquiry.tag = item
            holder.vb.flPaymentInquiry.setOnClickListener(onClickListener)
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTransactionQueryBinding {
        return ItemTransactionQueryBinding.inflate(inflater)
    }
}