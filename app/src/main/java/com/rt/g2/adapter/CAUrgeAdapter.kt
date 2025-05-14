package com.rt.g2.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.blankj.utilcode.util.TimeUtils
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.bean.ca.UrgeBean
import com.rt.common.util.AppUtil
import com.rt.g2.databinding.ItemUrgeBinding

class CAUrgeAdapter(data: MutableList<UrgeBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<UrgeBean, ItemUrgeBinding>(data) {
    var carLicense = ""

    override fun convert(holder: VBViewHolder<ItemUrgeBinding>, item: UrgeBean) {
        holder.vb.tvNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
        holder.vb.tvLicensePlate.text = carLicense
        holder.vb.tvNo.text = item.districtId
        holder.vb.tvTime.text = TimeUtils.millis2String(item.dataTime,"yyyy-MM-dd HH:mm:ss")

        holder.vb.rrlUrge.tag = item
        holder.vb.rrlUrge.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemUrgeBinding {
        return ItemUrgeBinding.inflate(inflater)
    }

    fun updateCarLicense(carLicense: String) {
        this.carLicense = carLicense
    }
}