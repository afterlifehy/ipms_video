package com.rt.ipms_video.ui.activity.login

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.arouter.ARouterMap
import com.rt.base.viewbase.VbBaseActivity
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityLoginBinding
import com.rt.ipms_video.mvvm.viewmodel.LoginViewModel

@Route(path = ARouterMap.LOGIN)
class LoginActivity : VbBaseActivity<LoginViewModel, ActivityLoginBinding>(), OnClickListener {

    override fun initView() {

    }

    override fun initListener() {
        binding.tvForgetPw.setOnClickListener(this)
        binding.rtvLogin.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_forgetPw -> {

            }

            R.id.rtv_login -> {
                ARouter.getInstance().build(ARouterMap.MAIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<LoginViewModel>? {
        return LoginViewModel::class.java
    }

    override val isFullScreen: Boolean
        get() = false

}