package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.TransactionBean
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.show
import ja.insepector.common.util.AppUtil
import ja.insepector.bxapp.databinding.ItemTransactionQueryBinding
import com.zrq.spanbuilder.TextStyle

class TransactionQueryAdapter(data: MutableList<TransactionBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<TransactionBean, ItemTransactionQueryBinding>(data) {
    val colors = intArrayOf(ja.insepector.base.R.color.color_ff0371f4, ja.insepector.base.R.color.color_ff0371f4, ja.insepector.base.R.color.color_ff0371f4)
    val colors2 = intArrayOf(ja.insepector.base.R.color.color_ffe92404, ja.insepector.base.R.color.color_ffe92404, ja.insepector.base.R.color.color_ffe92404)
    val sizes = intArrayOf(16, 20, 16)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)

    override fun convert(holder: VBViewHolder<ItemTransactionQueryBinding>, item: TransactionBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = item.carLicense
        holder.vb.tvStartTime.text = item.startTime
        holder.vb.tvEndTime.text = item.endTime
        holder.vb.tvNo.text = item.parkingNo
        if (item.hasPayed == "0") {
            val strings = arrayOf("已付：", item.payedAmount, "元")
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
            holder.vb.flNotification.show()
            holder.vb.flPaymentInquiry.gone()
            holder.vb.flNotification.tag = item
            holder.vb.flNotification.setOnClickListener(onClickListener)
        } else {
            val strings = arrayOf("欠：", item.oweMoney, "元")
            holder.vb.tvAmount.text = AppUtil.getSpan(strings, sizes, colors2, styles)
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