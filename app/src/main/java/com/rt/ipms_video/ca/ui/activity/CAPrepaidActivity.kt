package com.rt.ipms_video.ca.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ClickUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.PayResultBean
import com.rt.base.bean.PrintInfoBean
import com.rt.base.bean.ca.QueryPayBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.event.RefreshParkingSpaceEvent
import com.rt.base.ext.i18N
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.realm.RealmUtil
import com.rt.common.util.AppUtil
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityPrepaidBinding
import com.rt.ipms_video.dialog.PaymentQrDialog
import com.rt.ipms_video.mvvm.viewmodel.PrepaidViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.CA_PREPAID)
class CAPrepaidActivity : VbBaseActivity<PrepaidViewModel, ActivityPrepaidBinding>(), OnClickListener {
    var timeDuration = 1.0
    var maxDuration = 99.0
    var minDuration = 1.0
    var paymentQrDialog: PaymentQrDialog? = null

    var parkingNo = ""
    var carLicense = ""
    var carColor = ""
    var orderNo = ""

    var token = ""
    var loginName = ""

    var count = 0
    var handler = Handler(Looper.getMainLooper())
    var tradeNo = ""
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

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.预支付)

        carLicense = intent.getStringExtra(ARouterMap.PREPAID_CARLICENSE).toString()
        carColor = intent.getStringExtra(ARouterMap.PREPAID_CARCOLOR).toString()
        parkingNo = intent.getStringExtra(ARouterMap.PREPAID_PARKING_NO).toString()
        orderNo = intent.getStringExtra(ARouterMap.PREPAID_ORDER_NO).toString()

        binding.tvPlate.text = carLicense
        binding.tvParkingNo.text = parkingNo

        val street = RealmUtil.instance?.findCurrentStreet()
        maxDuration = street?.prepayDuration!!
        if (maxDuration < 1.0) {
            maxDuration = 1.0
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflAdd.setOnClickListener(this)
        binding.rflMinus.setOnClickListener(this)
        ClickUtils.applySingleDebouncing(binding.rflScanPay, 3000, this)
        binding.etTimeDuration.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val value = s.toString()
                if (value == ".") {
                    binding.etTimeDuration.setText("0.")
                    timeDuration = 0.0
                    return
                }
                if (value.contains(".")) {
                    val splitInput = value.split(".")
                    if (splitInput.size > 1 && splitInput[1].length > 1) {
                        s?.delete(s.length - 1, s.length)
                    }
                    timeDuration = value.toDouble()
                    if (timeDuration < minDuration) {
                        timeDuration = minDuration
                        binding.etTimeDuration.setText(minDuration.toString())
                        binding.etTimeDuration.setSelection(minDuration.toString().length)
                        return
                    }
                } else if (value.length > 0) {
                    timeDuration = value.toDouble()
                } else {
                    timeDuration = minDuration
                }
                if (timeDuration > maxDuration) {
                    timeDuration = maxDuration
                    binding.etTimeDuration.setText(timeDuration.toString())
                    binding.etTimeDuration.setSelection(timeDuration.toString().length)
                }
            }

        })
    }

    override fun initData() {
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
            prepay()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_add -> {
                if (timeDuration == maxDuration) {
                    return
                }
                if (timeDuration < minDuration) {
                    timeDuration = minDuration
                } else {
                    timeDuration += 0.5
                }
                binding.etTimeDuration.setText(timeDuration.toString())
                binding.etTimeDuration.setSelection(timeDuration.toString().length)
            }

            R.id.rfl_minus -> {
                if (timeDuration <= minDuration) {
                    timeDuration = minDuration
                } else {
                    timeDuration -= 0.5
                }
                binding.etTimeDuration.setText(timeDuration.toString())
                binding.etTimeDuration.setSelection(timeDuration.toString().length)
            }

            R.id.rfl_scanPay -> {
                if (timeDuration >= minDuration) {
                    prepay()
                } else {
                    ToastUtil.showMiddleToast("时长过短")
                    return
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            prepayLiveData.observe(this@CAPrepaidActivity) {
                dismissProgressDialog()
                tradeNo = it.orderId.toString()
                paymentQrDialog =
                    PaymentQrDialog(it.qrCode.toString(), "", AppUtil.keepNDecimals((it.payMoney!! / 100).toString(), 2), carLicense)
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener { handler.removeCallbacks(runnable) }
                count = 0
                handler.postDelayed(runnable, 2000)
                qrNotice(it.payMoney!!)
            }
            querypayLiveData.observe(this@CAPrepaidActivity) {
                dismissProgressDialog()
                handler.removeCallbacks(runnable)
                ToastUtil.showBottomToast(i18N(com.rt.base.R.string.支付成功))
                queryPayBean = it
                invoiceQrcode(it.orderId)
            }
            invoiceQrcodeLiveData.observe(this@CAPrepaidActivity) {
                ticketQrCode = it.qrcode.toString()
                payResultNotice(queryPayBean)
            }
            payResultNoticeLiveData.observe(this@CAPrepaidActivity) {
                if (paymentQrDialog != null) {
                    paymentQrDialog?.dismiss()
                }
                val payResultBean = it
                var rxPermissions = RxPermissions(this@CAPrepaidActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            startPrint(payResultBean)
                        }
                    }
                } else {
                    startPrint(it)
                }
                EventBus.getDefault().post(RefreshParkingSpaceEvent())
                onBackPressedSupport()
            }
            errMsg.observe(this@CAPrepaidActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CAPrepaidActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun prepay() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["businessId"] = orderNo
        param["payDuration"] = timeDuration.toInt() * 60
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.prepay(param)
    }

    fun querypay() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["orderId"] = tradeNo
        mViewModel.querypay(param)
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

    fun qrNotice(payMoney: Int) {
        runBlocking {
            val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["loginName"] = loginName
            jsonobject["businessId"] = orderNo
            jsonobject["plateId"] = carLicense
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
            oweCount = it.oweCount,
            ticketQrCode = ticketQrCode
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

    override fun providerVMClass(): Class<PrepaidViewModel> {
        return PrepaidViewModel::class.java
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityPrepaidBinding.inflate(layoutInflater)
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onReloadData() {

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