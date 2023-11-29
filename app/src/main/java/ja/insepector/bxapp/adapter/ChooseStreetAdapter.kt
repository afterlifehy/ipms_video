package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.bxapp.databinding.ItemChooseStreetBinding
import ja.insepector.base.bean.Street

class ChooseStreetAdapter(data: MutableList<Street>? = null, var streetChoosedList: MutableList<Street>) :
    BaseBindingAdapter<Street, ItemChooseStreetBinding>(data) {

    override fun convert(holder: VBViewHolder<ItemChooseStreetBinding>, item: Street) {
        holder.vb.tvStreet.text = item.streetName
        if (streetChoosedList.contains(item)) {
            holder.vb.cbStreet.isChecked = true
        } else {
            holder.vb.cbStreet.isChecked = false
        }
        holder.vb.rlStreet.setOnClickListener {
            holder.vb.cbStreet.isChecked = !item.ischeck
            if (holder.vb.cbStreet.isChecked) {
                streetChoosedList.add(item)
            } else {
                streetChoosedList.remove(item)
            }
        }
        holder.vb.cbStreet.setOnClickListener {
            if (holder.vb.cbStreet.isChecked) {
                streetChoosedList.add(item)
            } else {
                streetChoosedList.remove(item)
            }
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemChooseStreetBinding {
        return ItemChooseStreetBinding.inflate(inflater)
    }

//    fun getCheckedList(): MutableList<Int> {
//        return checkedList
//    }
}