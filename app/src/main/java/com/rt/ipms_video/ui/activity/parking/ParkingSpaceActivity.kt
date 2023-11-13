package com.rt.ipms_video.ui.activity.parking

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.AppUtil
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityParkingSpaceBinding
import com.rt.ipms_video.dialog.PaymentQrDialog
import com.rt.ipms_video.mvvm.viewmodel.ParkingSpaceViewModel
import com.zrq.spanbuilder.TextStyle

@Route(path = ARouterMap.PARKING_SPACE)
class ParkingSpaceActivity : VbBaseActivity<ParkingSpaceViewModel, ActivityParkingSpaceBinding>(), OnClickListener {
    val sizes = intArrayOf(19, 19)
    val colors = intArrayOf(com.rt.base.R.color.color_ff666666, com.rt.base.R.color.color_ff1a1a1a)
    val colors2 = intArrayOf(com.rt.base.R.color.color_ff666666, com.rt.base.R.color.color_fff70f0f)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD)

    var paymentQrDialog: PaymentQrDialog? = null
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = "CN00707"
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_video)
        binding.layoutToolbar.ivRight.show()

        val strings = arrayOf(i18N(com.rt.base.R.string.开始时间), "2023-06-25 10:12:24")
        binding.tvStartTime.text = AppUtil.getSpan(strings, sizes, colors)

        val strings2 = arrayOf(i18N(com.rt.base.R.string.待缴费用), "15.00元")
        binding.tvPendingFee.text = AppUtil.getSpan(strings2, sizes, colors2, styles)
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rrlArrears.setOnClickListener(this)
        binding.rflOnSitePayment.setOnClickListener(this)
        binding.rflAbnormalReport.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.VIDEO_PIC).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rrl_arrears -> {
                ARouter.getInstance().build(ARouterMap.DEBT_COLLECTION).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_onSitePayment -> {
                paymentQrDialog = PaymentQrDialog()
                paymentQrDialog?.show()
            }

            R.id.rfl_abnormalReport -> {

            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityParkingSpaceBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<ParkingSpaceViewModel> {
        return ParkingSpaceViewModel::class.java
    }
}