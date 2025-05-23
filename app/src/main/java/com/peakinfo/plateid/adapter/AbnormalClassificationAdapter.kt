package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.plateid.databinding.ItemParkingChooseStreetBinding

class AbnormalClassificationAdapter(data: MutableList<String>? = null, var currentClassification: String, val callback: ChooseClassificationAdapterCallBack) :
    BaseBindingAdapter<String, ItemParkingChooseStreetBinding>(data) {
    var lastClassificationCB: CheckBox? = null
    var currentClassificationCB: CheckBox? = null
    override fun convert(holder: VBViewHolder<ItemParkingChooseStreetBinding>, item: String) {
        holder.vb.tvStreet.text = item
        if (currentClassification == item) {
            holder.vb.cbStreet.isChecked = true
            currentClassificationCB = holder.vb.cbStreet
        }
        holder.vb.rlStreet.setOnClickListener {
            lastClassificationCB = currentClassificationCB
            lastClassificationCB?.isChecked = false
            currentClassificationCB = holder.vb.cbStreet
            currentClassificationCB?.isChecked = true
            currentClassification = item
            callback.chooseClassification(currentClassification)
        }
        holder.vb.cbStreet.setOnClickListener {
            lastClassificationCB = currentClassificationCB
            lastClassificationCB?.isChecked = false
            currentClassificationCB = holder.vb.cbStreet
            currentClassificationCB?.isChecked = true
            currentClassification = item
            callback.chooseClassification(currentClassification)
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingChooseStreetBinding {
        return ItemParkingChooseStreetBinding.inflate(inflater)
    }

    interface ChooseClassificationAdapterCallBack {
        fun chooseClassification(classification: String)
    }
}