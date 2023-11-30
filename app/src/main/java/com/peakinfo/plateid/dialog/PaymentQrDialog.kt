package com.peakinfo.plateid.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.SizeUtils
import com.peakinfo.base.dialog.VBBaseLibDialog
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.common.util.CodeUtils
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.databinding.DialogPaymentQrBinding

class PaymentQrDialog(var qr: String) : VBBaseLibDialog<DialogPaymentQrBinding>(
    ActivityCacheManager.instance().getCurrentActivity()!!,
    com.peakinfo.base.R.style.CommonBottomDialogStyle
) {

    init {
        initView()
    }

    private fun initView() {
        val qrBitmap = CodeUtils.createImage(qr, SizeUtils.dp2px(153f), SizeUtils.dp2px(153f), null)
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