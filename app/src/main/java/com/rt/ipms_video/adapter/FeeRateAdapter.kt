package com.rt.ipms_video.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.bean.FeeRateBean
import com.rt.base.ext.i18n
import com.rt.ipms_video.databinding.ItemFeeRateBinding

class FeeRateAdapter(data: MutableList<FeeRateBean>? = null) : BaseBindingAdapter<FeeRateBean, ItemFeeRateBinding>(data) {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: VBViewHolder<ItemFeeRateBinding>, item: FeeRateBean) {
        when (item.dateType) {
            1 -> {
                holder.vb.tvTitle.text = i18n(com.rt.base.R.string.工作日标准)
            }

            2 -> {
                holder.vb.tvTitle.text = i18n(com.rt.base.R.string.周末标准)
            }

            3 -> {
                holder.vb.tvTitle.text = i18n(com.rt.base.R.string.节假日标准)
            }
        }
        holder.vb.tvDayTime.text = "${item.whiteStart}至${item.whiteEnd}"
        holder.vb.tvNightTime.text = "${item.blackStart}至${item.blackEnd}"
        holder.vb.rtvStartAmount.text = "${item.first}元"
        holder.vb.tvCenterAmount.text = "${item.second}元"
        holder.vb.rtvEndAmount.text = "${item.third}元"
        holder.vb.tvRemark.text = "计次：${item.unitPrice}元/次"
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFeeRateBinding {
        val binding = ItemFeeRateBinding.inflate(inflater)
        binding.rllFeeRate.layoutParams.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(26f)
        binding.rllFeeRate.requestLayout()
        return binding
    }
}