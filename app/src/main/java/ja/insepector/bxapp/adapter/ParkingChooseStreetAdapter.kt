package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.bean.Street
import ja.insepector.bxapp.databinding.ItemParkingChooseStreetBinding

class ParkingChooseStreetAdapter(data: MutableList<Street>? = null, var currentStreet: Street, val callback: ChooseStreetAdapterCallBack) :
    BaseBindingAdapter<Street, ItemParkingChooseStreetBinding>(data) {
    var lastStreetCB: CheckBox? = null
    var currentStreetCB: CheckBox? = null
    override fun convert(holder: VBViewHolder<ItemParkingChooseStreetBinding>, item: Street) {
        holder.vb.tvStreet.text = item.streetName
        if (currentStreet == item) {
            holder.vb.cbStreet.isChecked = true
            currentStreetCB = holder.vb.cbStreet
        }else{
            holder.vb.cbStreet.isChecked = false
        }
        holder.vb.rlStreet.setOnClickListener {
            lastStreetCB = currentStreetCB
            lastStreetCB?.isChecked = false
            currentStreetCB = holder.vb.cbStreet
            currentStreetCB?.isChecked = true
            currentStreet = item
            callback.chooseStreet(currentStreet)
        }
        holder.vb.cbStreet.setOnClickListener {
            lastStreetCB = currentStreetCB
            lastStreetCB?.isChecked = false
            currentStreetCB = holder.vb.cbStreet
            currentStreetCB?.isChecked = true
            currentStreet = item
            callback.chooseStreet(currentStreet)
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemParkingChooseStreetBinding {
        return ItemParkingChooseStreetBinding.inflate(inflater)
    }

    interface ChooseStreetAdapterCallBack {
        fun chooseStreet(street: Street)
    }
}