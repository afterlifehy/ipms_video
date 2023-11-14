package com.rt.ipms_video.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import com.rt.base.BaseApplication
import com.rt.base.dialog.VBBaseLibDialog
import com.rt.base.help.ActivityCacheManager
import com.rt.ipms_video.adapter.ParkingChooseStreetAdapter
import com.rt.ipms_video.databinding.DialogAbnormalStreetListBinding

class AbnormalStreetListDialog(val streetList: MutableList<Int>, val callBack: AbnormalStreetCallBack) :
    VBBaseLibDialog<DialogAbnormalStreetListBinding>(ActivityCacheManager.instance().getCurrentActivity()!!) {
    var parkingChooseStreetAdapter: ParkingChooseStreetAdapter? = null
    var currentStreet = 1

    init {
        initView()
    }

    private fun initView() {
        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(BaseApplication.instance())
        parkingChooseStreetAdapter =
            ParkingChooseStreetAdapter(streetList, currentStreet, object : ParkingChooseStreetAdapter.ChooseStreetAdapterCallBack {
                override fun chooseStreet(currentStreet: Int) {
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
        fun chooseStreet(currentStreet: Int)
    }
}