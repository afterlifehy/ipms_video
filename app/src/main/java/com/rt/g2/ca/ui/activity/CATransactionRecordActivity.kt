package com.rt.g2.ca.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.PrintInfoBean
import com.rt.base.bean.TransactionBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.gone
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.g2.R
import com.rt.g2.adapter.TransactionRecordAdapter
import com.rt.g2.databinding.ActivityTransactionRecordBinding
import com.rt.g2.mvvm.viewmodel.TransactionRecordViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.CA_TRANSACTION_RECORD)
class CATransactionRecordActivity : VbBaseActivity<TransactionRecordViewModel, ActivityTransactionRecordBinding>(), OnClickListener {
    var transactionRecordAdapter: TransactionRecordAdapter? = null
    var transactionRecordList: MutableList<TransactionBean> = ArrayList()
    var orderNo = ""
    var token = ""
    lateinit var ticketQrCode: String
    lateinit var currentTransactionBean:TransactionBean

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.交易记录信息)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))

        orderNo = intent.getStringExtra(ARouterMap.TRANSACTION_RECORD_ORDER_NO).toString()

        binding.rvTransactionRecord.setHasFixedSize(true)
        binding.rvTransactionRecord.layoutManager = LinearLayoutManager(this@CATransactionRecordActivity)
        transactionRecordAdapter = TransactionRecordAdapter(transactionRecordList, this)
        binding.rvTransactionRecord.adapter = transactionRecordAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
        }
        query()
    }

    private fun query() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
//        20230904JAZ038001P7A32A
        param["attr"] = jsonobject
        mViewModel.transactionInquiryByOrder(param)
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.fl_notification -> {
                var rxPermissions = RxPermissions(this@CATransactionRecordActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            showProgressDialog(20000)
                            currentTransactionBean = v.tag as TransactionBean
                            invoiceQrcode()
                        }
                    }
                } else {
                    showProgressDialog(20000)
                    currentTransactionBean = v.tag as TransactionBean
                    invoiceQrcode()
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            transactionInquiryByOrderLiveData.observe(this@CATransactionRecordActivity) {
                transactionRecordList.clear()
                transactionRecordList.addAll(it.result)
                if (transactionRecordList.size > 0) {
                    binding.rvTransactionRecord.show()
                    binding.layoutNoData.root.gone()
                    transactionRecordAdapter?.setList(transactionRecordList)
                } else {
                    binding.rvTransactionRecord.gone()
                    binding.layoutNoData.root.show()
                }
                dismissProgressDialog()
            }
            invoiceQrcodeLiveData.observe(this@CATransactionRecordActivity) {
                ticketQrCode = it.qrcode.toString()
                notificationInquiry()
            }
            notificationInquiryLiveData.observe(this@CATransactionRecordActivity) {
                dismissProgressDialog()
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
            errMsg.observe(this@CATransactionRecordActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CATransactionRecordActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun invoiceQrcode() {
        val param = HashMap<String, Any>()
        param["token"] = token
        param["orderId"] = currentTransactionBean.tradeNo
        param["plateId"] = currentTransactionBean.carLicense
        param["plateColor"] = 99
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.invoiceQrcode(param)
    }

    fun notificationInquiry(){
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["tradeNo"] = currentTransactionBean.tradeNo
//                    jsonobject["tradeNo"] = "20230825JAZ03850048412"
        jsonobject["token"] = token
        param["attr"] = jsonobject
        mViewModel.notificationInquiry(param)
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityTransactionRecordBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<TransactionRecordViewModel> {
        return TransactionRecordViewModel::class.java
    }
}