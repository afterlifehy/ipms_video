package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.blankj.utilcode.util.SizeUtils
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.plateid.databinding.ItemChooseStreetBinding
import com.peakinfo.base.bean.Street

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
        val lp = ViewGroup.MarginLayoutParams(WindowManager.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(60f))
        val binding = ItemChooseStreetBinding.inflate(inflater)
        binding.root.layoutParams = lp
        return binding
    }

//    fun getCheckedList(): MutableList<Int> {
//        return checkedList
//    }
}