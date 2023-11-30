package com.peakinfo.base.viewbase

import android.view.View
import androidx.viewbinding.ViewBinding
import com.peakinfo.base.R
import com.peakinfo.base.base.mvvm.BaseViewModel

abstract class VbBaseActivity<VM : BaseViewModel, vb : ViewBinding> : BaseDataActivityKt<VM>() {
    lateinit var binding: vb

    override fun getBindingView(): View? {
        val mBindind = getVbBindingView()
        binding = mBindind as vb
        return mBindind.root
    }


    abstract fun getVbBindingView(): ViewBinding

    override fun getLayoutResId(): Int {
        return R.layout.activity_vb_default_layout
    }
}