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
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.hide
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.ext.show
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.Constant
import com.peakinfo.plateid.databinding.ItemParkingLotBinding

class ParkingLotAdapter(data: MutableList<ParkingLotBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<ParkingLotBean, ItemParkingLotBinding>(data) {
    var plateBgMap: MutableMap<String, Int> = ArrayMap()
    var plateTxtColorMap: MutableMap<String, Int> = ArrayMap()
    var colors = intArrayOf(com.peakinfo.base.R.color.color_ffeb0000, com.peakinfo.base.R.color.black)
    var colors2 = intArrayOf(com.peakinfo.base.R.color.black, com.peakinfo.base.R.color.color_ffeb0000)
    var sizes = intArrayOf(24, 24)

    init {
        plateBgMap[Constant.BLACK] = com.peakinfo.common.R.mipmap.ic_plate_bg_black
        plateBgMap[Constant.WHITE] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.GREY] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.RED] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.BLUE] = com.peakinfo.common.R.mipmap.ic_plate_bg_blue
        plateBgMap[Constant.YELLOW] = com.peakinfo.common.R.mipmap.ic_plate_bg_yellow
        plateBgMap[Constant.ORANGE] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.BROWN] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.GREEN] = com.peakinfo.common.R.mipmap.ic_plate_bg_green
        plateBgMap[Constant.PURPLE] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.CYAN] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.PINK] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.TRANSPARENT] = com.peakinfo.common.R.mipmap.ic_plate_bg_white
        plateBgMap[Constant.OTHERS] = com.peakinfo.common.R.mipmap.ic_plate_bg_white

        plateTxtColorMap[Constant.BLACK] = com.peakinfo.base.R.color.white
        plateTxtColorMap[Constant.WHITE] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.GREY] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.RED] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.BLUE] = com.peakinfo.base.R.color.white
        plateTxtColorMap[Constant.YELLOW] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.ORANGE] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.BROWN] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.GREEN] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.PURPLE] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.CYAN] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.PINK] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.TRANSPARENT] = com.peakinfo.base.R.color.black
        plateTxtColorMap[Constant.OTHERS] = com.peakinfo.base.R.color.black
    }

    override fun convert(holder: VBViewHolder<ItemParkingLotBinding>, item: ParkingLotBean) {
        if (item.state == "01") {
            holder.vb.llParkingLotBg.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_bg_grey)
            holder.vb.tvParkingLotNum.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_num_bg_grey)
            holder.vb.tvPlate.text = i18n(com.peakinfo.base.R.string.空闲)
            holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.black))
            holder.vb.tvPlate.background = null
            holder.vb.rflParking.tag = item
            holder.vb.rflParking.setOnClickListener(onClickListener)
        } else {
            if (item.deadLine > System.currentTimeMillis()) {
                holder.vb.llParkingLotBg.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_bg_green)
                holder.vb.tvParkingLotNum.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_num_bg_green)
            } else {
                holder.vb.llParkingLotBg.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_bg_red)
                holder.vb.tvParkingLotNum.setBackgroundResource(com.peakinfo.common.R.mipmap.ic_parking_num_bg_red)
            }
            if (item.carColor == "20") {
                holder.vb.llPlate.show()
                holder.vb.tvPlate.gone()
                holder.vb.tvPlate1.text = item.carLicense.substring(0, 2)
                holder.vb.tvPlate2.text = item.carLicense.substring(2, item.carLicense.length)
                holder.vb.tvPlate1.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.black))
                holder.vb.tvPlate2.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.black))
            } else {
                holder.vb.llPlate.hide()
                holder.vb.tvPlate.show()
                if (item.carLicense.startsWith("WJ")) {
                    val strings = arrayOf("WJ", item.carLicense.substring(2, item.carLicense.length))
                    holder.vb.tvPlate.text = AppUtil.getSpan(strings, sizes, colors)
                } else if (item.carLicense.contains("警")) {
                    val strings = arrayOf(item.carLicense.substring(0, item.carLicense.length - 1), "警")
                    holder.vb.tvPlate.text = AppUtil.getSpan(strings, sizes, colors2)
                } else {
                    holder.vb.tvPlate.text = item.carLicense
                }
                holder.vb.tvPlate.background =
                    plateBgMap[item.carColor]?.let { ContextCompat.getDrawable(BaseApplication.instance(), it) }
                holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), plateTxtColorMap[item.carColor]!!))
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