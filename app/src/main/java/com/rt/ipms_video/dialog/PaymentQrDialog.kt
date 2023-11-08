package com.rt.ipms_video.dialog

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.SizeUtils
import com.rt.base.dialog.VBBaseLibDialog
import com.rt.base.help.ActivityCacheManager
import com.rt.common.util.CodeUtils
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.databinding.DialogPaymentQrBinding

class PaymentQrDialog() : VBBaseLibDialog<DialogPaymentQrBinding>(
    ActivityCacheManager.instance().getCurrentActivity()!!,
    com.rt.base.R.style.CommonBottomDialogStyle
) {

    init {
        initView()
    }

    private fun initView() {
        var qrCode = "https://www.baidu.com/"
        val qrBitmap = CodeUtils.createImage(qrCode, SizeUtils.dp2px(153f), SizeUtils.dp2px(153f), null)
        GlideUtils.instance?.loadImage(binding.rivQr, qrBitmap)

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun getVbBindingView(): ViewBinding? {
        return DialogPaymentQrBinding.inflate(layoutInflater)
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
        return false
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }
}