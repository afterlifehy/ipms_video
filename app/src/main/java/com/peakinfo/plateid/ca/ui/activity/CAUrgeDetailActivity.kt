package com.peakinfo.plateid.ca.ui.activity

import android.view.View
import androidx.viewbinding.ViewBinding
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.plateid.databinding.ActivityUrgeDetailBinding
import com.peakinfo.plateid.mvvm.viewmodel.CAUrgeDetailViewModel

class CAUrgeDetailActivity : VbBaseActivity<CAUrgeDetailViewModel, ActivityUrgeDetailBinding>() {

    override fun initView() {
    }

    override fun initListener() {
    }

    override fun initData() {
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityUrgeDetailBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

    override val isFullScreen: Boolean
        get() = true

}