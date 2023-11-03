package com.rt.ipms_video.ui.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BarUtils
import com.rt.base.viewbase.VbBaseActivity
import com.rt.ipms_video.databinding.ActivityMainBinding
import com.rt.ipms_video.mvvm.viewmodel.MainViewModel

class MainActivity : VbBaseActivity<MainViewModel, ActivityMainBinding>() {

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initView() {
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, com.rt.base.R.color.transparent))
//        BarUtils.addMarginTopEqualStatusBarHeight(binding.rlPerson)
//        BarUtils.addMarginTopEqualStatusBarHeight(binding.rlDetail)
        BarUtils.setNavBarVisibility(this@MainActivity, false)
    }

    override fun initListener() {

    }

    override fun initData() {
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
        get() = true

}