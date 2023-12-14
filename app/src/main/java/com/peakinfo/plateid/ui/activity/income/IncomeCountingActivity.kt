package com.peakinfo.plateid.ui.activity.income

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.IncomeCountingBean
import com.peakinfo.base.bean.Summary
import com.peakinfo.base.dialog.DialogHelp
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.BluePrint
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.MonthSummaryAdapter
import com.peakinfo.plateid.adapter.TodaySummaryAdapter
import com.peakinfo.plateid.databinding.ActivityIncomeCountingBinding
import com.peakinfo.plateid.mvvm.viewmodel.IncomeCountingViewModel
import com.peakinfo.plateid.pop.DatePop
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zrq.spanbuilder.TextStyle
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

@Route(path = ARouterMap.INCOME_COUNTING)
class IncomeCountingActivity : VbBaseActivity<IncomeCountingViewModel, ActivityIncomeCountingBinding>(), OnClickListener {
    var datePop: DatePop? = null
    var startDate = ""
    var endDate = ""
    var streetNos = ""
    var incomeCountingBean: IncomeCountingBean? = null
    var todaySummaryAdapter: TodaySummaryAdapter? = null
    var todaySummaryList: MutableList<Summary> = ArrayList()
    var monthSummaryAdapter: MonthSummaryAdapter? = null
    var monthSummaryList: MutableList<Summary> = ArrayList()
    var loginName = ""
    var searchRange = "0"

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.营收盘点)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.peakinfo.common.R.mipmap.ic_calendar)
        binding.layoutToolbar.ivRight.show()

        binding.rvTodaySummary.setHasFixedSize(true)
        binding.rvTodaySummary.layoutManager = LinearLayoutManager(this)
        todaySummaryAdapter = TodaySummaryAdapter(todaySummaryList)
        binding.rvTodaySummary.adapter = todaySummaryAdapter

        binding.rvMonthSummary.setHasFixedSize(true)
        binding.rvMonthSummary.layoutManager = LinearLayoutManager(this)
        monthSummaryAdapter = MonthSummaryAdapter(monthSummaryList)
        binding.rvMonthSummary.adapter = monthSummaryAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rtvPrint.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
        endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
        startDate = endDate.substring(0, 8) + "01"
        val streetList = RealmUtil.instance?.findCheckedStreetList()
        if (streetList != null) {
            streetNos = streetList.joinToString(separator = ",") { it.streetNo }
        }
        getIncomeCounting()
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                if (datePop == null) {
                    datePop = DatePop(BaseApplication.instance(), startDate, endDate, 1, object : DatePop.DateCallBack {
                        override fun selectDate(startTime: String, endTime: String) {
                            startDate = startTime
                            endDate = endTime
                            val difference = TimeUtils.getTimeSpan(endTime, startTime, SimpleDateFormat("yyyy-MM-dd"), TimeConstants.DAY)
                            if (difference > 90) {
                                ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.查询时间间隔不得超过90天))
                                return
                            }
                            binding.rtvDateRange.text = "统计时间：${startDate}~${endDate}"
                            getIncomeCounting()
                        }

                    })
                }
                datePop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.rtv_print -> {
                incomeCountingBean?.loginName = loginName
                incomeCountingBean?.range = startDate + "~" + endDate
                incomeCountingBean?.list1 = todaySummaryList as ArrayList<Summary>
                incomeCountingBean?.list2 = monthSummaryList as ArrayList<Summary>
                var str = "receipt,"
                var rxPermissions = RxPermissions(this@IncomeCountingActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.开始打印))
                            Thread {
                                BluePrint.instance?.zkblueprint(str + JSONObject.toJSONString(incomeCountingBean))
                            }.start()
                        }
                    }
                } else {
                    ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.开始打印))
                    Thread {
                        BluePrint.instance?.zkblueprint(str + JSONObject.toJSONString(incomeCountingBean))
                    }.start()
                }
            }
        }
    }

    fun getIncomeCounting() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["streetNos"] = streetNos
        jsonobject["startDate"] = startDate
        jsonobject["endDate"] = endDate
        jsonobject["loginName"] = loginName
        jsonobject["searchRange"] = searchRange
        param["attr"] = jsonobject
        mViewModel.incomeCounting(param)
    }

    override fun startObserve() {
        mViewModel.apply {
            incomeCountingLiveData.observe(this@IncomeCountingActivity) {
                dismissProgressDialog()
                incomeCountingBean = it
                todaySummaryList.clear()
                todaySummaryList.addAll(incomeCountingBean!!.list1)
                todaySummaryAdapter?.setList(todaySummaryList)
                if (searchRange == "1") {
                    binding.rllMonth.show()
                    monthSummaryList.clear()
                    monthSummaryList.addAll(incomeCountingBean!!.list2)
                    monthSummaryAdapter?.setList(monthSummaryList)
                }
                searchRange = "1"
            }
            errMsg.observe(this@IncomeCountingActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityIncomeCountingBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<IncomeCountingViewModel> {
        return IncomeCountingViewModel::class.java
    }
}