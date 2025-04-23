package com.rt.ipms_geo.ca.ui.activity

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
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.TimeUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.PayResultBean
import com.rt.base.bean.PrintInfoBean
import com.rt.base.bean.ca.FeeInfoBean
import com.rt.base.bean.ca.QueryPayBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.gone
import com.rt.base.ext.hide
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.event.ParkingSpaceBackEvent
import com.rt.common.event.RefreshParkingLotEvent
import com.rt.common.util.AppUtil
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.ipms_geo.R
import com.rt.ipms_geo.databinding.ActivityParkingSpaceBinding
import com.rt.ipms_geo.dialog.PaymentQrDialog
import com.rt.ipms_geo.mvvm.viewmodel.ParkingSpaceViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zrq.spanbuilder.TextStyle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.CA_PARKING_SPACE)
class CAParkingSpaceActivity : VbBaseActivity<ParkingSpaceViewModel, ActivityParkingSpaceBinding>(), OnClickListener {
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

    var amountPending = 0L

    var count = 0
    var handler = Handler(Looper.getMainLooper())

    private lateinit var feeInfo: FeeInfoBean
    private var oweCount = 0
    private var oweMoney = 0L
    lateinit var queryPayBean: QueryPayBean
    lateinit var payResultBean: PayResultBean
    lateinit var ticketQrCode: String
    var from = 0

    val runnable = object : Runnable {
        override fun run() {
            if (count < 60) {
                querypay()
                count++
                handler.postDelayed(this, 3000)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(parkingSpaceBackEvent: ParkingSpaceBackEvent) {
        onBackPressedSupport()
    }

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
        binding.rflPrepaid.setOnClickListener(this)
        binding.rflPrintNotice.setOnClickListener(this)
    }

    override fun initData() {
        showProgressDialog(20000)
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            fee()
            owemoney()
        }
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
                if (oweCount != 0) {
                    ARouter.getInstance().build(ARouterMap.CA_DEBT_COLLECTION).withString(ARouterMap.DEBT_CAR_LICENSE, carLicense)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }

            R.id.rfl_prepaid -> {
                if (this@CAParkingSpaceActivity::feeInfo.isInitialized) {
                    if (feeInfo.prepayMoney!! > 0) {
                        ToastUtil.showMiddleToast("已付金额大于0")
                    } else if (System.currentTimeMillis() - feeInfo.arrivedTime!! > 1000 * 60 * 60
                    ) {
                        ToastUtil.showMiddleToast("在停时间超过1小时")
                    } else {
                        ARouter.getInstance().build(ARouterMap.CA_PREPAID).withString(ARouterMap.PREPAID_CARLICENSE, carLicense)
                            .withString(ARouterMap.PREPAID_CARCOLOR, carColor)
                            .withString(ARouterMap.PREPAID_PARKING_NO, parkingNo)
                            .withString(ARouterMap.PREPAID_ORDER_NO, orderNo).navigation()
                    }
                }
            }

            R.id.rfl_printNotice -> {
                var rxPermissions = RxPermissions(this@CAParkingSpaceActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            noticePrintRequest()
                        }
                    }
                } else {
                    noticePrintRequest()
                }
            }

            R.id.rfl_onSitePayment -> {
                if (carLicense == "默00000") {
                    ToastUtil.showBottomToast(i18N(com.rt.base.R.string.请修改车牌))
                } else {
                    if (this@CAParkingSpaceActivity::feeInfo.isInitialized) {
                        showProgressDialog(20000)
                        val param = HashMap<String, Any>()
                        param["token"] = token
                        param["orderId"] = feeInfo.orderId.toString()
                        param["plateId"] = carLicense
                        param["plateColor"] = carColor
                        mViewModel.payonspot(param)
                    }
                }
            }

