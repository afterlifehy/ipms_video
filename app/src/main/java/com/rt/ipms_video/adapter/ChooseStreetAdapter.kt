package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.ipms_video.databinding.ItemChooseStreetBinding

class ChooseStreetAdapter(data: MutableList<Int>? = null) :
    BaseBindingAdapter<Int, ItemChooseStreetBinding>(data) {
    var checkedList: MutableList<Int> = ArrayList()

    override fun convert(holder: VBViewHolder<ItemChooseStreetBinding>, item: Int) {
        holder.vb.tvStreet.text = "定西路(愚园路～安化路)西侧"
        holder.vb.rlStreet.setOnClickListener {
            holder.vb.cbStreet.isChecked = !holder.vb.cbStreet.isChecked
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemChooseStreetBinding {
        return ItemChooseStreetBinding.inflate(inflater)
    }


//    fun getCheckedList(): MutableList<Int> {
//        return checkedList
//    }
}