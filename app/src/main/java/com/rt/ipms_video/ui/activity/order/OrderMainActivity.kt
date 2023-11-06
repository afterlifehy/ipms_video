package com.rt.ipms_video.ui.activity.order

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.SizeUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.i18N
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityOrderMainBinding
import com.rt.ipms_video.mvvm.viewmodel.OrderMainViewmodel

@Route(path = ARouterMap.ORDER_MAIN)
class OrderMainActivity : VbBaseActivity<OrderMainViewmodel, ActivityOrderMainBinding>(), OnClickListener {

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_home)
        val lp = binding.layoutToolbar.ivBack.layoutParams
        lp.width = SizeUtils.dp2px(25f)
        lp.height = SizeUtils.dp2px(25f)
        binding.layoutToolbar.ivBack.layoutParams = lp
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.订单)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.baseApplication, com.rt.base.R.color.white))
    }

    override fun initListener() {
        binding.layoutToolbar.ivBack.setOnClickListener(this)
        binding.rflTransactionQuery.setOnClickListener(this)
        binding.rflDebtCollect.setOnClickListener(this)
        binding.rflOrderInquiry.setOnClickListener(this)
        binding.rflCollectionManagement.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_transactionQuery -> {

            }

            R.id.rfl_debtCollect -> {

            }

            R.id.rfl_orderInquiry -> {

            }

            R.id.rfl_collectionManagement -> {

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