package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.TransactionBean
import ja.insepector.bxapp.databinding.ItemTransactionRecordBinding

class TransactionRecordAdapter(data: MutableList<TransactionBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<TransactionBean, ItemTransactionRecordBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemTransactionRecordBinding>, item: TransactionBean) {
        holder.vb.tvOrderNo.text = item.tradeNo
        holder.vb.tvAmount.text = "${item.payedAmount}元"
        holder.vb.tvStartTime.text = item.startTime
        holder.vb.tvEndTime.text = item.endTime
        holder.vb.flNotification.tag = item
        holder.vb.flNotification.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTransactionRecordBinding {
        return ItemTransactionRecordBinding.inflate(inflater)
    }
}