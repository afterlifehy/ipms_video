package com.peakinfo.plateid.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.FeeRateBean
import com.peakinfo.base.ext.i18n
import com.peakinfo.plateid.databinding.ItemFeeRateBinding

class FeeRateAdapter(data: MutableList<FeeRateBean>? = null) : BaseBindingAdapter<FeeRateBean, ItemFeeRateBinding>(data) {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: VBViewHolder<ItemFeeRateBinding>, item: FeeRateBean) {
        when (item.dateType) {
            1 -> {
                holder.vb.tvTitle.text = i18n(com.peakinfo.base.R.string.工作日标准)
            }

            2 -> {
                holder.vb.tvTitle.text = i18n(com.peakinfo.base.R.string.周末标准)
            }

            3 -> {
                holder.vb.tvTitle.text = i18n(com.peakinfo.base.R.string.节假日标准)
            }
        }
        holder.vb.tvDayTime.text = "${item.whiteStart}至${item.whiteEnd}"
        holder.vb.tvNightTime.text = "${item.blackStart}至${item.blackEnd}"
        holder.vb.rtvStartAmount.text = "${item.first}元"
        holder.vb.tvCenterAmount.text = "${item.second}元"
        holder.vb.rtvEndAmount.text = "${item.third}元"
        holder.vb.tvRemark.text = "${item.unitPrice}。计次：${item.period}元/次"
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFeeRateBinding {
        val binding = ItemFeeRateBinding.inflate(inflater)
        binding.rllFeeRate.layoutParams.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(26f)
        binding.rllFeeRate.requestLayout()
        return binding
    }
}