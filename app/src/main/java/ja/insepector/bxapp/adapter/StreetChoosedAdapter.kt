package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.Street
import ja.insepector.bxapp.databinding.ItemStreetChoosedBinding

class StreetChoosedAdapter(var streetChoosedList: MutableList<Street>, val onClickListener: OnClickListener) :
    BaseBindingAdapter<Street, ItemStreetChoosedBinding>() {

    override fun convert(holder: VBViewHolder<ItemStreetChoosedBinding>, item: Street) {
        holder.vb.tvStreet.text = item.streetName
        holder.vb.rflDelete.tag = item
        holder.vb.rflDelete.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemStreetChoosedBinding {
        return ItemStreetChoosedBinding.inflate(inflater)
    }
}