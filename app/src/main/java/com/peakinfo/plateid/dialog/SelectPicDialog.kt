package com.peakinfo.plateid.dialog

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import com.peakinfo.base.dialog.VBBaseLibDialog
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.DialogSelectPicBinding

class SelectPicDialog(val callback: Callback) : VBBaseLibDialog<DialogSelectPicBinding>(
    ActivityCacheManager.instance().getCurrentActivity()!!,
    com.peakinfo.base.R.style.CommonBottomDialogStyle
), View.OnClickListener {

    init {
        initView()
    }

    private fun initView() {
        binding.rtvSelectFromAlbum.setOnClickListener(this)
        binding.rtvCancel.setOnClickListener(this)
        binding.tvTakePhoto.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_selectFromAlbum -> callback.onPickPhoto()
            R.id.tv_takePhoto -> callback.onTakePhoto()
            R.id.rtv_cancel -> {
            }
        }
        dismiss()
    }

    override fun getHideInput(): Boolean {
        return false
    }

    override fun getWidth(): Float {
        return ScreenUtils.getScreenWidth() * 0.9f
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.WRAP_CONTENT.toFloat()
    }

    override fun getVbBindingView(): ViewBinding? {
        return DialogSelectPicBinding.inflate(layoutInflater)
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return false
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    interface Callback {

        fun onTakePhoto()

        fun onPickPhoto()

    }
}