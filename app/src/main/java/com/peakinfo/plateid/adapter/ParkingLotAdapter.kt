package com.peakinfo.plateid.adapter

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.base.adapter.VBViewHolder
import com.peakinfo.base.bean.ParkingLotBean
import com.peakinfo.base.ext.hide
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.ext.show
import com.peakinfo.plateid.databinding.ItemParkingLotBinding

class ParkingLotAdapter(data: MutableList<ParkingLotBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<ParkingLotBean, ItemParkingLotBinding>(data) {
    var plateBgMap: MutableMap<String, Int> = ArrayMap()
    var plateTxtColorMap: MutableMap<String, Int> = ArrayMap()

    init {
        plateBgMap["1"] = com.peakinfo.common.R.mipmap.ic_plate_bg_black
        plateBgMap["2"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["3"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["4"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["5"] = com.peakinfo.common.R.mipmap.ic_plate_bg_blue
        plateBgMap["6"] = com.peakinfo.common.R.mipmap.ic_plate_bg_yellow
        plateBgMap["7"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["8"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["9"] = com.peakinfo.common.R.mipmap.ic_plate_bg_green
        plateBgMap["10"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["11"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["12"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["13"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap["99"] = com.peakinfo.common.R.mipmap.ic_plate_bg_white

        plateTxtColorMap["1"] = com.peakinfo.base.R.color.white
        plateTxtColorMap["2"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["3"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["4"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["5"] = com.peakinfo.base.R.color.white
        plateTxtColorMap["6"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["7"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["8"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["9"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["10"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["11"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["12"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["13"] = com.peakinfo.base.R.color.black
        plateTxtColorMap["99"] = com.peakinfo.base.R.color.black
    }

    override fun convert(holder: VBViewHolder<ItemParkingLotBinding>, item: ParkingLotBean) {
        if (item.state == "01") {
            holder.vb.llParkingLotBg.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_bg_grey)
            holder.vb.tvParkingLotNum.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_num_bg_grey)
            holder.vb.tvPlate.text = i18n(com.peakinfo.base.R.string.空闲)
            holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.black))
            holder.vb.tvPlate.background = null
            holder.vb.rflParking.setOnClickListener(null)
        } else {
            if (item.deadLine > System.currentTimeMillis()) {
                holder.vb.llParkingLotBg.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_bg_green)
                holder.vb.tvParkingLotNum.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_num_bg_green)
            } else {
                holder.vb.llParkingLotBg.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_bg_red)
                holder.vb.tvParkingLotNum.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_num_bg_red)
            }
            holder.vb.tvPlate.text = item.carLicense
            if (item.carColor == "20") {
                holder.vb.llPlate.show()
                holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.black))
                holder.vb.tvPlate.background = null
            } else {
                holder.vb.llPlate.hide()
                holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), plateTxtColorMap[item.carColor]!!))
                holder.vb.tvPlate.background = plateBgMap[item.carColor]?.let { ContextCompat.getDrawable(BaseApplication.instance(), it) }
            }
            holder.vb.rflParking.tag = item
            holder.vb.rflParking.setOnClickListener(onClickListener)
        }
        holder.vb.tvParkingLotNum.text = item.parkingNo.substring(item.parkingNo.length - 3, item.parkingNo.length)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingLotBinding {
        return ItemParkingLotBinding.inflate(inflater)
    }
}