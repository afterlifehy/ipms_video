package com.rt.g2.ui.activity.income

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
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.IncomeCountingBean
import com.rt.base.bean.Summary
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.realm.RealmUtil
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.g2.R
import com.rt.g2.adapter.MonthSummaryAdapter
import com.rt.g2.adapter.TodaySummaryAdapter
import com.rt.g2.databinding.ActivityIncomeCountingBinding
import com.rt.g2.mvvm.viewmodel.IncomeCountingViewModel
import com.rt.g2.pop.DatePop
import com.tbruyelle.rxpermissions3.RxPermissions
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
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.营收盘点)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_calendar)
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
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
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
                                ToastUtil.showBottomToast(i18N(com.rt.base.R.string.查询时间间隔不得超过90天))
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
                if (searchRange == "1") {
                    incomeCountingBean?.range = startDate + "~" + endDate
                }
                incomeCountingBean?.list1 = todaySummaryList as ArrayList<Summary>
                incomeCountingBean?.list2 = monthSummaryList as ArrayList<Summary>
                var str = "receipt,"
                var rxPermissions = RxPermissions(this@IncomeCountingActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            val printList = BluePrint.instance?.blueToothDevice!!
                            if (printList.size == 1) {
                                Thread {
                                    val device = printList[0]
                                    var connectResult = BluePrint.instance?.connet(device.address)
                                    if (connectResult == 0) {
                                        runOnUiThread {
                                            ToastUtil.showBottomToast("开始打印")
                                        }
                                        BluePrint.instance?.zkblueprint(str + JSONObject.toJSONString(incomeCountingBean))
                                    }
                                }.start()
                            }
                        }
                    }
                } else {
                    val printList = BluePrint.instance?.blueToothDevice!!
                    if (printList.size == 1) {
                        Thread {
                            val device = printList[0]
                            var connectResult = BluePrint.instance?.connet(device.address)
                            if (connectResult == 0) {
                                runOnUiThread {
                                    ToastUtil.showBottomToast("开始打印")
                                }
                                BluePrint.instance?.zkblueprint(str + JSONObject.toJSONString(incomeCountingBean))
                            }
                        }.start()
                    }
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
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@IncomeCountingActivity) {
                dismissProgressDialog()
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