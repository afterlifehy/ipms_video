package com.rt.ipms_video.ui.activity.mine

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.i18n
import com.rt.base.help.ActivityCacheManager
import com.rt.base.viewbase.VbBaseActivity
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityLogOutBinding
import com.rt.ipms_video.mvvm.viewmodel.LogoutViewModel

@Route(path = ARouterMap.LOGOUT)
class LogoutActivity : VbBaseActivity<LogoutViewModel, ActivityLogOutBinding>(), OnClickListener {

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18n(com.rt.base.R.string.签退)
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.tvLogout.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.tv_logout -> {
                ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
//                for (i in ActivityCacheManager.instance().getAllActivity()) {
//                    i.finish()
//                }
                ActivityCacheManager.instance()?.getCurrentActivity()?.finish()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLogOutBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<LogoutViewModel>? {
        return LogoutViewModel::class.java
    }

}