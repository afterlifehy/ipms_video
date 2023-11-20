package com.rt.ipms_video.ui.activity.order

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
import com.rt.ipms_video.databinding.ActivityDebtOrderDetailBinding
import com.rt.ipms_video.dialog.PaymentQrDialog
import com.rt.ipms_video.mvvm.viewmodel.DebtOrderDetailViewModel

@Route(path = ARouterMap.DEBT_ORDER_DETAIL)
class DebtOrderDetailActivity : VbBaseActivity<DebtOrderDetailViewModel, ActivityDebtOrderDetailBinding>(), OnClickListener {
    val colors = intArrayOf(com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4)
    val sizes = intArrayOf(16, 24, 16)
    val colors2 = intArrayOf(com.rt.base.R.color.color_ff666666, com.rt.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)
    var paymentQrDialog: PaymentQrDialog? = null
    var qr = ""

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.欠费订单详情)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_video)
        binding.layoutToolbar.ivRight.show()
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rflPay.setOnClickListener(this)
    }

    override fun initData() {
        val strings1 = arrayOf(i18N(com.rt.base.R.string.订单) + ":", "202308080CN007008000348")
        binding.tvOrderNo.text = AppUtil.getSpan(strings1, sizes2, colors2)
        binding.tvBerth.text = AppUtil.getSpan(strings1, sizes2, colors2)
        binding.tvStreet.text = AppUtil.getSpan(strings1, sizes2, colors2)
        binding.tvStartTime.text = AppUtil.getSpan(strings1, sizes2, colors2)
        binding.tvEndTime.text = AppUtil.getSpan(strings1, sizes2, colors2)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.VIDEO_PIC).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_pay -> {
                paymentQrDialog = PaymentQrDialog(qr)
                paymentQrDialog?.show()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityDebtOrderDetailBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<DebtOrderDetailViewModel> {
        return DebtOrderDetailViewModel::class.java
    }
}