            R.id.rfl_abnormalReport -> {
                if (this@CAParkingSpaceActivity::feeInfo.isInitialized) {
                    ARouter.getInstance().build(ARouterMap.BERTH_ABNORMAL).withString(ARouterMap.ABNORMAL_STREET_NO, feeInfo.roadId)
                        .withString(ARouterMap.ABNORMAL_PARKING_NO, parkingNo)
                        .withString(ARouterMap.ABNORMAL_ORDER_NO, orderNo)
                        .withString(ARouterMap.ABNORMAL_CARLICENSE, carLicense)
                        .withString(ARouterMap.ABNORMAL_CAR_COLOR, carColor)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            feeLiveData.observe(this@CAParkingSpaceActivity) {
                dismissProgressDialog()
                feeInfo = it
                binding.rflPlate.show()
                binding.rllParking.show()
                binding.llOperation.show()
                binding.tvNoData.gone()
                binding.tvPlate.text = it.plateId
                if (it.monthPay!! > 0L) {
                    binding.rtvMonthlyPayment.show()
                } else {
                    binding.rtvMonthlyPayment.hide()
                }

                val strings =
                    arrayOf(i18N(com.rt.base.R.string.开始时间), TimeUtils.millis2String(it.arrivedTime!!, "yyyy-MM-dd HH:mm:ss"))
                binding.tvStartTime.text = AppUtil.getSpan(strings, sizes, colors)

                val strings2 = arrayOf(i18N(com.rt.base.R.string.在停时间), AppUtil.millisToDate(it.parkingTime!! * 1000L))
                binding.tvParkingTime.text = AppUtil.getSpan(strings2, sizes, colors)

                val strings3 = arrayOf(i18N(com.rt.base.R.string.已付金额), "${AppUtil.keepNDecimal(it.prepayMoney!! / 100.00, 2)}元")
                binding.tvPaidAmount.text = AppUtil.getSpan(strings3, sizes, colors)

                if (it.monthPay!! > 0L) {
                    val strings4 =
                        arrayOf(i18N(com.rt.base.R.string.月结金额), "${AppUtil.keepNDecimal(it.monthPay!! / 100.00, 2)}元")
                    binding.tvMonthPay.text = AppUtil.getSpan(strings4, sizes, colors)
                } else {
                    binding.tvMonthPay.gone()
                }

                val strings5 = arrayOf(i18N(com.rt.base.R.string.待缴费用), "${AppUtil.keepNDecimal(it.payMoney!! / 100.00, 2)}元")
                binding.tvPendingFee.text = AppUtil.getSpan(strings5, sizes, colors2, styles)

                val strings6 = arrayOf(i18N(com.rt.base.R.string.订单总额), "${AppUtil.keepNDecimal(it.dueMoney!! / 100.00, 2)}元")
                binding.tvOrderAmount.text = AppUtil.getSpan(strings6, sizes, colors)

                amountPending = it.payMoney!!
            }
            owemoneyLiveData.observe(this@CAParkingSpaceActivity) {
                oweCount = it.size
                oweMoney = it.sumOf { it.oweMoney ?: 0 } ?: 0
                binding.tvArrearsNum.text = "${oweCount}笔"
                binding.tvArrearsAmount.text = "${oweMoney}.00元"
            }
            payonspotLiveData.observe(this@CAParkingSpaceActivity) {
                dismissProgressDialog()
                if (it != null) {
                    paymentQrDialog =
                        PaymentQrDialog(it.qrCode!!, it.payUrl!!, AppUtil.keepNDecimal(amountPending / 100.00, 2), carLicense)
                    paymentQrDialog?.show()
                    paymentQrDialog?.setOnDismissListener(object : DialogInterface.OnDismissListener {
                        override fun onDismiss(p0: DialogInterface?) {
                            handler.removeCallbacks(runnable)
                        }
                    })
                    qrNotice()
                    count = 0
                    handler.post(runnable)
                } else {
                    ToastUtil.showBottomToast("没有二维码信息！", 1)
                    return@observe
                }
            }
            querypayLiveData.observe(this@CAParkingSpaceActivity) {
                dismissProgressDialog()
                handler.removeCallbacks(runnable)
                ToastUtil.showBottomToast(i18N(com.rt.base.R.string.支付成功))
                fee()
                queryPayBean = it
                from = 0
                invoiceQrcode(queryPayBean.orderId)
                EventBus.getDefault().post(RefreshParkingLotEvent())
            }
            invoiceQrcodeLiveData.observe(this@CAParkingSpaceActivity) {
                ticketQrCode = it.qrcode.toString()
                if (from == 0) {
                    payResultNotice(queryPayBean)
                } else {
                    startPrint(payResultBean, ticketQrCode) {}
                }
            }
            payResultNoticeLiveData.observe(this@CAParkingSpaceActivity) {
                if (paymentQrDialog != null) {
                    paymentQrDialog?.dismiss()
                }
                val payResultBean = it
                var rxPermissions = RxPermissions(this@CAParkingSpaceActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            startPrint(payResultBean, ticketQrCode) {

                            }
                        }
                    }
                } else {
                    startPrint(it, ticketQrCode) {}
                }
            }
            queryNoticeByOrderNoLiveData.observe(this@CAParkingSpaceActivity) {
                dismissProgressDialog()
                if (it.result != null && it.result.size > 0) {
//                    performPrintTasks(it.result) {
//                    }
                    from = 1
                    payResultBean = it.result[0]
                    invoiceQrcode(payResultBean.tradeNo)
                }
            }
            errMsg.observe(this@CAParkingSpaceActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CAParkingSpaceActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun fee() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["businessId"] = orderNo
        param["plateId"] = carLicense
        param["plateColor"] = carColor
        mViewModel.fee(param)
    }

