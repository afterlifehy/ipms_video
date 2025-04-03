package com.peakinfo.plateid.ca.ui.activity

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
import com.peakinfo.base.bean.DebtCollectionBean
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.PrintInfoBean
import com.peakinfo.base.bean.ca.OweMoneyBean
import com.peakinfo.base.bean.ca.QueryPayBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.event.RefreshParkingSpaceEvent
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.event.RefreshDebtOrderListEvent
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.BluePrint
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityDebtOrderDetailBinding
import com.peakinfo.plateid.dialog.PaymentQrDialog
import com.peakinfo.plateid.mvvm.viewmodel.DebtOrderDetailViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.CA_DEBT_ORDER_DETAIL)
class CADebtOrderDetailActivity : VbBaseActivity<DebtOrderDetailViewModel, ActivityDebtOrderDetailBinding>(), OnClickListener {
    val colors = intArrayOf(com.peakinfo.base.R.color.color_fff70f0f, com.peakinfo.base.R.color.color_fff70f0f)
    val sizes = intArrayOf(24, 16)
    val colors2 = intArrayOf(com.peakinfo.base.R.color.color_ff666666, com.peakinfo.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)
    var paymentQrDialog: PaymentQrDialog? = null
    var tradeNo = ""
    var debtCollectionBean: DebtCollectionBean? = null
    var token = ""
    var count = 0
    var handler = Handler(Looper.getMainLooper())
    var payMoney = 0

    lateinit var queryPayBean: QueryPayBean
    lateinit var ticketQrCode: String

    val runnable = object : Runnable {
        override fun run() {
            if (count < 60) {
                querypay()
                count++
                handler.postDelayed(this, 3000)
            }
        }
    }

    @SuppressLint("NewApi")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.欠费订单详情)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.peakinfo.common.R.mipmap.ic_video)
        binding.layoutToolbar.ivRight.show()

        debtCollectionBean = intent.getParcelableExtra(ARouterMap.DEBT_ORDER) as? DebtCollectionBean

        binding.tvPlate.text = debtCollectionBean!!.carLicense
        val strings1 = arrayOf("${AppUtil.keepNDecimal(debtCollectionBean!!.oweMoney / 100.00, 2)}", "元")
        binding.tvArrearsAmount.text = AppUtil.getSpan(strings1, sizes, colors)
        val strings2 = arrayOf(i18N(com.peakinfo.base.R.string.订单) + "：", debtCollectionBean!!.orderNo)
        binding.tvOrderNo.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18N(com.peakinfo.base.R.string.泊位) + "：", debtCollectionBean!!.parkingNo)
        binding.tvBerth.text = AppUtil.getSpan(strings3, sizes2, colors2)
        val strings4 = arrayOf(i18N(com.peakinfo.base.R.string.路段) + "：", debtCollectionBean!!.streetName)
        binding.tvStreet.text = AppUtil.getSpan(strings4, sizes2, colors2)
        val strings5 = arrayOf(i18N(com.peakinfo.base.R.string.入场) + "：", debtCollectionBean!!.startTime)
        binding.tvStartTime.text = AppUtil.getSpan(strings5, sizes2, colors2)
        val strings6 = arrayOf(i18N(com.peakinfo.base.R.string.出场) + "：", debtCollectionBean!!.endTime)
        binding.tvEndTime.text = AppUtil.getSpan(strings6, sizes2, colors2)

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rflPay.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.VIDEO_PIC).withString(ARouterMap.VIDEO_PIC_ORDER_NO, debtCollectionBean!!.orderNo)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_pay -> {
                showProgressDialog(20000)
                val param = HashMap<String, Any>()
                param["token"] = token
                param["orderId"] = debtCollectionBean!!.oweOrderId
                param["channel"] = "pos"
                mViewModel.payowemoney(param)
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            payowemoneyLiveData.observe(this@CADebtOrderDetailActivity) {
                payMoney = it.amount
                consumeonline(it)
            }
            consumeonlineLiveData.observe(this@CADebtOrderDetailActivity) {
                qrNotice()
                paymentQrDialog =
                    PaymentQrDialog(
                        it.qrCode.toString(), it.payUrl.toString(), AppUtil.keepNDecimal(debtCollectionBean!!.oweMoney / 100.00, 2),
                        debtCollectionBean!!.carLicense
                    )
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(p0: DialogInterface?) {
                        handler.removeCallbacks(runnable)
                    }
                })
                count = 0
                handler.post(runnable)
            }
            querypayLiveData.observe(this@CADebtOrderDetailActivity) {
                dismissProgressDialog()
                handler.removeCallbacks(runnable)
                ToastUtil.showBottomToast(i18N(com.peakinfo.base.R.string.支付成功))
                payResultNotice(it)
            }
            invoiceQrcodeLiveData.observe(this@CADebtOrderDetailActivity) {
                ticketQrCode = it.qrCode.toString()
                payResultNotice(queryPayBean)
            }
            payResultNoticeLiveData.observe(this@CADebtOrderDetailActivity) {
                if (paymentQrDialog != null) {
                    paymentQrDialog?.dismiss()
                }
                val payResultBean = it
                var rxPermissions = RxPermissions(this@CADebtOrderDetailActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            startPrint(payResultBean)
                        }
                    }
                } else {
                    startPrint(it)
                }
                EventBus.getDefault().post(RefreshDebtOrderListEvent())
                onBackPressedSupport()
            }
            errMsg.observe(this@CADebtOrderDetailActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CADebtOrderDetailActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun consumeonline(oweMoneyBean: OweMoneyBean) {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["businessId"] = debtCollectionBean!!.orderNo
        param["orderId"] = oweMoneyBean.orderId
        param["oweOrderId"] = oweMoneyBean.oweOrderId
        param["payMoney"] = oweMoneyBean.amount
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.consumeonline(param)
    }

    fun querypay() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["orderId"] = tradeNo
        mViewModel.querypay(param)
    }

//    fun invoiceQrcode(orderId: String) {
//        val param = HashMap<String, Any>()
//        param["token"] = token
//        param["orderId"] = orderId
//        param["plateId"] = carLicense
//        param["plateColor"] = carColor
//        param["dataTime"] = System.currentTimeMillis()
//        mViewModel.invoiceQrcode(param)
//    }

    fun qrNotice() {
        runBlocking {
            val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["loginName"] = loginName
            jsonobject["businessId"] = debtCollectionBean!!.orderNo
            jsonobject["plateId"] = debtCollectionBean!!.carLicense
            jsonobject["orderId"] = tradeNo
            jsonobject["payMoney"] = payMoney
            jsonobject["orderType"] = "2"
            param["attr"] = jsonobject
            mViewModel.qrNotice(param)
        }
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

    fun startPrint(it: PayResultBean) {
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
            oweCount = it.oweCount
        )
        val printList = BluePrint.instance?.blueToothDevice!!
        if (printList.size == 1) {
            Thread {
                val device = printList[0]
                var connectResult = BluePrint.instance?.connet(device.address)
                if (connectResult == 0) {
                    runOnUiThread {
                        ToastUtil.showBottomToast("开始打印")
                    }
                    BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
                }
            }.start()
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}