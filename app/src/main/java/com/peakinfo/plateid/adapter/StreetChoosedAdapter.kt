package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.Street
import com.peakinfo.plateid.databinding.ItemStreetChoosedBinding

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