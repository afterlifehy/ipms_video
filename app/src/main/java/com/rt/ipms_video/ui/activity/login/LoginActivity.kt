package com.rt.ipms_video.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.LoginBean
import com.rt.base.bean.Street
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.realm.RealmUtil
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityLoginBinding
import com.rt.ipms_video.mvvm.viewmodel.LoginViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking


@Route(path = ARouterMap.LOGIN)
class LoginActivity : VbBaseActivity<LoginViewModel, ActivityLoginBinding>(), OnClickListener {
    var locationManager: LocationManager? = null
    var lat = 121.123212
    var lon = 31.434312

    @SuppressLint("CheckResult", "MissingPermission")
    override fun initView() {
        var rxPermissions = RxPermissions(this@LoginActivity)
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
            if (it) {
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val provider = LocationManager.NETWORK_PROVIDER
                locationManager?.requestLocationUpdates(provider, 1000, 1f, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        lat = location.latitude
                        lon = location.longitude
                    }
                })
            }
        }
    }

    override fun initListener() {
        binding.tvForgetPw.setOnClickListener(this)
        binding.rtvLogin.setOnClickListener(this)
    }

    override fun initData() {
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_forgetPw -> {

            }

            R.id.rtv_login -> {
                if (binding.etAccount.text.isEmpty()) {
                    ToastUtil.showToast(i18N(com.rt.base.R.string.请输入账号))
                    return
                }
                if (binding.etPw.text.isEmpty()) {
                    i18N(com.rt.base.R.string.请输入密码)
                    return
                }
                var rxPermissions = RxPermissions(this@LoginActivity)
                rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
                    if (it) {
                        if (locationManager == null) {
                            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val provider = LocationManager.NETWORK_PROVIDER
                            locationManager?.requestLocationUpdates(provider, 1000, 1f, object : LocationListener {
                                override fun onLocationChanged(location: Location) {
                                    lat = location.latitude
                                    lon = location.longitude
                                }
                            })
                        }
                        val param = HashMap<String, Any>()
                        val jsonobject = JSONObject()
                        jsonobject["loginName"] = binding.etAccount.text.toString()
                        jsonobject["password"] = binding.etPw.text.toString()
                        jsonobject["longitude"] = lon
                        jsonobject["latitude"] = lat
                        param["attr"] = jsonobject
                        mViewModel.login(param)
                    }
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            loginLiveData.observe(this@LoginActivity) {
                val streetList = it.result as ArrayList<Street>
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, it.token)
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, it.phone)
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, it.name)
                }
                RealmUtil.instance?.deleteAllStreet()
                RealmUtil.instance?.addRealmAsyncList(streetList)
                ARouter.getInstance().build(ARouterMap.STREET_CHOOSE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            errMsg.observe(this@LoginActivity) {
                ToastUtil.showToast(it.msg)
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