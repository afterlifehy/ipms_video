package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.ipms_video.databinding.ItemParkingChooseStreetBinding

class ParkingChooseStreetAdapter(data: MutableList<Int>? = null, var currentStreet: Int) :
    BaseBindingAdapter<Int, ItemParkingChooseStreetBinding>(data) {
    var lastStreetCB: CheckBox? = null
    var currentStreetCB: CheckBox? = null
    override fun convert(holder: VBViewHolder<ItemParkingChooseStreetBinding>, item: Int) {
        holder.vb.tvStreet.text = "林泉路（仙霞路—泉口路）两侧"
        if (currentStreet == item) {
            holder.vb.cbStreet.isChecked = true
            currentStreetCB = holder.vb.cbStreet
        }
        holder.vb.rlStreet.setOnClickListener{
            lastStreetCB = currentStreetCB
            lastStreetCB?.isChecked = false
            currentStreetCB = holder.vb.cbStreet
            currentStreetCB?.isChecked = true
        }
        holder.vb.cbStreet.setOnClickListener{
            lastStreetCB = currentStreetCB
            lastStreetCB?.isChecked = false
            currentStreetCB = holder.vb.cbStreet
            currentStreetCB?.isChecked = true
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingChooseStreetBinding {
        return ItemParkingChooseStreetBinding.inflate(inflater)
    }
}