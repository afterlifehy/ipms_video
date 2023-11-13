package com.rt.ipms_video.adapter

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.rt.base.BaseApplication
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.ext.hide
import com.rt.base.ext.i18n
import com.rt.base.ext.show
import com.rt.common.util.AppUtil
import com.rt.ipms_video.databinding.ItemParkingLotBinding

class ParkingLotAdapter(data: MutableList<Int>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<Int, ItemParkingLotBinding>(data) {
    var plateBgMap: MutableMap<Int, Int> = ArrayMap()
    var plateTxtColorMap: MutableMap<Int, Int> = ArrayMap()

    init {
        plateBgMap[1] = com.rt.common.R.mipmap.ic_plate_bg_black
        plateBgMap[2] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[3] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[4] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[5] = com.rt.common.R.mipmap.ic_plate_bg_blue
        plateBgMap[6] = com.rt.common.R.mipmap.ic_plate_bg_yellow
        plateBgMap[7] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[8] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[9] = com.rt.common.R.mipmap.ic_plate_bg_green
        plateBgMap[10] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[11] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[12] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[13] = com.rt.common.R.mipmap.ic_plate_bg_white
        plateBgMap[99] = com.rt.common.R.mipmap.ic_plate_bg_white

        plateTxtColorMap[1] = com.rt.base.R.color.white
        plateTxtColorMap[2] = com.rt.base.R.color.black
        plateTxtColorMap[3] = com.rt.base.R.color.black
        plateTxtColorMap[4] = com.rt.base.R.color.black
        plateTxtColorMap[5] = com.rt.base.R.color.white
        plateTxtColorMap[6] = com.rt.base.R.color.black
        plateTxtColorMap[7] = com.rt.base.R.color.black
        plateTxtColorMap[8] = com.rt.base.R.color.black
        plateTxtColorMap[9] = com.rt.base.R.color.black
        plateTxtColorMap[10] = com.rt.base.R.color.black
        plateTxtColorMap[11] = com.rt.base.R.color.black
        plateTxtColorMap[12] = com.rt.base.R.color.black
        plateTxtColorMap[13] = com.rt.base.R.color.black
        plateTxtColorMap[99] = com.rt.base.R.color.black
    }

    override fun convert(holder: VBViewHolder<ItemParkingLotBinding>, item: Int) {
        if (item == 0) {
            holder.vb.llParkingLotBg.setBackgroundResource(com.rt.common.R.mipmap.ic_parking_bg_grey)
            holder.vb.tvParkingLotNum.setBackgroundResource(com.rt.common.R.mipmap.ic_parking_num_bg_grey)
            holder.vb.tvPlate.text = i18n(com.rt.base.R.string.空闲)
            holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.black))
            holder.vb.tvPlate.background = null
            holder.vb.rflParking.setOnClickListener(null)
        } else {
            holder.vb.llParkingLotBg.setBackgroundResource(com.rt.common.R.mipmap.ic_parking_bg_red)
            holder.vb.tvParkingLotNum.setBackgroundResource(com.rt.common.R.mipmap.ic_parking_num_bg_red)
            holder.vb.tvPlate.text = "沪AWD9331"
            if (item == 20) {
                holder.vb.llPlate.show()
                holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.black))
                holder.vb.tvPlate.background = null
            } else {
                holder.vb.llPlate.hide()
                holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), plateTxtColorMap[item]!!))
                holder.vb.tvPlate.background = plateBgMap[item]?.let { ContextCompat.getDrawable(BaseApplication.instance(), it) }
            }
            holder.vb.rflParking.tag = item
            holder.vb.rflParking.setOnClickListener(onClickListener)
        }
        holder.vb.tvParkingLotNum.text = AppUtil.fillZero((data.indexOf(item) + 1).toString())
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingLotBinding {
        return ItemParkingLotBinding.inflate(inflater)
    }
}