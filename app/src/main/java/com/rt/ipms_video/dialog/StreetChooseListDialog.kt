package com.rt.ipms_video.dialog

import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.rt.base.BaseApplication
import com.rt.base.bean.Street
import com.rt.base.dialog.VBBaseLibDialog
import com.rt.base.ext.gone
import com.rt.base.help.ActivityCacheManager
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.ChooseStreetAdapter
import com.rt.ipms_video.databinding.DialogStreetListBinding

class StreetChooseListDialog(
    val streetList: MutableList<Street>,
    var streetChoosedList: MutableList<Street>,
    val callback: StreetChooseCallBack
) :
    VBBaseLibDialog<DialogStreetListBinding>(
        ActivityCacheManager.instance().getCurrentActivity()!!,
        com.rt.base.R.style.CommonBottomDialogStyle
    ), OnClickListener {

    var chooseStreetAdapter: ChooseStreetAdapter? = null

    init {
        initView()
    }

    private fun initView() {
        binding.tvDialogTitle.gone()
        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(BaseApplication.instance())
        chooseStreetAdapter = ChooseStreetAdapter(streetList, streetChoosedList)
        binding.rvStreet.adapter = chooseStreetAdapter

        binding.rtvOk.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_ok -> {
                callback.chooseStreets()
                dismiss()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return DialogStreetListBinding.inflate(layoutInflater)
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getWidth(): Float {
        return WindowManager.LayoutParams.MATCH_PARENT.toFloat()
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return true
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    interface StreetChooseCallBack {
        fun chooseStreets()
    }
}