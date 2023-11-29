package ja.insepector.bxapp.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.SizeUtils
import ja.insepector.base.dialog.VBBaseLibDialog
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.databinding.DialogPaymentQrBinding

class PaymentQrDialog(var qr: String) : VBBaseLibDialog<DialogPaymentQrBinding>(
    ActivityCacheManager.instance().getCurrentActivity()!!,
    ja.insepector.base.R.style.CommonBottomDialogStyle
) {

    init {
        initView()
    }

    private fun initView() {
        val qrBitmap = ja.insepector.common.util.CodeUtils.createImage(qr, SizeUtils.dp2px(153f), SizeUtils.dp2px(153f), null)
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