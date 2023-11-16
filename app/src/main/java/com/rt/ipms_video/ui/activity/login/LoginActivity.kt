package com.rt.ipms_video.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Location
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.rt.base.arouter.ARouterMap
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.LocationUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityLoginBinding
import com.rt.ipms_video.mvvm.viewmodel.LoginViewModel
import com.tbruyelle.rxpermissions3.RxPermissions

@Route(path = ARouterMap.LOGIN)
class LoginActivity : VbBaseActivity<LoginViewModel, ActivityLoginBinding>(), OnClickListener {

    @SuppressLint("CheckResult")
    override fun initView() {
    }

    override fun initListener() {
        binding.tvForgetPw.setOnClickListener(this)
        binding.rtvLogin.setOnClickListener(this)
    }

    override fun initData() {
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_forgetPw -> {

            }

            R.id.rtv_login -> {
                var rxPermissions = RxPermissions(this@LoginActivity)
                rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
                    if (it) {
                        val param = HashMap<String, Any>()
                        val jsonobject = JSONObject()
                        jsonobject["loginName"] = ""
                        jsonobject["password"] = ""
                        jsonobject["longitude"] = ""
                        jsonobject["latitude"] = ""
                        param["attr"] = jsonobject
                        mViewModel.login(param)
                        LocationUtils.getInstance(this)?.getLocation(object : LocationUtils.LocationCallBack {
                            override fun setLocation(location: Location?) {

                            }

                            override fun setAddress(address: Address?) {

                            }

                        })
                    } else {
                        onBackPressedSupport()
                    }
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            loginLiveData.observe(this@LoginActivity) {
                ARouter.getInstance().build(ARouterMap.STREET_CHOOSE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
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