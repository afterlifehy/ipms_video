package com.rt.ipms_video.ui.activity.order

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.DebtCollectionBean
import com.rt.base.bean.PayResultBean
import com.rt.base.bean.PrintInfoBean
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
import com.rt.ipms_video.databinding.ActivityDebtOrderDetailBinding
import com.rt.ipms_video.dialog.PaymentQrDialog
import com.rt.ipms_video.mvvm.viewmodel.DebtOrderDetailViewModel
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.DEBT_ORDER_DETAIL)
class DebtOrderDetailActivity : VbBaseActivity<DebtOrderDetailViewModel, ActivityDebtOrderDetailBinding>(), OnClickListener {
    val colors = intArrayOf(com.rt.base.R.color.color_fff70f0f, com.rt.base.R.color.color_fff70f0f)
    val sizes = intArrayOf(24, 16)
    val colors2 = intArrayOf(com.rt.base.R.color.color_ff666666, com.rt.base.R.color.color_ff1a1a1a)
    val sizes2 = intArrayOf(19, 19)
    var paymentQrDialog: PaymentQrDialog? = null
    var qr = ""
    var tradeNo = ""
    var debtCollectionBean: DebtCollectionBean? = null
    var token = ""
    var count = 0
    var handler = Handler(Looper.getMainLooper())
    val print = BluePrint(this)

    @SuppressLint("NewApi")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.欠费订单详情)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_video)
        binding.layoutToolbar.ivRight.show()

        debtCollectionBean = intent.getParcelableExtra(ARouterMap.DEBT_ORDER) as? DebtCollectionBean

        binding.tvPlate.text = debtCollectionBean!!.carLicense
        val strings1 = arrayOf("${AppUtil.keepNDecimal(debtCollectionBean!!.oweMoney / 100.00, 2)}", "元")
        binding.tvArrearsAmount.text = AppUtil.getSpan(strings1, sizes, colors)
        val strings2 = arrayOf(i18N(com.rt.base.R.string.订单) + "：", debtCollectionBean!!.orderNo)
        binding.tvOrderNo.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18N(com.rt.base.R.string.泊位) + "：", debtCollectionBean!!.parkingNo)
        binding.tvBerth.text = AppUtil.getSpan(strings3, sizes2, colors2)
        val strings4 = arrayOf(i18N(com.rt.base.R.string.路段) + "：", debtCollectionBean!!.streetName)
        binding.tvStreet.text = AppUtil.getSpan(strings4, sizes2, colors2)
        val strings5 = arrayOf(i18N(com.rt.base.R.string.入场) + "：", debtCollectionBean!!.startTime)
        binding.tvStartTime.text = AppUtil.getSpan(strings5, sizes2, colors2)
        val strings6 = arrayOf(i18N(com.rt.base.R.string.出场) + "：", debtCollectionBean!!.endTime)
        binding.tvEndTime.text = AppUtil.getSpan(strings6, sizes2, colors2)

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rflPay.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.VIDEO_PIC).withString(ARouterMap.VIDEO_PIC_ORDER_NO, debtCollectionBean!!.orderNo)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_pay -> {
                showProgressDialog(20000)
                runBlocking {
                    token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["token"] = token
                    jsonobject["oweOrderId"] = debtCollectionBean!!.oweOrderId
                    jsonobject["oweMoney"] = debtCollectionBean!!.oweMoney
                    jsonobject["orderNo"] = debtCollectionBean!!.orderNo
                    param["attr"] = jsonobject
                    mViewModel.debtPay(param)
                }
            }
        }
    }

    fun checkPayResult() {
//        TODO("测试入参")
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["token"] = "746a8c3eeb974709b9075b4cfc139977"
//            token
        jsonobject["tradeNo"] = "20230831JAZ03850133112"
//            tradeNo
        param["attr"] = jsonobject
        mViewModel.payResult(param)
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

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            debtPayLiveData.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                tradeNo = it.tradeNo
                qr = it.payUrl
                paymentQrDialog = PaymentQrDialog(qr)
                paymentQrDialog?.show()
                paymentQrDialog?.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(p0: DialogInterface?) {
                        handler.removeCallbacks(runnable)
                    }
                })
                count = 0
                handler.post(runnable)
            }
            payResultLiveData.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                handler.removeCallbacks(runnable)
                ToastUtil.showToast(i18N(com.rt.base.R.string.支付成功))
                startPrint(it)
            }
            errMsg.observe(this@DebtOrderDetailActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
            }
        }
    }

    fun startPrint(it: PayResultBean) {
        val payMoney = it.payMoney
        val printInfo = PrintInfoBean(
            roadId = it.roadName,
            plateId = it.carLicense,
            payMoney = String.format("%.2f", payMoney.toFloat()),
            orderId = debtCollectionBean!!.orderNo,
            phone = it.phone,
            startTime = it.startTime,
            leftTime = it.endTime,
            remark = it.remark,
            company = it.businessCname,
            oweCount = 0
        )
        Thread {
            print.zkblueprint(printInfo.toString())
        }.start()
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityDebtOrderDetailBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<DebtOrderDetailViewModel> {
        return DebtOrderDetailViewModel::class.java
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}