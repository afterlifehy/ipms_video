package com.rt.ipms_video.ca.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.TimeUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.PrintInfoBean
import com.rt.base.bean.TransactionBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.gone
import com.rt.base.ext.i18N
import com.rt.base.ext.i18n
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.realm.RealmUtil
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.common.view.keyboard.MyOnTouchListener
import com.rt.common.view.keyboard.MyTextWatcher
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.TransactionQueryAdapter
import com.rt.ipms_video.databinding.ActivityTransactionQueryBinding
import com.rt.ipms_video.mvvm.viewmodel.TransactionQueryViewModel
import com.rt.ipms_video.pop.DatePop
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.CA_TRANSACTION_QUERY)
class CATransactionQueryActivity : VbBaseActivity<TransactionQueryViewModel, ActivityTransactionQueryBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil

    var transactionQueryAdapter: TransactionQueryAdapter? = null
    var transactionQueryList: MutableList<TransactionBean> = ArrayList()
    var datePop: DatePop? = null
    var pageIndex = 1
    var pageSize = 10
    var startDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
    var endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
    var streetNo = ""
    var token = ""
    var currentTransactionBean: TransactionBean? = null
    var loginName = ""
    var ticketQrCode = ""
    var oweCount = 0

    override fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.交易查询)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_calendar_white)
        binding.layoutToolbar.ivRight.show()

        binding.rvTransaction.setHasFixedSize(true)
        binding.rvTransaction.layoutManager = LinearLayoutManager(this)
        transactionQueryAdapter = TransactionQueryAdapter(transactionQueryList, this)
        binding.rvTransaction.adapter = transactionQueryAdapter

        initKeyboard()
    }

    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.etSearch.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.etSearch)
        }

        binding.etSearch.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))
        binding.etSearch.setOnTouchListener(MyOnTouchListener(true, binding.etSearch, keyboardUtil))
        binding.root.setOnClickListener {
            keyboardUtil.hideKeyboard()
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.tvSearch.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
        binding.ivCamera.setOnClickListener(this)
        binding.srlTransaction.setOnRefreshListener {
            pageIndex = 1
            binding.srlTransaction.finishRefresh(5000)
            transactionQueryList.clear()
            query()
        }
        binding.srlTransaction.setOnLoadMoreListener {
            pageIndex++
            binding.srlTransaction.finishLoadMore(5000)
            query()
        }
    }

    override fun initData() {
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
        }
        streetNo = RealmUtil.instance?.findCurrentStreet()!!.streetNo
        showProgressDialog(20000)
        query()
    }

    fun query() {
        keyboardUtil.hideKeyboard()
        val searchContent = binding.etSearch.text.toString()
        if (searchContent.isNotEmpty() && (searchContent.length != 7 && searchContent.length != 8)) {
            dismissProgressDialog()
            ToastUtil.showBottomToast(i18N(com.rt.base.R.string.车牌长度只能是7位或8位))
            return
        }
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["streetNo"] = streetNo
        jsonobject["loginName"] = loginName
        jsonobject["carLicense"] = searchContent
        jsonobject["startDate"] = startDate
        jsonobject["endDate"] = endDate
        jsonobject["page"] = pageIndex
        jsonobject["size"] = pageSize
        param["attr"] = jsonobject
        mViewModel.transactionInquiry(param)
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                datePop = DatePop(BaseApplication.instance(), startDate, endDate, 0, object : DatePop.DateCallBack {
                    override fun selectDate(startTime: String, endTime: String) {
                        startDate = startTime
                        endDate = endTime
                        pageIndex = 1
                        showProgressDialog(20000)
                        query()
                    }

                })
                datePop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.iv_camera -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@CATransactionQueryActivity, 1)
            }

            R.id.tv_search -> {
                pageIndex = 1
                showProgressDialog(20000)
                query()
            }

            R.id.fl_notification -> {
                currentTransactionBean = v.tag as TransactionBean
                var rxPermissions = RxPermissions(this@CATransactionQueryActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            showProgressDialog(20000)
                            debtInquiry()
                        }
                    }
                } else {
                    showProgressDialog(20000)
                    debtInquiry()
                }
            }

            R.id.fl_paymentInquiry -> {
                currentTransactionBean = v.tag as TransactionBean
                val param = HashMap<String, Any>()
                param["token"] = token
                param["orderId"] = currentTransactionBean?.tradeNo.toString()
                mViewModel.querypay(param)
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }
        }
    }

    fun debtInquiry() {
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["token"] = token
            jsonobject["carLicense"] = currentTransactionBean?.carLicense
            param["attr"] = jsonobject
            mViewModel.debtInquiry(param)
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            transactionInquiryLiveData.observe(this@CATransactionQueryActivity) {
                dismissProgressDialog()
                val tempList = it.result
                if (pageIndex == 1) {
                    if (tempList.isEmpty()) {
                        transactionQueryAdapter?.setNewInstance(null)
                        binding.rvTransaction.gone()
                        binding.layoutNoData.root.show()
                        binding.srlTransaction.finishRefresh()
                    } else {
                        transactionQueryList.clear()
                        transactionQueryList.addAll(tempList)
                        transactionQueryAdapter?.setList(transactionQueryList)
                        binding.srlTransaction.finishRefresh()
                        binding.rvTransaction.show()
                        binding.layoutNoData.root.gone()
                    }
                } else {
                    if (tempList.isEmpty()) {
                        pageIndex--
                        binding.srlTransaction.finishLoadMoreWithNoMoreData()
                    } else {
                        transactionQueryList.addAll(tempList)
                        transactionQueryAdapter?.setList(transactionQueryList)
                        binding.srlTransaction.finishLoadMore(300)
                    }
                }
            }
            invoiceQrcodeLiveData.observe(this@CATransactionQueryActivity) {
                ticketQrCode = it.qrcode.toString()
                notificationInquiry()
            }
            debtInquiryLiveData.observe(this@CATransactionQueryActivity) {
                dismissProgressDialog()
                if (it.result != null) {
                    oweCount = it.result.size
                }
                val param = HashMap<String, Any>()
                param["token"] = token
                param["orderId"] = currentTransactionBean!!.tradeNo
                param["plateId"] = currentTransactionBean!!.carLicense
                param["plateColor"] = 99
                param["dataTime"] = System.currentTimeMillis()
                mViewModel.invoiceQrcode(param)
            }
            notificationInquiryLiveData.observe(this@CATransactionQueryActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(i18n(com.rt.base.R.string.开始打印))
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
                    ticketQrCode = ticketQrCode,
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
            querypayLiveData.observe(this@CATransactionQueryActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(i18N(com.rt.base.R.string.支付成功))
                currentTransactionBean?.hasPayed = "1"
                currentTransactionBean?.payedAmount = currentTransactionBean!!.oweMoney
                transactionQueryAdapter?.notifyItemChanged(transactionQueryList.indexOf(currentTransactionBean))
            }
            errMsg.observe(this@CATransactionQueryActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CATransactionQueryActivity) {
                dismissProgressDialog()
            }
        }
    }

    @SuppressLint("CheckResult")
    fun notificationInquiry() {
        var rxPermissions = RxPermissions(this@CATransactionQueryActivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                if (it) {
                    showProgressDialog(20000)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["tradeNo"] = currentTransactionBean?.tradeNo
                    jsonobject["token"] = token
                    param["attr"] = jsonobject
                    mViewModel.notificationInquiry(param)
                }
            }
        } else {
            showProgressDialog(20000)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["tradeNo"] = currentTransactionBean?.tradeNo
            jsonobject["token"] = token
            param["attr"] = jsonobject
            mViewModel.notificationInquiry(param)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                val plate = data?.getStringExtra("plate")
                if (!plate.isNullOrEmpty()) {
                    val plateId = if (plate.contains("新能源")) {
                        plate.substring(plate.length - 8, plate.length)
                    } else {
                        plate.substring(plate.length.minus(7) ?: 0, plate.length)
                    }
                    binding.etSearch.setText(plateId)
                    binding.etSearch.setSelection(plateId.length)
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityTransactionQueryBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<TransactionQueryViewModel> {
        return TransactionQueryViewModel::class.java
    }

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyboardUtil.isShow()) {
                keyboardUtil.hideKeyboard()
            } else {
                return super.onKeyDown(keyCode, event)
            }
        }
        return false
    }

}