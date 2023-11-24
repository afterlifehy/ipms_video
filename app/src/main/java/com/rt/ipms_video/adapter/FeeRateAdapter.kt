package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.ext.i18n
import com.rt.ipms_video.databinding.ItemFeeRateBinding

class FeeRateAdapter(data: MutableList<Int>? = null) : BaseBindingAdapter<Int, ItemFeeRateBinding>(data) {
    override fun convert(holder: VBViewHolder<ItemFeeRateBinding>, item: Int) {
        when (item) {
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
        holder.vb.tvDayTime.text = "08:00至22:00"
        holder.vb.tvNightTime.text = "22:00-次日07:00"
        holder.vb.rtvStartTime.text = "首15分钟"
        holder.vb.tvCenterTime.text = "后15分钟"
        holder.vb.rtvEndTime.text = "后30分钟"
        holder.vb.rtvStartAmount.text = "4元"
        holder.vb.tvCenterAmount.text = "7元"
        holder.vb.rtvEndAmount.text = "10元"
        holder.vb.tvRemark.text = "后续每30分钟10元。计次：10元/次"
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFeeRateBinding {
        val binding = ItemFeeRateBinding.inflate(inflater)
        binding.rllFeeRate.layoutParams.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(26f)
        binding.rllFeeRate.requestLayout()
        return binding
    }
}