package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.ipms_video.databinding.ItemStreetChoosedBinding

class StreetChoosedAdapter(var streetChoosedList: MutableList<String>, val onClickListener: OnClickListener) :
    BaseBindingAdapter<String, ItemStreetChoosedBinding>() {

    override fun convert(holder: VBViewHolder<ItemStreetChoosedBinding>, item: String) {
        holder.vb.tvStreet.text = item
        holder.vb.rflDelete.tag = item
        holder.vb.rflDelete.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemStreetChoosedBinding {
        return ItemStreetChoosedBinding.inflate(inflater)
    }
}