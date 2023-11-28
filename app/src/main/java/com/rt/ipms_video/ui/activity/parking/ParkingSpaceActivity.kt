package com.rt.ipms_video.ui.activity.parking

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.ParkingSpaceBean
import com.rt.base.bean.PayResultBean
import com.rt.base.bean.PrintInfoBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.AppUtil
import com.rt.common.util.BigDecimalManager
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityParkingSpaceBinding
import com.rt.ipms_video.dialog.PaymentQrDialog
import com.rt.ipms_video.mvvm.viewmodel.ParkingSpaceViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zrq.spanbuilder.TextStyle
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.PARKING_SPACE)
class ParkingSpaceActivity : VbBaseActivity<ParkingSpaceViewModel, ActivityParkingSpaceBinding>(), OnClickListener {
    var job: Job? = null
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
    var parkingSpaceBean: ParkingSpaceBean? = null

    var qr = ""
    var tradeNo = ""
    var amountPending = 0

    var count = 0
    var handler = Handler(Looper.getMainLooper())

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

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rrlArrears.setOnClickListener(this)
        binding.rflOnSitePayment.setOnClickListener(this)
        binding.rflAbnormalReport.setOnClickListener(this)
    }

    override fun initData() {
        showProgressDialog(20000)
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["token"] = token
            jsonobject["orderNo"] = orderNo
            jsonobject["carLicense"] = carLicense
            jsonobject["carColor"] = carColor
            param["attr"] = jsonobject
            mViewModel.parkingSpaceFee(param)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.VIDEO_PIC).withString(ARouterMap.VIDEO_PIC_ORDER_NO, orderNo)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rrl_arrears -> {
                ARouter.getInstance().build(ARouterMap.DEBT_COLLECTION).withString(ARouterMap.DEBT_CAR_LICENSE, carLicense)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_onSitePayment -> {
                showProgressDialog(20000)
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["token"] = token
                jsonobject["tradeNo"] = tradeNo
                jsonobject["carLicense"] = carLicense
                jsonobject["carColor"] = carColor
                jsonobject["amountPending"] = amountPending
                jsonobject["orderNo"] = orderNo
                param["attr"] = jsonobject
                mViewModel.insidePay(param)
            }

            R.id.rfl_abnormalReport -> {
                ARouter.getInstance().build(ARouterMap.BERTH_ABNORMAL).withString(ARouterMap.ABNORMAL_STREET_NO, parkingSpaceBean?.streetNo)
                    .withString(ARouterMap.ABNORMAL_PARKING_NO, parkingSpaceBean?.parkingNo)
                    .withString(ARouterMap.ABNORMAL_ORDER_NO, orderNo)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    fun checkPayResult() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["token"] = token
        jsonobject["tradeNo"] = tradeNo
        param["attr"] = jsonobject
        mViewModel.payResult(param)
    }

    val runnable = object : Runnable {
        override fun run() {
            if (count < 60) {
                checkPayResult()
                count++
                handler.postDelayed(this, 3000)
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingSpaceFeeLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                parkingSpaceBean = it
                binding.tvPlate.text = it.carLicense

                val strings = arrayOf(i18N(com.rt.base.R.string.开始时间), it.startTime)
                binding.tvStartTime.text = AppUtil.getSpan(strings, sizes, colors)

                val strings2 = arrayOf(i18N(com.rt.base.R.string.在停时间), "${it.parkingTime / 60}分钟")
                binding.tvParkingTime.text = AppUtil.getSpan(strings2, sizes, colors)

                val strings3 = arrayOf(i18N(com.rt.base.R.string.已付金额), "${AppUtil.keepNDecimal(it.amountPayed / 100.00, 2)}元")
                binding.tvPaidAmount.text = AppUtil.getSpan(strings3, sizes, colors)

                val strings4 = arrayOf(i18N(com.rt.base.R.string.待缴费用), "${AppUtil.keepNDecimal(it.amountPending / 100.00, 2)}元")
                binding.tvPendingFee.text = AppUtil.getSpan(strings4, sizes, colors2, styles)

                val strings5 = arrayOf(i18N(com.rt.base.R.string.订单总额), "${AppUtil.keepNDecimal(it.amountTotal / 100.00, 2)}元")
                binding.tvOrderAmount.text = AppUtil.getSpan(strings5, sizes, colors)

                binding.tvArrearsNum.text = "${it.historyCount}笔"

                binding.tvArrearsAmount.text =
                    "${AppUtil.keepNDecimal(BigDecimalManager.divisionDoubleToString(it.historySum, 100.00), 2)}元"

                tradeNo = it.tradeNo
                amountPending = it.amountPending
            }
            insidePayLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                tradeNo = it.tradeNo
                qr = it.payUrl
                paymentQrDialog = PaymentQrDialog(qr)
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(p0: DialogInterface?) {
                        handler.removeCallbacks(runnable)
                    }
                })
                count = 0
                handler.post(runnable)
            }
            payResultLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                handler.removeCallbacks(runnable)
                ToastUtil.showToast(i18N(com.rt.base.R.string.支付成功))
                val payResultBean = it
                var rxPermissions = RxPermissions(this@ParkingSpaceActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            startPrint(payResultBean)
                        }
                    }
                } else {
                    startPrint(it)
                }

            }
            errMsg.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
            }
        }
    }

    fun startPrint(it: PayResultBean) {
        val payMoney = it.payMoney
        val printInfo = PrintInfoBean(
            roadId = it.roadName,
            plateId = it.carLicense,
            payMoney = String.format("%.2f", payMoney.toFloat()),
            orderId = orderNo,
            phone = it.phone,
            startTime = it.startTime,
            leftTime = it.endTime,
            remark = it.remark,
            company = it.businessCname,
            oweCount = 0
        )
        Thread {
            BluePrint.instance?.zkblueprint(printInfo.toString())
        }.start()
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