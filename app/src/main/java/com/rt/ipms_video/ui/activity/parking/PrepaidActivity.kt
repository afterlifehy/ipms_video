package com.rt.ipms_video.ui.activity.parking

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
    var oweCount = 0

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.预支付)

        carLicense = intent.getStringExtra(ARouterMap.PREPAID_CARLICENSE).toString()
        parkingNo = intent.getStringExtra(ARouterMap.PREPAID_PARKING_NO).toString()
        orderNo = intent.getStringExtra(ARouterMap.PREPAID_ORDER_NO).toString()
        oweCount = intent.getIntExtra(ARouterMap.PREPAID_OWE_COUNT, 0)

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
                    ToastUtil.showMiddleToast(i18N(com.rt.base.R.string.支付成功))
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
            oweCount = oweCount,
            ticketQrCode = it.qrcode,
            orderType = it.orderType
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