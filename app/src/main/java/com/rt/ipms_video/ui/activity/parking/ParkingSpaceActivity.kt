package com.rt.ipms_video.ui.activity.parking

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.AppUtil
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityParkingSpaceBinding
import com.rt.ipms_video.dialog.PaymentQrDialog
import com.rt.ipms_video.mvvm.viewmodel.ParkingSpaceViewModel
import com.zrq.spanbuilder.TextStyle
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.PARKING_SPACE)
class ParkingSpaceActivity : VbBaseActivity<ParkingSpaceViewModel, ActivityParkingSpaceBinding>(), OnClickListener {
    val sizes = intArrayOf(19, 19)
    val colors = intArrayOf(com.rt.base.R.color.color_ff666666, com.rt.base.R.color.color_ff1a1a1a)
    val colors2 = intArrayOf(com.rt.base.R.color.color_ff666666, com.rt.base.R.color.color_fff70f0f)
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD)

    var paymentQrDialog: PaymentQrDialog? = null

    var orderNo = ""
    var carLicense = ""
    var carColor = ""
    var parkingNo = ""
    var token = ""

    var qr = ""

    override fun initView() {
        orderNo = intent.getStringExtra(ARouterMap.ORDER_NO).toString()
        carLicense = intent.getStringExtra(ARouterMap.CAR_LICENSE).toString()
        carColor = intent.getStringExtra(ARouterMap.CAR_COLOR).toString()
        parkingNo = intent.getStringExtra(ARouterMap.PARKING_NO).toString()

        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = parkingNo
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_video)
        binding.layoutToolbar.ivRight.show()

        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rrlArrears.setOnClickListener(this)
        binding.rflOnSitePayment.setOnClickListener(this)
        binding.rflAbnormalReport.setOnClickListener(this)
    }

    override fun initData() {
        showProgressDialog()
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["token"] = token
        jsonobject["orderNo"] = orderNo
        jsonobject["carLicense"] = carLicense
        jsonobject["carColor"] = carColor
        param["attr"] = jsonobject
        mViewModel.parkingSpaceFee(param)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.VIDEO_PIC_ORDER_NO).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rrl_arrears -> {
                ARouter.getInstance().build(ARouterMap.DEBT_COLLECTION).withString(ARouterMap.DEBT_CAR_LICENSE, carLicense)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_onSitePayment -> {
                paymentQrDialog = PaymentQrDialog(qr)
                paymentQrDialog?.show()
            }

            R.id.rfl_abnormalReport -> {
                ARouter.getInstance().build(ARouterMap.BERTH_ABNORMAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingSpaceFeeLiveData.observe(this@ParkingSpaceActivity) {
                binding.tvPlate.text = it.carLicense

                val strings = arrayOf(i18N(com.rt.base.R.string.开始时间), it.startTime)
                binding.tvStartTime.text = AppUtil.getSpan(strings, sizes, colors2)

                val strings2 = arrayOf(i18N(com.rt.base.R.string.在停时间), "${it.parkingTime / 60}分钟")
                binding.tvParkingTime.text = AppUtil.getSpan(strings2, sizes, colors)

                val strings3 = arrayOf(i18N(com.rt.base.R.string.已付金额), "${it.amountPayed / 100.00}元")
                binding.tvPaidAmount.text = AppUtil.getSpan(strings3, sizes, colors)

                val strings4 = arrayOf(i18N(com.rt.base.R.string.待缴费用), "${it.amountPending / 100.00}元")
                binding.tvPendingFee.text = AppUtil.getSpan(strings4, sizes, colors2, styles)

                val strings5 = arrayOf(i18N(com.rt.base.R.string.订单总额), "${it.amountTotal / 100.00}元")
                binding.tvOrderAmount.text = AppUtil.getSpan(strings5, sizes, colors)

//                TODO("少两个字段")
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["token"] = token
                jsonobject["tradeNo"] = it.tradeNo
                jsonobject["carLicense"] = it.carLicense
                jsonobject["carColor"] = it.carColor
                param["attr"] = jsonobject
                mViewModel.insidePay(param)
            }
            insidePayLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                qr = it.qrCode
            }
            errMsg.observe(this@ParkingSpaceActivity) {
                ToastUtil.showToast(it.msg)
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