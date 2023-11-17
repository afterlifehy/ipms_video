package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.ipms_video.databinding.ItemChooseStreetBinding
import com.rt.base.bean.Street

class ChooseStreetAdapter(data: MutableList<Street>? = null) :
    BaseBindingAdapter<Street, ItemChooseStreetBinding>(data) {
    var checkedList: MutableList<Street> = ArrayList()

    override fun convert(holder: VBViewHolder<ItemChooseStreetBinding>, item: Street) {
        holder.vb.tvStreet.text = item.streetName
        holder.vb.rlStreet.setOnClickListener {
            holder.vb.cbStreet.isChecked = !holder.vb.cbStreet.isChecked
            if (holder.vb.cbStreet.isChecked) {
                checkedList.add(item)
            } else {
                checkedList.remove(item)
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