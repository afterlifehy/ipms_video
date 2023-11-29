package ja.insepector.bxapp.dialog

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import ja.insepector.base.dialog.VBBaseLibDialog
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.DialogSelectPicBinding

class SelectPicDialog(val callback: Callback) : VBBaseLibDialog<DialogSelectPicBinding>(
    ActivityCacheManager.instance().getCurrentActivity()!!,
    ja.insepector.base.R.style.CommonBottomDialogStyle
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