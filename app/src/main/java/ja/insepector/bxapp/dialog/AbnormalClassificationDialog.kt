package ja.insepector.bxapp.dialog

import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.dialog.VBBaseLibDialog
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.bxapp.adapter.AbnormalClassificationAdapter
import ja.insepector.bxapp.databinding.DialogAbnormalClassificationBinding

class AbnormalClassificationDialog(
    val classificationList: MutableList<String>,
    var currentStreet: String,
    val callBack: AbnormalClassificationCallBack
) :
    VBBaseLibDialog<DialogAbnormalClassificationBinding>(ActivityCacheManager.instance().getCurrentActivity()!!) {
    var abnormalClassificationAdapter: ja.insepector.bxapp.adapter.AbnormalClassificationAdapter? = null

    init {
        initView()
    }

    private fun initView() {
        binding.rvClassification.setHasFixedSize(true)
        binding.rvClassification.layoutManager = LinearLayoutManager(BaseApplication.instance())
        abnormalClassificationAdapter =
            AbnormalClassificationAdapter(
                classificationList,
                currentStreet,
                object : AbnormalClassificationAdapter.ChooseClassificationAdapterCallBack {
                    override fun chooseClassification(classification: String) {
                        callBack.chooseClassification(classification)
                        dismiss()
                    }
                })
        binding.rvClassification.adapter = abnormalClassificationAdapter
    }

    override fun getVbBindingView(): ViewBinding {
        return DialogAbnormalClassificationBinding.inflate(layoutInflater)
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

    interface AbnormalClassificationCallBack {
        fun chooseClassification(classification: String)
    }
}