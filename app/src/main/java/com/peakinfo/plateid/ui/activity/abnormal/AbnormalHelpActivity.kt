package com.peakinfo.plateid.ui.activity.abnormal

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityAbnormalHelpBinding
import com.peakinfo.plateid.mvvm.viewmodel.AbnormalHelpViewModel

@Route(path = ARouterMap.ABNORMAL_HELP)
class AbnormalHelpActivity : VbBaseActivity<AbnormalHelpViewModel, ActivityAbnormalHelpBinding>(), OnClickListener {

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.异常上报使用帮助)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityAbnormalHelpBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<AbnormalHelpViewModel>? {
        return AbnormalHelpViewModel::class.java
    }
}