package com.peakinfo.plateid.ca.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.PrintInfoBean
import com.peakinfo.base.bean.ca.QueryPayBean
import com.peakinfo.base.bean.ca.UrgeBean
import com.peakinfo.base.bean.ca.UrgeMonthBean
import com.peakinfo.base.bean.ca.UrgeOrderBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.startArouter
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.event.RefreshDebtOrderListEvent
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.BluePrint
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.UrgeMonthAdapter
import com.peakinfo.plateid.adapter.UrgeOrderAdapter
import com.peakinfo.plateid.databinding.ActivityUrgeDetailBinding
import com.peakinfo.plateid.dialog.PaymentQrDialog
import com.peakinfo.plateid.mvvm.viewmodel.CAUrgeDetailViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.CA_URGE_DETAIL)
class CAUrgeDetailActivity : VbBaseActivity<CAUrgeDetailViewModel, ActivityUrgeDetailBinding>(), OnClickListener {
    var urgeOrderAdapter: UrgeOrderAdapter? = null
    var urgeMonthAdapter: UrgeMonthAdapter? = null
    var urgeOrderList: MutableList<UrgeOrderBean> = ArrayList()
    var urgeMonthList: MutableList<UrgeMonthBean> = ArrayList()
    var paymentQrDialog: PaymentQrDialog? = null
    lateinit var urgeBean: UrgeBean
    var urgeOrderBean: UrgeOrderBean? = null
    var count = 0
    var handler = Handler(Looper.getMainLooper())
    var token = ""
    var ticketQrCode = ""
    lateinit var payResultBean: PayResultBean

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = "催缴告知书详情"

        urgeBean = intent.getParcelableExtra(ARouterMap.URGE)!!
        binding.tvPlate.text = urgeBean.plateId

        binding.rvOrders.isNestedScrollingEnabled = true
        binding.rvOrders.setHasFixedSize(true)
        urgeOrderAdapter = UrgeOrderAdapter(urgeOrderList) { order: UrgeOrderBean ->
            urgeOrderBean = order
            val param = HashMap<String, Any>()
            param["token"] = token
            param["orderId"] = urgeBean.urgePayId
            param["orderType"] = "7"
            param["channel"] = "pos"
            mViewModel.payowemoney(param)
        }
        binding.rvOrders.adapter = urgeOrderAdapter

        binding.rvMonth.isNestedScrollingEnabled = true
        binding.rvMonth.setHasFixedSize(true)
        urgeMonthAdapter = UrgeMonthAdapter(urgeMonthList) {

        }
        binding.rvMonth.setHasFixedSize(true)
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rtvUrge.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            urgepaydetail()
        }
    }

    fun urgepaydetail() {
        showProgressDialog(60000)
        val param = HashMap<String, Any>()
        param["token"] = token
        param["urgePayId"] = urgeBean.urgePayId
        param["plateId"] = urgeBean.plateId
        param["plateColor"] = urgeBean.plateColor
        mViewModel.urgepaydetail(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }

            R.id.rtv_urge -> {
                startArouter(ARouterMap.CA_COLLECTION_MANAGEMENT, data = Bundle().apply {
                    putParcelable(ARouterMap.URGE, urgeBean)
                })
            }
        }
    }

    fun urgepayQrcode(){
        val param = HashMap<String, Any>()
        param["token"] = token
        param["urgePayId"] = urgeBean.urgePayId
        param["plateId"] = urgeBean.plateId
        param["plateColor"] = urgeBean.plateColor
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.urgepayQrcode(param)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            urgepaydetailLiveData.observe(this@CAUrgeDetailActivity) {
                dismissProgressDialog()
                urgeOrderList.clear()
                urgeOrderList.addAll(it.oweList)
                urgeOrderAdapter?.setList(urgeOrderList)

                urgeMonthList.clear()
                urgeMonthList.addAll(it.monthList)
                urgeMonthAdapter?.setList(urgeMonthList)
            }
            payowemoneyLiveData.observe(this@CAUrgeDetailActivity) {
                val param = HashMap<String, Any>()
                param["urgePayId"] = urgeBean.urgePayId
                param["orderId"] = it.orderId
                param["payMoney"] = it.amount
                param["dataTime"] = System.currentTimeMillis()
                mViewModel.consumeUrgePay(param)
            }
            consumeUrgePayLiveData.observe(this@CAUrgeDetailActivity) {
                paymentQrDialog =
                    PaymentQrDialog(
                        it.qrCode,
                        "",
                        AppUtil.keepNDecimals((urgeOrderBean!!.oweMoney / 100).toString(), 2),
                        urgeBean.plateId
                    )
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener { handler.removeCallbacks(runnable) }
                count = 0
                handler.postDelayed(runnable, 2000)
            }
            querypayLiveData.observe(this@CAUrgeDetailActivity) {
                if (it != null && it.payMoney != null) {
                    handler.removeCallbacks(runnable)
                    ToastUtil.showBottomToast("支付成功")
                    payResultNotice(it)
                }
            }
            payResultNoticeLiveData.observe(this@CAUrgeDetailActivity) {
                payResultBean = it
                if (!isDestroyed && !isFinishing) {
                    if (paymentQrDialog != null) {
                        paymentQrDialog?.dismiss()
                    }
                }
                invoiceQrcode()
                urgepaydetail()
            }
            invoiceQrcodeLiveData.observe(this@CAUrgeDetailActivity) {
                ticketQrCode = it.qrcode.toString()
                var rxPermissions = RxPermissions(this@CAUrgeDetailActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            startPrint(payResultBean)
                        }
                    }
                } else {
                    startPrint(payResultBean)
                }
            }
            urgepayQrcodeLiveData.observe(this@CAUrgeDetailActivity) {

            }
            errMsg.observe(this@CAUrgeDetailActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CAUrgeDetailActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun querypay() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["orderId"] = urgeOrderBean!!.orderId
        mViewModel.querypay(param)
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

    fun invoiceQrcode() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["orderId"] = urgeBean.urgePayId
        param["plateId"] = urgeBean.plateId
        param["plateColor"] = urgeBean.plateColor
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.invoiceQrcode(param)
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
            ticketQrCode = ticketQrCode,
            orderType = it.orderType
        )
        val printList = BluePrint.instance?.blueToothDevice!!
        if (printList.size >= 1) {
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

    val runnable = object : Runnable {
        override fun run() {
            if (count < 60) {
                if (!isFinishing && !isDestroyed) {
                    querypay()
                }
                count++
                handler.postDelayed(this, 3000)
            } else {
                paymentQrDialog?.dismiss()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityUrgeDetailBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

    override val isFullScreen: Boolean
        get() = true

}