    fun owemoney() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["district"] = 1
        param["plateId"] = carLicense
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.owemoney(param)
    }

    fun querypay() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["orderId"] = feeInfo.orderId.toString()
        mViewModel.querypay(param)
    }

    fun qrNotice() {
        runBlocking {
            val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["loginName"] = loginName
            jsonobject["businessId"] = feeInfo.businessId
            jsonobject["plateId"] = carLicense
            jsonobject["orderId"] = feeInfo.orderId
            jsonobject["payMoney"] = feeInfo.payMoney.toString()
            jsonobject["orderType"] = "2"
            param["attr"] = jsonobject
            mViewModel.qrNotice(param)
        }
    }

    fun invoiceQrcode(orderId: String) {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["orderId"] = orderId
        param["plateId"] = carLicense
        param["plateColor"] = carColor
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.invoiceQrcode(param)
    }

    fun payResultNotice(queryPayBean: QueryPayBean) {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["payType"] = queryPayBean.payType
        jsonobject["payStatus"] = queryPayBean.payStatus
        jsonobject["payTime"] = queryPayBean.payTime
        jsonobject["tradeNo"] = queryPayBean.orderId
        jsonobject["payMoney"] = queryPayBean.payMoney
        param["attr"] = jsonobject
        mViewModel.payResultNotice(param)
    }

    fun noticePrintRequest() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        param["attr"] = jsonobject
        mViewModel.queryNoticeByOrderNo(param)
    }

    fun performPrintTasks(printDataList: List<PayResultBean>, onComplete: () -> Unit) {
        val iterator = printDataList.iterator()

        fun printNext() {
            if (iterator.hasNext()) {
                val printData = iterator.next()
                startPrint(printData, "") {
                    // 打印完成后继续下一个打印
                    printNext()
                }
            } else {
                // 所有打印任务完成时调用 onComplete 回调
                Handler(Looper.getMainLooper()).postDelayed({ onComplete() }, 1000)
            }
        }
        // 开始第一个打印任务
        printNext()
    }

    fun startPrint(it: PayResultBean, ticketQrCode: String, onComplete: () -> Unit) {
        val payMoney = it.payMoney
        val printInfo = PrintInfoBean(
            roadId = it.roadName,
            plateId = it.carLicense,
            payMoney = String.format("%.2f", payMoney.toFloat()),
            orderId = it.tradeNo,
            phone = it.phone,
            startTime = it.startTime,
            leftTime = it.endTime,
            remark = it.remark,
            company = it.businessCname,
            oweCount = 0,
            ticketQrCode = ticketQrCode
        )
        val printList = BluePrint.instance?.blueToothDevice!!
        if (printList.size == 1) {
            Thread {
                val device = printList[0]
                var connectResult = BluePrint.instance?.connet(device.address)
                if (connectResult == 0) {
                    BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
                }
            }.start()
        }
        GlobalScope.launch {
            delay(3000)
            // 执行打印完成后的回调
            onComplete()
        }
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

    override fun onStop() {
        super.onStop()
        if (handler != null) {
            handler.removeCallbacks(runnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) {
            handler.removeCallbacks(runnable)
        }
    }
}