package com.peakinfo.plateid.ui.activity.order

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.show
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityOrderMainBinding
import com.peakinfo.plateid.mvvm.viewmodel.OrderMainViewmodel

@Route(path = ARouterMap.ORDER_MAIN)
class OrderMainActivity : VbBaseActivity<OrderMainViewmodel, ActivityOrderMainBinding>(), OnClickListener {

    override fun initView() {
        binding.layoutToolbar.flBack.gone()
        binding.layoutToolbar.ivBackHome.show()
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.订单)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.baseApplication, com.peakinfo.base.R.color.white))
    }

    override fun initListener() {
        binding.layoutToolbar.ivBackHome.setOnClickListener(this)
        binding.rflTransactionQuery.setOnClickListener(this)
        binding.rflDebtCollect.setOnClickListener(this)
        binding.rflOrderInquiry.setOnClickListener(this)
        binding.rflCollectionManagement.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_backHome -> {
                onBackPressedSupport()
            }

            R.id.rfl_transactionQuery -> {
                ARouter.getInstance().build(ARouterMap.TRANSACTION_QUERY).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_debtCollect -> {
                ARouter.getInstance().build(ARouterMap.DEBT_COLLECTION).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_orderInquiry -> {
                ARouter.getInstance().build(ARouterMap.ORDER_INQUIRY).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.rfl_collectionManagement -> {
                ARouter.getInstance().build(ARouterMap.COLLECTION_MANAGEMENT).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityOrderMainBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<OrderMainViewmodel>? {
        return OrderMainViewmodel::class.java
    }

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

}