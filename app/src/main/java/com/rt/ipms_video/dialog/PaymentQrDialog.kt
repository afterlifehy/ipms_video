package com.rt.ipms_video.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.SizeUtils
import com.rt.base.dialog.VBBaseLibDialog
import com.rt.base.ext.i18N
import com.rt.base.help.ActivityCacheManager
import com.rt.common.util.AppUtil
import com.rt.common.util.CodeUtils
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.databinding.DialogPaymentQrBinding
import com.zrq.spanbuilder.TextStyle

class PaymentQrDialog(var qrCode: String,var qrUrl: String = "", var amount: String = "", var plate: String = "") : VBBaseLibDialog<DialogPaymentQrBinding>(
    ActivityCacheManager.instance().getCurrentActivity()!!,
    com.rt.base.R.style.CommonBottomDialogStyle
) {
    val sizes = intArrayOf(30, 19)
    val colors = intArrayOf(com.rt.base.R.color.color_ffe92404, com.rt.base.R.color.color_ffe92404)
    val styles = arrayOf(TextStyle.BOLD, TextStyle.NORMAL)

    init {
        initView()
    }

    private fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val strings = arrayOf(amount, i18N(com.rt.base.R.string.元))
        binding.tvAmount.text = AppUtil.getSpan(strings, sizes, colors, styles)
        binding.tvPlate.text = plate
        if(qrCode.isEmpty()){
            val qrBitmap = CodeUtils.createImage(qrUrl, SizeUtils.dp2px(184f), SizeUtils.dp2px(184f), null)
            GlideUtils.instance?.loadImage(binding.rivQr, qrBitmap)
        }else{
            val qrBitmap = AppUtil.base64ToBitmap(qrCode)
            GlideUtils.instance?.loadImage(binding.rivQr,qrBitmap)
        }
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
        return Gravity.CENTER
    }
}