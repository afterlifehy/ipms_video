package com.peakinfo.plateid.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.bean.Street
import com.peakinfo.base.dialog.VBBaseLibDialog
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.plateid.adapter.ParkingChooseStreetAdapter
import com.peakinfo.plateid.databinding.DialogAbnormalStreetListBinding

class AbnormalStreetListDialog(val streetList: MutableList<Street>, var currentStreet: Street, val callBack: AbnormalStreetCallBack) :
    VBBaseLibDialog<DialogAbnormalStreetListBinding>(ActivityCacheManager.instance().getCurrentActivity()!!) {
    var parkingChooseStreetAdapter: ParkingChooseStreetAdapter? = null

    init {
        initView()
    }

    private fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(BaseApplication.instance())
        parkingChooseStreetAdapter =
            ParkingChooseStreetAdapter(streetList, currentStreet, object : ParkingChooseStreetAdapter.ChooseStreetAdapterCallBack {
                override fun chooseStreet(currentStreet: Street) {
                    callBack.chooseStreet(currentStreet)
                    dismiss()
                }

            })
        binding.rvStreet.adapter = parkingChooseStreetAdapter
    }

    override fun getVbBindingView(): ViewBinding {
        return DialogAbnormalStreetListBinding.inflate(layoutInflater)
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getWidth(): Float {
        return ScreenUtils.getScreenWidth() * 0.83f
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return true
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    interface AbnormalStreetCallBack {
        fun chooseStreet(currentStreet: Street)
    }
}