package ja.insepector.bxapp.adapter

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ja.insepector.base.BaseApplication
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.ParkingLotBean
import ja.insepector.base.ext.hide
import ja.insepector.base.ext.i18n
import ja.insepector.base.ext.show
import ja.insepector.bxapp.databinding.ItemParkingLotBinding

class ParkingLotAdapter(data: MutableList<ParkingLotBean>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<ParkingLotBean, ItemParkingLotBinding>(data) {
    var plateBgMap: MutableMap<String, Int> = ArrayMap()
    var plateTxtColorMap: MutableMap<String, Int> = ArrayMap()

    init {
        plateBgMap["1"] = ja.insepector.common.R.mipmap.ic_plate_bg_black
        plateBgMap["2"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["3"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["4"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["5"] = ja.insepector.common.R.mipmap.ic_plate_bg_blue
        plateBgMap["6"] = ja.insepector.common.R.mipmap.ic_plate_bg_yellow
        plateBgMap["7"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["8"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["9"] = ja.insepector.common.R.mipmap.ic_plate_bg_green
        plateBgMap["10"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["11"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["12"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["13"] = ja.insepector.common.R.mipmap.ic_plate_bg_white
        plateBgMap["99"] = ja.insepector.common.R.mipmap.ic_plate_bg_white

        plateTxtColorMap["1"] = ja.insepector.base.R.color.white
        plateTxtColorMap["2"] = ja.insepector.base.R.color.black
        plateTxtColorMap["3"] = ja.insepector.base.R.color.black
        plateTxtColorMap["4"] = ja.insepector.base.R.color.black
        plateTxtColorMap["5"] = ja.insepector.base.R.color.white
        plateTxtColorMap["6"] = ja.insepector.base.R.color.black
        plateTxtColorMap["7"] = ja.insepector.base.R.color.black
        plateTxtColorMap["8"] = ja.insepector.base.R.color.black
        plateTxtColorMap["9"] = ja.insepector.base.R.color.black
        plateTxtColorMap["10"] = ja.insepector.base.R.color.black
        plateTxtColorMap["11"] = ja.insepector.base.R.color.black
        plateTxtColorMap["12"] = ja.insepector.base.R.color.black
        plateTxtColorMap["13"] = ja.insepector.base.R.color.black
        plateTxtColorMap["99"] = ja.insepector.base.R.color.black
    }

    override fun convert(holder: VBViewHolder<ItemParkingLotBinding>, item: ParkingLotBean) {
        if (item.state == "01") {
            holder.vb.llParkingLotBg.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_bg_grey)
            holder.vb.tvParkingLotNum.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_num_bg_grey)
            holder.vb.tvPlate.text = i18n(ja.insepector.base.R.string.空闲)
            holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.black))
            holder.vb.tvPlate.background = null
            holder.vb.rflParking.setOnClickListener(null)
        } else {
            holder.vb.llParkingLotBg.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_bg_red)
            holder.vb.tvParkingLotNum.setBackgroundResource(ja.insepector.common.R.mipmap.ic_parking_num_bg_red)
            holder.vb.tvPlate.text = item.carLicense
            if (item.carColor == "20") {
                holder.vb.llPlate.show()
                holder.vb.tvPlate.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.black))
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