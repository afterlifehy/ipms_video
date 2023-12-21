package com.peakinfo.plateid.ui.activity.parking

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
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.ParkingSpaceBean
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.PrintInfoBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.event.RefreshParkingLotEvent
import com.peakinfo.common.event.RefreshParkingSpaceEvent
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.BigDecimalManager
import com.peakinfo.common.util.BluePrint
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityParkingSpaceBinding
import com.peakinfo.plateid.dialog.PaymentQrDialog
import com.peakinfo.plateid.mvvm.viewmodel.ParkingSpaceViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zrq.spanbuilder.TextStyle
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.PARKING_SPACE)
class ParkingSpaceActivity : VbBaseActivity<ParkingSpaceViewModel, ActivityParkingSpaceBinding>(), OnClickListener {
    val sizes = intArrayOf(19, 19)
    val colors = intArrayOf(com.peakinfo.base.R.color.color_ff666666, com.peakinfo.base.R.color.color_ff1a1a1a)
    val colors2 = intArrayOf(com.peakinfo.base.R.color.color_ff666666, com.peakinfo.base.R.color.color_fff70f0f)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshParkingSpaceEvent: RefreshParkingSpaceEvent) {
        carLicense = refreshParkingSpaceEvent.carLicense
        carColor = refreshParkingSpaceEvent.carColor.toString()
        requestParkingSpaceFee()
    }

    override fun initView() {
        orderNo = intent.getStringExtra(ARouterMap.ORDER_NO).toString()
        carLicense = intent.getStringExtra(ARouterMap.CAR_LICENSE).toString()
        carColor = intent.getStringExtra(ARouterMap.CAR_COLOR).toString()
        parkingNo = intent.getStringExtra(ARouterMap.PARKING_NO).toString()

        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = parkingNo
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.peakinfo.common.R.mipmap.ic_video)
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
            requestParkingSpaceFee()
        }
    }

    fun requestParkingSpaceFee() {
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
                ARouter.getInstance().build(ARouterMap.VIDEO_PIC).withString(ARouterMap.VIDEO_PIC_ORDER_NO, orderNo)
                    .withInt(ARouterMap.VIDEO_PIC_FROM, 1)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rrl_arrears -> {
                if (parkingSpaceBean?.historyCount != 0) {
                    ARouter.getInstance().build(ARouterMap.DEBT_COLLECTION).withString(ARouterMap.DEBT_CAR_LICENSE, carLicense)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }

            R.id.rfl_onSitePayment -> {
                if (carLicense == "默00000") {
                    ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.请修改车牌))
                } else {
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
            }

            R.id.rfl_abnormalReport -> {
                ARouter.getInstance().build(ARouterMap.BERTH_ABNORMAL).withString(ARouterMap.ABNORMAL_STREET_NO, parkingSpaceBean?.streetNo)
                    .withString(ARouterMap.ABNORMAL_PARKING_NO, parkingSpaceBean?.parkingNo)
                    .withString(ARouterMap.ABNORMAL_ORDER_NO, orderNo)
                    .withString(ARouterMap.ABNORMAL_CARLICENSE, parkingSpaceBean?.carLicense)
                    .withString(ARouterMap.ABNORMAL_CAR_COLOR, parkingSpaceBean?.carColor.toString())
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
                binding.rflPlate.show()
                binding.rllParking.show()
                binding.llOperation.show()
                binding.tvNoData.gone()

                parkingSpaceBean = it
                binding.tvPlate.text = it.carLicense

                val strings = arrayOf(i18N(com.peakinfo.base.R.string.开始时间), it.startTime)
                binding.tvStartTime.text = AppUtil.getSpan(strings, sizes, colors)

                val strings2 = arrayOf(i18N(com.peakinfo.base.R.string.在停时间), AppUtil.dayHourMin(it.parkingTime))
                binding.tvParkingTime.text = AppUtil.getSpan(strings2, sizes, colors)

                val strings3 = arrayOf(i18N(com.peakinfo.base.R.string.已付金额), "${AppUtil.keepNDecimal(it.amountPayed / 100.00, 2)}元")
                binding.tvPaidAmount.text = AppUtil.getSpan(strings3, sizes, colors)

                val strings4 = arrayOf(i18N(com.peakinfo.base.R.string.待缴费用), "${AppUtil.keepNDecimal(it.amountPending / 100.00, 2)}元")
                binding.tvPendingFee.text = AppUtil.getSpan(strings4, sizes, colors2, styles)

                val strings5 = arrayOf(i18N(com.peakinfo.base.R.string.订单总额), "${AppUtil.keepNDecimal(it.amountTotal / 100.00, 2)}元")
                binding.tvOrderAmount.text = AppUtil.getSpan(strings5, sizes, colors)

                binding.tvArrearsNum.text = "${it.historyCount}笔"

                binding.tvArrearsAmount.text =
                    "${AppUtil.keepNDecimals(BigDecimalManager.divisionDoubleToString(it.historySum, 100.00), 2)}元"

                tradeNo = it.tradeNo
                amountPending = it.amountPending
            }
            insidePayLiveData.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
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
                ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.支付成功))
                requestParkingSpaceFee()
                if (paymentQrDialog != null) {
                    paymentQrDialog?.dismiss()
                }
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
                EventBus.getDefault().post(RefreshParkingLotEvent())
                onBackPressedSupport()
            }
            errMsg.observe(this@ParkingSpaceActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
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
            oweCount = it.oweCount
        )
        ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.开始打印))
        Thread {
            BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
        }.start()
    }

    override fun isRegEventBus(): Boolean {
        return true
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