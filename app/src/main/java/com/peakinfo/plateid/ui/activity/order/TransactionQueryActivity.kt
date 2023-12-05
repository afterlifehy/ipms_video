package com.peakinfo.plateid.ui.activity.order

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
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.BluePrint
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.common.view.keyboard.KeyboardUtil
import com.peakinfo.common.view.keyboard.MyOnTouchListener
import com.peakinfo.common.view.keyboard.MyTextWatcher
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.TransactionQueryAdapter
import com.peakinfo.plateid.databinding.ActivityTransactionQueryBinding
import com.peakinfo.plateid.mvvm.viewmodel.TransactionQueryViewModel
import com.peakinfo.plateid.pop.DatePop
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.TRANSACTION_QUERY)
class TransactionQueryActivity : VbBaseActivity<TransactionQueryViewModel, ActivityTransactionQueryBinding>(), OnClickListener {
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

    override fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.交易查询)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.peakinfo.common.R.mipmap.ic_calendar)
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
            ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.车牌长度只能是7位或8位))
            return
        }
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["streetNo"] = streetNo
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
                datePop = DatePop(BaseApplication.instance(), startDate, endDate, object : DatePop.DateCallBack {
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
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@TransactionQueryActivity, 1)
            }

            R.id.tv_search -> {
                pageIndex = 1
                showProgressDialog(20000)
                query()
            }

            R.id.fl_notification -> {
                var rxPermissions = RxPermissions(this@TransactionQueryActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            showProgressDialog(20000)
                            currentTransactionBean = v.tag as TransactionBean
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
                    currentTransactionBean = v.tag as TransactionBean
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["tradeNo"] = currentTransactionBean?.tradeNo
                    jsonobject["token"] = token
                    param["attr"] = jsonobject
                    mViewModel.notificationInquiry(param)
                }

            }

            R.id.fl_paymentInquiry -> {
                currentTransactionBean = v.tag as TransactionBean
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["tradeNo"] = currentTransactionBean?.tradeNo
                jsonobject["token"] = token
                param["attr"] = jsonobject
                mViewModel.payResult(param)
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            transactionInquiryLiveData.observe(this@TransactionQueryActivity) {
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
            notificationInquiryLiveData.observe(this@TransactionQueryActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.开始打印))
                val payMoney = it.payMoney
                val printInfo = PrintInfoBean(
                    roadId = it.roadName,
                    plateId = it.carLicense,
                    payMoney = String.format("%.2f", payMoney.toFloat()),
                    orderId = currentTransactionBean!!.orderNo,
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
            payResultLiveData.observe(this@TransactionQueryActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.支付成功))
                currentTransactionBean?.hasPayed = "1"
                currentTransactionBean?.payedAmount = currentTransactionBean!!.oweMoney
                transactionQueryAdapter?.notifyItemChanged(transactionQueryList.indexOf(currentTransactionBean))
            }
            errMsg.observe(this@TransactionQueryActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
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