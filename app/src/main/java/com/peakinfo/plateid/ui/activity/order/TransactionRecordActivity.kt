package com.peakinfo.plateid.ui.activity.order

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
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.PrintInfoBean
import com.peakinfo.base.bean.TransactionBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.util.BluePrint
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.TransactionRecordAdapter
import com.peakinfo.plateid.databinding.ActivityTransactionRecordBinding
import com.peakinfo.plateid.mvvm.viewmodel.TransactionRecordViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.TRANSACTION_RECORD)
class TransactionRecordActivity : VbBaseActivity<TransactionRecordViewModel, ActivityTransactionRecordBinding>(), OnClickListener {
    var transactionRecordAdapter: TransactionRecordAdapter? = null
    var transactionRecordList: MutableList<TransactionBean> = ArrayList()
    var orderNo = ""
    var token = ""

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.交易记录信息)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

        orderNo = intent.getStringExtra(ARouterMap.TRANSACTION_RECORD_ORDER_NO).toString()

        binding.rvTransactionRecord.setHasFixedSize(true)
        binding.rvTransactionRecord.layoutManager = LinearLayoutManager(this@TransactionRecordActivity)
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
                var rxPermissions = RxPermissions(this@TransactionRecordActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            showProgressDialog(20000)
                            val transactionBean = v.tag as TransactionBean
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
//                            jsonobject["tradeNo"] = "20230825JAZ03850048412"
                            jsonobject["tradeNo"] = transactionBean.tradeNo
                            jsonobject["token"] = token
                            param["attr"] = jsonobject
                            mViewModel.notificationInquiry(param)
                        }
                    }
                } else {
                    showProgressDialog(20000)
                    val transactionBean = v.tag as TransactionBean
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["tradeNo"] = transactionBean.tradeNo
//                    jsonobject["tradeNo"] = "20230825JAZ03850048412"
                    jsonobject["token"] = token
                    param["attr"] = jsonobject
                    mViewModel.notificationInquiry(param)
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            transactionInquiryByOrderLiveData.observe(this@TransactionRecordActivity) {
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
            notificationInquiryLiveData.observe(this@TransactionRecordActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.开始打印))
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
                Thread {
                    BluePrint.instance?.zkblueprint(JSONObject.toJSONString(printInfo))
                }.start()
            }
            errMsg.observe(this@TransactionRecordActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@TransactionRecordActivity) {
                dismissProgressDialog()
            }
        }
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