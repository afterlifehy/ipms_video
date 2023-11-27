package com.rt.ipms_video.ui.activity.income

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.TimeUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.IncomeCountingBean
import com.rt.base.dialog.DialogHelp
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.AppUtil
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityIncomeCountingBinding
import com.rt.ipms_video.mvvm.viewmodel.IncomeCountingViewModel
import com.rt.ipms_video.pop.DatePop
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zrq.spanbuilder.TextStyle
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.INCOME_COUNTING)
class IncomeCountingActivity : VbBaseActivity<IncomeCountingViewModel, ActivityIncomeCountingBinding>(), OnClickListener {
    val sizes = intArrayOf(19, 16)
    val colors = intArrayOf(com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4)
    val styles = arrayOf(TextStyle.BOLD, TextStyle.NORMAL)
    private val print = BluePrint(this)
    var datePop: DatePop? = null
    var loginName = ""
    var startDate = ""
    var endDate = ""
    var incomeCountingBean: IncomeCountingBean? = null

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.营收盘点)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_calendar)
        binding.layoutToolbar.ivRight.show()

        runBlocking {
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.tvTotalChargeTitle.setOnClickListener(this)
        binding.rtvPrint.setOnClickListener(this)
    }

    override fun initData() {
        endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
        startDate = endDate.substring(0, 8) + "01"
        getIncomeCounting()
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
                        binding.rtvDateRange.text = "${startDate}~${endDate}"
                        getIncomeCounting()
                    }

                })
                datePop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.tv_totalChargeTitle -> {
                DialogHelp.Builder().setTitle(i18N(com.rt.base.R.string.提示)).setContentMsg(i18N(com.rt.base.R.string.今日总收费介绍))
                    .setRightMsg(i18N(com.rt.base.R.string.确定)).setCancelable(true)
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {
                        }

                        override fun onRightClickLinsener(msg: String) {
                        }

                    }).isAloneButton(true).build(this@IncomeCountingActivity).showDailog()
            }

            R.id.rtv_print -> {
                var str =
                    "receipt," + loginName + "," + startDate + "," + endDate + ",payMoneyToday" + incomeCountingBean?.payMoneyToday +
                            ",orderTotalToday" + incomeCountingBean?.orderTotalToday + ",unclearedTotal" + incomeCountingBean?.unclearedTotal +
                            ",payMoneyTotal" + incomeCountingBean?.payMoneyTotal + ",orderTotal" + incomeCountingBean?.orderTotal
                var rxPermissions = RxPermissions(this@IncomeCountingActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            Thread {
                                if (print.zpSDK == null) {
                                    print.connet()
                                }
                                print.zkblueprint(str)
                            }.start()
                        }
                    }
                } else {
                    Thread {
                        if (print.zpSDK == null) {
                            print.connet()
                        }
                        print.zkblueprint(str)
                    }.start()
                }
            }
        }
    }

    fun getIncomeCounting() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["loginName"] = loginName
        jsonobject["startDate"] = startDate
        jsonobject["endDate"] = endDate
        param["attr"] = jsonobject
        mViewModel.incomeCounting(param)
    }

    override fun startObserve() {
        mViewModel.apply {
            incomeCountingLiveData.observe(this@IncomeCountingActivity) {
                dismissProgressDialog()
                incomeCountingBean = it
                val strings = arrayOf(it.payMoneyToday.toString(), "元")
                binding.tvTotalCharge.text = AppUtil.getSpan(strings, sizes, colors, styles)
                binding.tvTodayOrderNum.text = "${it.orderTotalToday}笔"
                binding.tvArrearsOrderNum.text = "${it.unclearedTotal}笔"
                binding.tvTotalIncome.text = "${it.payMoneyTotal}元"
                binding.tvOrderPlaced.text = "${it.orderTotal}笔"
            }
            errMsg.observe(this@IncomeCountingActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
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