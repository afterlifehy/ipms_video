package com.rt.ipms_video.ui.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.BarUtils
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.i18N
import com.rt.base.help.ActivityCacheManager
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.AppUtil
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityMainBinding
import com.rt.ipms_video.mvvm.viewmodel.MainViewModel

@Route(path = ARouterMap.MAIN)
class MainActivity : VbBaseActivity<MainViewModel, ActivityMainBinding>(), OnClickListener {

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.ivHead.setOnClickListener(this)
        binding.llParkingLot.setOnClickListener(this)
        binding.flStayTuned.setOnClickListener(this)
        binding.flOrder.setOnClickListener(this)
        binding.flParkingAbnormal.setOnClickListener(this)
        binding.flLogout.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_head -> {

            }

            R.id.ll_parkingLot -> {

            }

            R.id.fl_stayTuned -> {

            }

            R.id.fl_order -> {

            }

            R.id.fl_parkingAbnormal -> {

            }

            R.id.fl_logout -> {

            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {

    }

    override fun providerVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override val isFullScreen: Boolean
        get() = false

    override fun onBackPressedSupport() {
        if (AppUtil.isFastClick(1000)) {
            ActivityCacheManager.instance().getAllActivity().forEach {
                if (!it.isFinishing) {
                    it.finish()
                }
            }
        } else {
            ToastUtil.showToast(i18N(com.rt.base.R.string.再按一次退出程序))
        }
    }
}