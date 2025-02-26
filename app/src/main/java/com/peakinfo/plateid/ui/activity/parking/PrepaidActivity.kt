package com.peakinfo.plateid.ui.activity.parking

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
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.event.RefreshParkingSpaceEvent
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
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
class PrepaidActivity : VbBaseActivity<PrepaidViewModel, ActivityPrepaidBinding>(), OnClickListener {
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
                if (value.contains(".")) {
                    val splitInput = value.split(".")
                    if (splitInput.size > 1 && splitInput[1].length > 1) {
                        s?.delete(s.length - 1, s.length)
                    }
//                    if (value.endsWith(".") && value.length > 1) {
//                        timeDuration = value.replace(".", "").toDouble()
//                    } else if (value.endsWith(".") && value.length <= 1) {
//                        timeDuration = minAmount - 0.5
//                    } else {
                        timeDuration = value.toDouble()
//                    }
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
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
            prePayFee()
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
                    prePayFee()
                } else {
                    ToastUtil.showMiddleToast("时长过短")
                    return
                }
            }
        }
    }

    fun prePayFee() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        jsonobject["token"] = token
        jsonobject["parkingHours"] = timeDuration.toInt().toString()
        jsonobject["orderType"] = "1"
        param["attr"] = jsonobject
        mViewModel.prePayFeeInquiry(param)
    }

    @SuppressLint("CheckResult")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            prePayFeeInquiryLiveData.observe(this@PrepaidActivity) {
                dismissProgressDialog()
                tradeNo = it.tradeNo
                paymentQrDialog = PaymentQrDialog(it.qrCode, "", AppUtil.keepNDecimals(it.totalAmount.toString(), 2), carLicense)
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener { handler.removeCallbacks(runnable) }
                count = 0
                handler.postDelayed(runnable, 2000)
            }
            payResultInquiryLiveData.observe(this@PrepaidActivity) {
                dismissProgressDialog()
                if (it != null && it.payMoney != null) {
                    handler.removeCallbacks(runnable)
                    ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.支付成功))
                    if (paymentQrDialog != null) {
                        paymentQrDialog?.dismiss()
                    }
                    val payResultBean = it
                    var rxPermissions = RxPermissions(this@PrepaidActivity)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                            if (it) {
                                startPrint(payResultBean)
                            }
                        }
                    } else {
                        startPrint(payResultBean)
                    }
                    EventBus.getDefault().post(RefreshParkingSpaceEvent())
                    onBackPressedSupport()
                }
            }
            errMsg.observe(this@PrepaidActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@PrepaidActivity) {
                dismissProgressDialog()
            }
        }
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

    fun checkPayResult() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["token"] = token
        jsonobject["tradeNo"] = tradeNo
        param["attr"] = jsonobject
        mViewModel.payResult(param)
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
        Thread {
            runOnUiThread {
                ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.开始打印))
            }
            BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
        }.start()
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