package com.rt.ipms_video.ui.activity.order

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.OrderBean
import com.rt.base.ext.i18N
import com.rt.base.ext.i18n
import com.rt.base.ext.show
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.AppUtil
import com.rt.common.util.BigDecimalManager
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityOrderDetailBinding
import com.rt.ipms_video.mvvm.viewmodel.OrderDetailViewModel
import com.zrq.spanbuilder.TextStyle

@Route(path = ARouterMap.ORDER_DETAIL)
class OrderDetailActivity : VbBaseActivity<OrderDetailViewModel, ActivityOrderDetailBinding>(), OnClickListener {
    val colors = intArrayOf(com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4)
    val colors1 = intArrayOf(com.rt.base.R.color.color_ffe92404, com.rt.base.R.color.color_ffe92404, com.rt.base.R.color.color_ffe92404)
    val colors2 = intArrayOf(com.rt.base.R.color.color_ff666666, com.rt.base.R.color.color_ff1a1a1a)
    val sizes = intArrayOf(16, 24, 16)
    val sizes2 = intArrayOf(19, 19)
    var order: OrderBean? = null
    val styles = arrayOf(TextStyle.NORMAL, TextStyle.BOLD, TextStyle.NORMAL)

    @SuppressLint("NewApi")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.订单详细信息)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_video)
        binding.layoutToolbar.ivRight.show()

        order = intent.getParcelableExtra(ARouterMap.ORDER, OrderBean::class.java)
        binding.tvPlate.text = order?.carLicense
        when (order?.amount?.toDouble() == order?.paidAmount?.toDouble()) {
            true -> {
                val strings = arrayOf(i18n(com.rt.base.R.string.已付), order?.paidAmount.toString(), i18n(com.rt.base.R.string.元))
                binding.tvPayment.text = AppUtil.getSpan(strings, sizes, colors, styles)
                binding.rtvTransactionRecord.delegate.setBackgroundColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.rt.base.R.color.color_ff0371f4
                    )
                )
            }

            false -> {
                val strings = arrayOf(
                    i18n(com.rt.base.R.string.欠),
                    BigDecimalManager.subtractionDoubleToString(order?.amount!!.toDouble(), order?.paidAmount!!.toDouble()),
                    i18n(com.rt.base.R.string.元)
                )
                binding.tvPayment.text = AppUtil.getSpan(strings, sizes, colors1, styles)
                binding.rtvTransactionRecord.delegate.setBackgroundColor(
                    ContextCompat.getColor(
                        BaseApplication.instance(),
                        com.rt.base.R.color.color_ffbac8d8
                    )
                )
            }
        }
        binding.rtvTransactionRecord.delegate.init()

        val strings1 = arrayOf(i18N(com.rt.base.R.string.订单) + ":", order?.orderNo.toString())
        binding.tvOrderNo.text = AppUtil.getSpan(strings1, sizes2, colors2)
        val strings2 = arrayOf(i18N(com.rt.base.R.string.泊位) + ":", order?.parkingNo.toString())
        binding.tvBerth.text = AppUtil.getSpan(strings2, sizes2, colors2)
        val strings3 = arrayOf(i18N(com.rt.base.R.string.路段) + ":", order?.streetName.toString())
        binding.tvStreet.text = AppUtil.getSpan(strings3, sizes2, colors2)
        val strings4 = arrayOf(i18N(com.rt.base.R.string.入场) + ":", order?.startTime.toString())
        binding.tvStartTime.text = AppUtil.getSpan(strings4, sizes2, colors2)
        val strings5 = arrayOf(i18N(com.rt.base.R.string.出场) + ":", order?.endTime.toString())
        binding.tvEndTime.text = AppUtil.getSpan(strings5, sizes2, colors2)
        val strings6 = arrayOf(i18N(com.rt.base.R.string.时长) + ":", mintoString(order?.duration!!.toInt()))
        binding.tvTotalTime.text = AppUtil.getSpan(strings6, sizes2, colors2)
        val strings7 = arrayOf(i18N(com.rt.base.R.string.总额) + ":", order?.amount.toString())
        binding.tvAmount.text = AppUtil.getSpan(strings7, sizes2, colors2)
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.rtvDebtCollection.setOnClickListener(this)
        binding.rtvTransactionRecord.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.VIDEO_PIC).withString(ARouterMap.VIDEO_PIC_ORDER_NO, order?.orderNo)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rtv_debtCollection -> {
                ARouter.getInstance().build(ARouterMap.DEBT_COLLECTION).withString(ARouterMap.DEBT_CAR_LICENSE, order?.carLicense)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rtv_transactionRecord -> {
                ARouter.getInstance().build(ARouterMap.TRANSACTION_RECORD)
                    .withString(ARouterMap.TRANSACTION_RECORD_ORDER_NO, order?.orderNo).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    fun mintoString(min: Int): String {
        val hour = min / 60
        val minute = min - hour * 60
        return "${hour}小时${minute}分钟"
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityOrderDetailBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<OrderDetailViewModel>? {
        return OrderDetailViewModel::class.java
    }
}