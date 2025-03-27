package com.peakinfo.plateid.ca.ui.activity

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
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.PrintInfoBean
import com.peakinfo.base.bean.ca.QueryPayBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.event.RefreshParkingSpaceEvent
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.event.RefreshParkingLotEvent
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.BluePrint
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityPrepaidBinding
import com.peakinfo.plateid.dialog.PaymentQrDialog
import com.peakinfo.plateid.mvvm.viewmodel.PrepaidViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.PREPAID)
class CAPrepaidActivity : VbBaseActivity<PrepaidViewModel, ActivityPrepaidBinding>(), OnClickListener {
    var timeDuration = 1.0
    var maxDuration = 99.0
    var minDuration = 1.0
    var paymentQrDialog: PaymentQrDialog? = null

    var parkingNo = ""
    var carLicense = ""
    var orderNo = ""

    var token = ""
    var loginName = ""

    var count = 0
    var handler = Handler(Looper.getMainLooper())
    var tradeNo = ""

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
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.预支付)

        carLicense = intent.getStringExtra(ARouterMap.PREPAID_CARLICENSE).toString()
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
                paymentQrDialog = PaymentQrDialog(it.qrCode.toString(), "", AppUtil.keepNDecimals(it.payMoney.toString(), 2), carLicense)
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener { handler.removeCallbacks(runnable) }
                count = 0
                handler.postDelayed(runnable, 2000)
                qrNotice(it.payMoney!!)
            }
            querypayLiveData.observe(this@CAPrepaidActivity) {
                dismissProgressDialog()
                handler.removeCallbacks(runnable)
                ToastUtil.showBottomToast(i18N(com.peakinfo.base.R.string.支付成功))
                payResultNotice(it)
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
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@CAPrepaidActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun prepay() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["token"] = token
        jsonobject["businessId"] = orderNo
        jsonobject["payDuration"] = timeDuration.toInt() * 60
        jsonobject["dataTime"] = System.currentTimeMillis()
        param["attr"] = jsonobject
        mViewModel.prepay(param)
    }

    fun querypay() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["orderId"] = tradeNo
        mViewModel.querypay(param)
    }

    fun qrNotice(payMoney:Int) {
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