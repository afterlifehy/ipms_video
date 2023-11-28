package com.rt.ipms_video.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.bean.BlueToothDeviceBean
import com.rt.ipms_video.databinding.ItemBluetoothDeviceBinding
import kotlinx.coroutines.runBlocking

class BlueToothDeviceAdapter(data: MutableList<BluetoothDevice>? = null, var currentDevice: BlueToothDeviceBean?) :
    BaseBindingAdapter<BluetoothDevice, ItemBluetoothDeviceBinding>(data) {
    var checkedDevice: BluetoothDevice? = null
    var lastDevice: BluetoothDevice? = null

    @SuppressLint("MissingPermission")
    override fun convert(holder: VBViewHolder<ItemBluetoothDeviceBinding>, item: BluetoothDevice) {
        holder.vb.tvDevice.text = item.name
        if (currentDevice != null && checkedDevice == null && item.address == currentDevice!!.address) {

            checkedDevice = item
        }
        if (item == checkedDevice) {
            holder.vb.cbDevice.isChecked = true
        } else {
            holder.vb.cbDevice.isChecked = false
        }
        holder.vb.rlDevice.setOnClickListener {
            holder.vb.cbDevice.isChecked = true
            if (holder.vb.cbDevice.isChecked) {
                lastDevice = checkedDevice
                checkedDevice = item
                notifyItemChanged(data.indexOf(lastDevice))
                notifyItemChanged(data.indexOf(checkedDevice))
            }
        }
        holder.vb.cbDevice.setOnClickListener {
            if (holder.vb.cbDevice.isChecked) {
                lastDevice = checkedDevice
                checkedDevice = item
                notifyItemChanged(data.indexOf(lastDevice))
                notifyItemChanged(data.indexOf(checkedDevice))
            } else {
                lastDevice = checkedDevice
                checkedDevice = null
            }
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemBluetoothDeviceBinding {
        return ItemBluetoothDeviceBinding.inflate(inflater)
    }

//    fun getCheckedList(): MutableList<Int> {
//        return checkedList
//    }
}